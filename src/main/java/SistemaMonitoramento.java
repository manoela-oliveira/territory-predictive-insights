import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.SensorFilaEspera;
import dominio.SensorOcupacaoSalas;
import dominio.SensorTempoAtendimento;
import dominio.UnidadeSaude;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;
import servico.ServicoMonitoramento;

/*
Ponto de entrada da aplicacao.
Inicializa o Spring Boot para gerenciamento de repositorios JPA e executa o loop do menu interativo de console.
*/
@SpringBootApplication(scanBasePackages = {"servico", "persistencia", "dominio"})
@EnableJpaRepositories(basePackages = "persistencia")
@EntityScan(basePackages = "dominio")
public class SistemaMonitoramento implements CommandLineRunner {

    private final ServicoMonitoramento servicoMonitoramento;
    private final ConfiguracaoSistema config;
    private final UnidadeSaude ubsCentral;

    public SistemaMonitoramento(ServicoMonitoramento servicoMonitoramento) {
        this.servicoMonitoramento = servicoMonitoramento;
        this.config = ConfiguracaoSistema.getInstancia();

        this.ubsCentral = new UnidadeSaude(
                1L,
                "UBS Jardim Esperanca - Dr. Paulo Freire",
                "Setor Leste - Periferia Urbana",
                "OPERACIONAL",
                8,
                5
        );

        this.servicoMonitoramento.registrarSensor(new SensorFilaEspera("SENSOR-FILA-01"));
        this.servicoMonitoramento.registrarSensor(new SensorTempoAtendimento("SENSOR-TEMPO-01"));
        this.servicoMonitoramento.registrarSensor(new SensorOcupacaoSalas("SENSOR-LEITOS-01"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SistemaMonitoramento.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        System.out.println("\n+==================================================================+");
        System.out.println("|  TERRITORY PREDICTIVE INSIGHTS - CitizenTech (Saude Publica)     |");
        System.out.println("|  Equipamento: " + String.format("%-50s", ubsCentral.getNome()) + " |");
        System.out.println("|  Regiao:      " + String.format("%-50s", ubsCentral.getRegiao()) + " |");
        System.out.println("+==================================================================+");

        while (opcao != 0) {
            exibirMenu();
            try {
                System.out.print("Digite o numero da opcao desejada: ");
                String entrada = scanner.nextLine().trim();
                opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1:
                        executarColetaSensores();
                        break;
                    case 2:
                        executarPrevisaoMenu(scanner);
                        break;
                    case 3:
                        consultarHistoricoMedicoes(scanner);
                        break;
                    case 4:
                        consultarHistoricoAlertas();
                        break;
                    case 5:
                        gerenciarLimiaresTerritorio(scanner);
                        break;
                    case 6:
                        exibirStatusTerritorial();
                        break;
                    case 0:
                        System.out.println("\n[SISTEMA] Encerrando plataforma de monitoramento territorial. Ate logo!");
                        break;
                    default:
                        System.out.println("\n[AVISO] Opcao invalida. Por favor, escolha um numero de 0 a 6.");
                }
            } catch (NumberFormatException e) {
                System.err.println("\n[ERRO DE ENTRADA] Entrada invalida. Digite apenas numeros inteiros (ex: 1, 2, 3).");
            } catch (DadosInvalidosException e) {
                System.err.println("\n[REGRA DE NEGOCIO] " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.err.println("\n[FALHA DE COMUNICACAO] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("\n[ERRO] Ocorreu uma falha no processamento: " + e.getMessage());
            }

            if (opcao != 0) {
                System.out.println("\nPressione [ENTER] para voltar ao menu principal...");
                scanner.nextLine();
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n======================== MENU DE OPERACOES ========================");
        System.out.println("  1. Coletar Novas Leituras    (Le os sensores e salva no histórico)");
        System.out.println("  2. Gerar Previsao de Risco   (Calcula tendencia e emite alertas)");
        System.out.println("  3. Ver Historico de Leituras (Consulta dados brutos de um sensor)");
        System.out.println("  4. Consultar Alertas Ativos  (Lista alertas gerados pelas previsoes)");
        System.out.println("  5. Ajustar Limiares da UBS   (Configura limites de fila, tempo e vagas)");
        System.out.println("  6. Diagnostico Operacional   (Exibe capacidade da equipe e salas)");
        System.out.println("  0. Sair do Sistema");
        System.out.println("====================================================================");
    }

    private void executarColetaSensores() {
        System.out.println("\n[INICIANDO COLETA] Interrogando telemetria dos sensores da UBS...");
        List<Medicao> medicoes = servicoMonitoramento.coletarMedicoes();

        System.out.println("\n+------------------------------------------------------------------+");
        System.out.println("| SUCESSO: Leituras registradas e salvas no banco de dados:       |");
        System.out.println("+------------------------------------------------------------------+");
        for (Medicao m : medicoes) {
            System.out.println(String.format("  * [%-18s] Valor Coletado: %-6.2f | Gravado em: %s",
                    m.getTipoSensor(), m.getValor(), m.getDataHora().toLocalTime()));
        }
    }

    private void executarPrevisaoMenu(Scanner scanner) {
        System.out.println("\n[PASSO 1/2] Selecione qual indicador da UBS deseja projetar:");
        System.out.println("  1. Fila de Espera na Triagem   (Contagem de pacientes aguardando)");
        System.out.println("  2. Tempo Medio de Atendimento  (Duracao media das consultas em min)");
        System.out.println("  3. Taxa de Ocupacao de Leitos  (Percentual de ocupacao das salas)");
        System.out.print("Escolha o indicador (1, 2 ou 3): ");
        String opSensor = scanner.nextLine().trim();

        String tipoSensor;
        String unidadeMedida;
        if ("1".equals(opSensor)) {
            tipoSensor = "FILA_ESPERA";
            unidadeMedida = "pessoas aguardando";
        } else if ("2".equals(opSensor)) {
            tipoSensor = "TEMPO_ATENDIMENTO";
            unidadeMedida = "minutos por consulta";
        } else if ("3".equals(opSensor)) {
            tipoSensor = "OCUPACAO_SALAS";
            unidadeMedida = "% de ocupacao";
        } else {
            throw new DadosInvalidosException("Indicador invalido. Escolha 1, 2 ou 3.");
        }

        System.out.println("\n[PASSO 2/2] Selecione o modelo matematico de previsao (Factory Pattern):");
        System.out.println("  A. Media Movel Simples              (Suaviza variacoes para tendencia estavel)");
        System.out.println("  B. Comparacao com Periodo Anterior  (Detecta surtos/aceleracao de demanda)");
        System.out.print("Escolha o modelo (A ou B): ");
        String opEstrat = scanner.nextLine().trim().toUpperCase();

        String chaveFabrica = "B".equals(opEstrat) ? "COMPARACAO" : "MEDIA_MOVEL";
        EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(chaveFabrica);

        System.out.println("\n[PROCESSANDO] Executando algoritmo: " + estrategia.getNomeEstrategia() + "...");
        double valorPrevisto = servicoMonitoramento.executarPrevisao(tipoSensor, estrategia);

        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("|                     RESULTADO DA PROJECAO                        |");
        System.out.println("--------------------------------------------------------------------");
        System.out.println(String.format("  Indicador Analisado: %s", tipoSensor));
        System.out.println(String.format("  Projecao Estimada:   %.2f %s", valorPrevisto, unidadeMedida));
        System.out.println("--------------------------------------------------------------------");

        Alerta alerta = servicoMonitoramento.analisarRiscoEEmitirAlerta(tipoSensor, valorPrevisto);
        if (alerta != null) {
            System.out.println("\n[ALERTA AUTOMATICO EMITIDO E REGISTRADO NO BANCO]");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("  GRAVIDADE:    " + alerta.getNivel());
            System.out.println("  LIMITE VIOLADO: " + alerta.getLimiarViolado());
            System.out.println("  JUSTIFICATIVA: " + alerta.getJustificativa());
            System.out.println("--------------------------------------------------------------------");
        } else {
            System.out.println("\n[SITUACAO CONTROLADA] O valor previsto esta dentro dos limites seguros da unidade.");
        }
    }

    private void consultarHistoricoMedicoes(Scanner scanner) {
        System.out.println("\nEscolha o sensor para consultar o historico bruto:");
        System.out.println("  1. Fila de Espera (FILA_ESPERA)");
        System.out.println("  2. Tempo de Atendimento (TEMPO_ATENDIMENTO)");
        System.out.println("  3. Ocupacao de Leitos (OCUPACAO_SALAS)");
        System.out.print("Opcao (1, 2 ou 3): ");
        String op = scanner.nextLine().trim();

        String tipoSensor = "FILA_ESPERA";
        if ("2".equals(op)) tipoSensor = "TEMPO_ATENDIMENTO";
        else if ("3".equals(op)) tipoSensor = "OCUPACAO_SALAS";

        List<Medicao> historico = servicoMonitoramento.listarHistoricoPorSensor(tipoSensor);
        if (historico.isEmpty()) {
            System.out.println("\n[INFO] Nenhuma medicao gravada para " + tipoSensor + ".");
            System.out.println("Dica: Utilize a Opcao 1 no menu para coletar dados dos sensores primeiro.");
            return;
        }

        System.out.println(String.format("\n--- Historico de Leituras Salvas [%s] (Total: %d) ---", tipoSensor, historico.size()));
        for (Medicao m : historico) {
            System.out.println(String.format("  ID #%-3d | Data/Hora: %s | Valor: %6.2f",
                    m.getId(), m.getDataHora().toString().replace("T", " "), m.getValor()));
        }
    }

    private void consultarHistoricoAlertas() {
        List<Alerta> alertas = servicoMonitoramento.listarAlertas();
        if (alertas.isEmpty()) {
            System.out.println("\n[AUDITORIA DE ALERTAS]");
            System.out.println("Nenhum alerta de risco foi registrado ate o momento.");
            System.out.println("Dica: Alertas sao criados automaticamente pela Opcao 2 quando a previsao estoura os limites.");
            return;
        }

        System.out.println(String.format("\n================ AUDITORIA DE ALERTAS EMITIDOS (%d) ================", alertas.size()));
        for (Alerta a : alertas) {
            System.out.println(String.format("[%s] Sensor: %-18s | Previsto: %-5.1f | Limite: %-5.1f",
                    a.getNivel(), a.getTipoSensor(), a.getValorPrevisto(), a.getLimiarViolado()));
            System.out.println("  Motivo: " + a.getJustificativa());
            System.out.println("  Data/Hora do Registro: " + a.getDataHora().toString().replace("T", " "));
            System.out.println("--------------------------------------------------------------------");
        }
    }

    private void gerenciarLimiaresTerritorio(Scanner scanner) {
        System.out.println("\n================ CONFIGURACAO DE TOLERANCIA (SINGLETON) ================");
        System.out.println("Limiares Atuais de Fila de Espera:");
        System.out.println("  - Nivel ATENCAO: " + config.getLimiarAtencaoFila() + " pessoas");
        System.out.println("  - Nivel ALERTA:  " + config.getLimiarAlertaFila() + " pessoas");
        System.out.println("  - Nivel CRITICO: " + config.getLimiarCriticoFila() + " pessoas");
        System.out.println("========================================================================");

        System.out.print("Deseja modificar os limiares de seguranca da fila? (S/N): ");
        String resp = scanner.nextLine().trim().toUpperCase();
        if ("S".equals(resp)) {
            System.out.print("Informe o novo limite para ATENCAO (ex: 20): ");
            double atencao = Double.parseDouble(scanner.nextLine());

            System.out.print("Informe o novo limite para ALERTA (ex: 35): ");
            double alerta = Double.parseDouble(scanner.nextLine());

            System.out.print("Informe o novo limite para CRITICO (ex: 50): ");
            double critico = Double.parseDouble(scanner.nextLine());

            config.configurarLimiaresFila(atencao, alerta, critico);
            System.out.println("\n[SUCESSO] Limiares atualizados na instancia Singleton da aplicacao.");
        } else {
            System.out.println("[INFO] Limiares mantidos sem alteracao.");
        }
    }

    private void exibirStatusTerritorial() {
        System.out.println("\n================= DIAGNOSTICO OPERACIONAL DA UBS ==================");
        System.out.println("  Equipamento Publico:    " + ubsCentral.getNome());
        System.out.println("  Regiao de Cobertura:    " + ubsCentral.getRegiao());
        System.out.println("  Total de Consultorios:  " + ubsCentral.getCapacidadeConsultorios());
        System.out.println("  Profissionais em Turno: " + ubsCentral.getProfissionaisDisponiveis());
        System.out.println("  Status de Capacidade:   " + ubsCentral.verificarStatus());
        System.out.println("====================================================================");
    }
}