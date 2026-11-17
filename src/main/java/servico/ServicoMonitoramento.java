package servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Sensor;
import excecao.DadosInvalidosException;
import persistencia.AlertaRepository;
import persistencia.MedicaoRepository;

// Serviço orquestrador que coordena a leitura de sensores, gravação do histórico, execução das previsões polimórficas e emissão de alertas.
@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracaoSistema config;
    private final List<Sensor> sensoresRegistrados;

    @Autowired
    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.config = ConfiguracaoSistema.getInstancia();
        this.sensoresRegistrados = new ArrayList<>();
    }

    // Registra um sensor físico ou virtual na rede de monitoramento.
    public void registrarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new DadosInvalidosException("Sensor não pode ser nulo.");
        }
        this.sensoresRegistrados.add(sensor);
    }

    /*
    Realiza a leitura de todos os sensores cadastrados, valida os dados e grava as medições válidas no banco de dados.
    @return lista de medições gravadas com sucesso.
    */
    public List<Medicao> coletarMedicoes() {
        List<Medicao> medicoesSalvas = new ArrayList<>();

        for (Sensor sensor : sensoresRegistrados) {
            // Pode lançar FonteIndisponivelException se o sensor estiver fora do ar
            double valorLido = sensor.lerValor();

            // Validação de domínio: medição negativa é inconsistente
            if (valorLido < 0) {
                throw new DadosInvalidosException("medicao", valorLido, "Leitura de sensor não pode ser negativa.");
            }

            Medicao medicao = new Medicao(sensor.retornarTipo(), valorLido);
            Medicao salva = medicaoRepository.save(medicao);
            medicoesSalvas.add(salva);
        }

        return medicoesSalvas;
    }

    /*
    Executa a estratégia preditiva informada sobre o histórico de um determinado sensor.
    @param tipoSensor tipo do sensor no histórico.
    @param estrategia objeto polimórfico de EstrategiaPrevisao.
    @return valor projetado para o próximo período.
    */
    public double executarPrevisao(String tipoSensor, EstrategiaPrevisao estrategia) {
        if (estrategia == null) {
            throw new DadosInvalidosException("Estratégia de previsão não informada.");
        }

        List<Medicao> historico = medicaoRepository.findByTipoSensorOrderByDataHoraDesc(tipoSensor);

        if (historico.size() < config.getLeiturasMinimasParaPrevisao()) {
            throw new DadosInvalidosException(String.format(
                    "São necessárias no mínimo %d leituras para o sensor '%s', mas há apenas %d no banco.",
                    config.getLeiturasMinimasParaPrevisao(), tipoSensor, historico.size()
            ));
        }

        // Execução polimórfica da previsão
        return estrategia.prever(historico);
    }

    // Analisa o valor previsto para um sensor e emite alertas automáticos caso os limiares de segurança da UBS sejam violados.
    public Alerta analisarRiscoEEmitirAlerta(String tipoSensor, double valorPrevisto) {
        if (tipoSensor == null || tipoSensor.trim().isEmpty()) {
            throw new DadosInvalidosException("Tipo do sensor não informado para análise de risco.");
        }

        Alerta alertaGerado = null;

        // Regras de negócio por tipo de métrica do equipamento de saúde
        if ("FILA_ESPERA".equalsIgnoreCase(tipoSensor)) {
            if (valorPrevisto >= config.getLimiarCriticoFila()) {
                String motivo = String.format("Fila projetada em %.0f pessoas (Limite Crítico: %.0f). Risco iminente de tumulto e desassistência.",
                        valorPrevisto, config.getLimiarCriticoFila());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarCriticoFila(), NivelAlerta.CRITICO, motivo);

            } else if (valorPrevisto >= config.getLimiarAlertaFila()) {
                String motivo = String.format("Fila projetada em %.0f pessoas (Limite Alerta: %.0f). Necessário reforço na triagem.",
                        valorPrevisto, config.getLimiarAlertaFila());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarAlertaFila(), NivelAlerta.ALERTA, motivo);

            } else if (valorPrevisto >= config.getLimiarAtencaoFila()) {
                String motivo = String.format("Fila projetada em %.0f pessoas (Limite Atenção: %.0f). Tendência de crescimento na procura.",
                        valorPrevisto, config.getLimiarAtencaoFila());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarAtencaoFila(), NivelAlerta.ATENCAO, motivo);
            }

        } else if ("TEMPO_ATENDIMENTO".equalsIgnoreCase(tipoSensor)) {
            if (valorPrevisto >= config.getLimiarCriticoTempo()) {
                String motivo = String.format("Tempo médio previsto de %.1f min por consulta (Crítico: %.1f min). Risco de atrasos em cascata.",
                        valorPrevisto, config.getLimiarCriticoTempo());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarCriticoTempo(), NivelAlerta.CRITICO, motivo);

            } else if (valorPrevisto >= config.getLimiarAtencaoTempo()) {
                String motivo = String.format("Tempo médio previsto de %.1f min por consulta (Atenção: %.1f min). Sobrecarga na equipe médica.",
                        valorPrevisto, config.getLimiarAtencaoTempo());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarAtencaoTempo(), NivelAlerta.ATENCAO, motivo);
            }

        } else if ("OCUPACAO_SALAS".equalsIgnoreCase(tipoSensor)) {
            if (valorPrevisto >= config.getLimiarCriticoOcupacao()) {
                String motivo = String.format("Taxa de ocupação de leitos/salas em %.1f%% (Crítico: %.1f%%). Esgotamento iminente de vagas.",
                        valorPrevisto, config.getLimiarCriticoOcupacao());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarCriticoOcupacao(), NivelAlerta.CRITICO, motivo);

            } else if (valorPrevisto >= config.getLimiarAtencaoOcupacao()) {
                String motivo = String.format("Taxa de ocupação de leitos/salas em %.1f%% (Atenção: %.1f%%). Recomenda-se acionar leitos de retaguarda.",
                        valorPrevisto, config.getLimiarAtencaoOcupacao());
                alertaGerado = new Alerta(tipoSensor, valorPrevisto, config.getLimiarAtencaoOcupacao(), NivelAlerta.ATENCAO, motivo);
            }
        }

        // Se algum limiar foi estourado, salva no histórico de alertas
        if (alertaGerado != null) {
            return alertaRepository.save(alertaGerado);
        }

        return null; // Operação dentro da normalidade
    }

    public List<Medicao> listarHistoricoPorSensor(String tipoSensor) {
        return medicaoRepository.findByTipoSensorOrderByDataHoraDesc(tipoSensor);
    }

    public List<Alerta> listarAlertas() {
        return alertaRepository.findAllByOrderByDataHoraDesc();
    }

    public List<Sensor> getSensoresRegistrados() {
        return sensoresRegistrados;
    }
}