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

        // Equipamento publico do territorio monitorado (UBS Central da Comunidade)
        this.ubsCentral = new UnidadeSaude(
                1L,
                "UBS Jardim Esperanca - Dr. Paulo Freire",
                "Setor Leste - Periferia Urbana",
                "OPERACIONAL",
                8, // 8 consultorios
                5  // 5 profissionais presentes
        );

        // Registro dos 3 sensores coerentes com o recorte
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

        System.out.println("\n==================================================================");
        System.out.println("  TERRITORY PREDICTIVE INSIGHTS - CitizenTech (Saude Publica)     ");
        System.out.println("  Equipamento: " + ubsCentral.getNome());
        System.out.println("  Regiao: " + ubsCentral.getRegiao());
        System.out.println("==================================================================");

        while (opcao != 0) {
            exibirMenu();
            try {
                System.out.print("Selecione uma opcao: ");
                String entrada = scanner.nextLine();
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
                        System.out.println("\n[AVISO] Opcao invalida. Digite um numero de 0 a 6.");
                }
            } catch (NumberFormatException e) {
                System.err.println("\n[ERRO DE ENTRADA] Por favor, digite apenas numeros inteiros.");
            } catch (DadosInvalidosException e) {
                System.err.println("\n[ERRO DE VALIDACAO] " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.err.println("\n[FALHA DE SENSOR/TELEMETRIA] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("\n[ERRO INESPERADO] Falha no processamento: " + e.getMessage());
            }

            if (opcao != 0) {
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n-------------------- MENU PRINCIPAL --------------------");
        System.out.println("1. Coletar leituras dos Sensores (Telemetria)");
        System.out.println("2. Rodar Previsoes Preditivas e Analise de Risco");
        System.out.println("3. Consultar Historico de Medicoes por Sensor");
        System.out.println("4. Consultar Alertas ja Emitidos (Auditoria)");
        System.out.println("5. Gerenciar Limiares de Alerta do Territorio");
        System.out.println("6. Exibir Status Geral da Unidade de Saude");
        System.out.println("0. Sair");
        System.out.println("--------------------------------------------------------");
    }

    private void executarColetaSensores() {
        System.out.println("\n>>> [COLETA] Interrogando sensores do territorio...");
        List<Medicao> medicoes = servicoMonitoramento.coletarMedicoes();
        System.out.println("[SUCESSO] " + medicoes.size() + " medicoes coletadas e gravadas no banco de dados:");
        for (Medicao m : medicoes) {
            System.out.println("   * " + m);
        }
    }

    private void executarPrevisaoMenu(Scanner scanner) {
        System.out.println("\n>>> [PREVISAO] Escolha a metrica para projetar:");
        System.out.println("1. Fila de Espera na Triagem (Pessoas)");
        System.out.println("2. Tempo Medio de Atendimento (Minutos)");
        System.out.println("3. Taxa de Ocupacao de Leitos/Salas (%)");
        System.out.print("Escolha o sensor (1-3): ");
        String opSensor = scanner.nextLine();

        String tipoSensor;
        if ("1".equals(opSensor)) tipoSensor = "FILA_ESPERA";
        else if ("2".equals(opSensor)) tipoSensor = "TEMPO_ATENDIMENTO";
        else if ("3".equals(opSensor)) tipoSensor = "OCUPACAO_SALAS";
        else throw new DadosInvalidosException("Sensor invalido.");

        System.out.println("\nEscolha a estrategia via Factory Pattern:");
        System.out.println("A. Media Movel Simples (Suavizacao de tendencia)");
        System.out.println("B. Comparacao com Periodo Anterior (Deteccao de picos/surtos)");
        System.out.print("Opcao (A/B): ");
        String opEstrat = scanner.nextLine().trim().toUpperCase();

        String chaveFabrica = "B".equals(opEstrat) ? "COMPARACAO" : "MEDIA_MOVEL";
        EstrategiaPrevisao estrategia = FabricaPrevisao.criarEstrategia(chaveFabrica);

        System.out.println("\n[PROCESSANDO] Executando estrategia: " + estrategia.getNomeEstrategia());
        double valorPrevisto = servicoMonitoramento.executarPrevisao(tipoSensor, estrategia);

        System.out.println(String.format("[RESULTADO DA PREVISAO] %.2f (%s)", valorPrevisto, tipoSensor));

        // Avaliacao de risco automatica
        Alerta alerta = servicoMonitoramento.analisarRiscoEEmitirAlerta(tipoSensor, valorPrevisto);
        if (alerta != null) {
            System.err.println("\n[ALERTA DE SEGURANCA] UM LIMIAR FOI VIOLADO:");
            System.err.println("   " + alerta);
        } else {
            System.out.println("[OK] Previsao dentro dos parametros operacionais seguros da UBS.");
        }
    }

    private void consultarHistoricoMedicoes(Scanner scanner) {
        System.out.print("\nDigite o sensor (FILA_ESPERA / TEMPO_ATENDIMENTO / OCUPACAO_SALAS): ");
        String tipoSensor = scanner.nextLine().trim().toUpperCase();

        List<Medicao> historico = servicoMonitoramento.listarHistoricoPorSensor(tipoSensor);
        if (historico.isEmpty()) {
            System.out.println("Nenhuma medicao encontrada para o sensor: " + tipoSensor);
            return;
        }

        System.out.println("\n--- Historico de Medicoes (" + tipoSensor + ") - Total: " + historico.size() + " ---");
        for (Medicao m : historico) {
            System.out.println(" " + m);
        }
    }

    private void consultarHistoricoAlertas() {
        List<Alerta> alertas = servicoMonitoramento.listarAlertas();
        if (alertas.isEmpty()) {
            System.out.println("\n[INFO] Nenhum alerta de risco registrado ate o momento. Operacao normal.");
            return;
        }

        System.out.println("\n--- Alertas Emitidos pela Plataforma - Total: " + alertas.size() + " ---");
        for (Alerta a : alertas) {
            System.out.println(" " + a);
        }
    }

    private void gerenciarLimiaresTerritorio(Scanner scanner) {
        System.out.println("\n--- Configuracao de Limiares (Singleton: ConfiguracaoSistema) ---");
        System.out.println("Atuais -> Fila Atencao: " + config.getLimiarAtencaoFila() +
                " | Fila Alerta: " + config.getLimiarAlertaFila() +
                " | Fila Critico: " + config.getLimiarCriticoFila());

        System.out.print("Deseja alterar os limiares de fila de espera? (S/N): ");
        String resp = scanner.nextLine().trim().toUpperCase();
        if ("S".equals(resp)) {
            System.out.print("Novo limiar de ATENCAO (ex: 20): ");
            double atencao = Double.parseDouble(scanner.nextLine());

            System.out.print("Novo limiar de ALERTA (ex: 35): ");
            double alerta = Double.parseDouble(scanner.nextLine());

            System.out.print("Novo limiar de CRITICO (ex: 50): ");
            double critico = Double.parseDouble(scanner.nextLine());

            config.configurarLimiaresFila(atencao, alerta, critico);
            System.out.println("[SUCESSO] Novos limiares configurados no Singleton!");
        }
    }

    private void exibirStatusTerritorial() {
        System.out.println("\n--- Status Operacional do Equipamento Territorial ---");
        System.out.println("Nome: " + ubsCentral.getNome());
        System.out.println("Regiao: " + ubsCentral.getRegiao());
        System.out.println("Consultorios: " + ubsCentral.getCapacidadeConsultorios());
        System.out.println("Equipe em Atendimento: " + ubsCentral.getProfissionaisDisponiveis());
        System.out.println("Diagnostico Polimorfico: " + ubsCentral.verificarStatus());
    }
}