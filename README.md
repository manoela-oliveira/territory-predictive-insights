# FIAP - Global Solution - Programação Orientada a Objeto
> **Professor:** Ygor Moraes Martins dos Anjos
---

Este projeto foi desenvolvido no âmbito da iniciativa **CitizenTech: Territory Predictive Insights**, tendo como foco central o recorte de **Demanda por serviços públicos**, especificamente monitorando e antecipando picos de atendimento em uma Unidade Básica de Saúde (UBS). A solução está estruturada sob o repositório `territory-predictive-insights`.

---

### Tecnologias Utilizadas
<p>
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=java,spring,maven,git" />
  </a>
</p>

* **Java 17 (LTS)**
* **Spring Boot 3.2.x** (Spring Data JPA)
* **Banco de Dados H2** (Configurado em modo arquivo local persistente)
* **JUnit 5** e **Mockito** (Testes unitários automatizados)
* **Maven** (Gerenciador de dependências e build)

---

### Pré-requisitos e Como Rodar no VS Code

#### 1. Pré-requisitos de Ambiente

- **Java Development Kit (JDK) 17** ou superior instalado e configurado na variável de ambiente `JAVA_HOME`.
- **Visual Studio Code** instalado.
- Extensão obrigatória no VS Code:
  - **Extension Pack for Java** (Microsoft) — inclui Language Support, Debugger, Maven for Java e Test Runner.
  - *(Opcional)* **Spring Boot Extension Pack** (VMware).

#### 2. Passo a Passo para Execução

1. Clone o repositório em seu ambiente local:

   ```bash
   git clone https://github.com/SEU-USUARIO/territory-predictive-insights.git
   ```

2. Abra a pasta raiz do projeto no VS Code (File > Open Folder...).

3. Aguarde o VS Code carregar as dependências do Maven (o indicador de sincronização aparecerá na barra inferior).

4. **Para rodar a aplicação (Menu de Console):**

   - Navegue até o arquivo src/main/java/SistemaMonitoramento.java.
   - Clique na opção **Run** que surge diretamente acima do método public static void main(String[] args).
   - Interaja com o sistema através do terminal integrado.

5. **Para rodar a suíte de testes unitários:**

   - Clique no ícone de **Testing** (frasco de laboratório) na barra lateral esquerda do VS Code.
   - Clique no botão **Play** no topo para rodar todas as classes de teste (MedicaoTest, PrevisaoMediaMovelTest, ServicoMonitoramentoTest).

#### 3. Resolução de Erros Comuns Enfrentados

* Incompatibilidade de Versão do Java (UnsupportedClassVersionError):
  * Causa: Tentar compilar ou rodar o projeto com uma versão legada do JDK (ex: Java 8 ou Java 11), enquanto o projeto exige Java 17.
  * Solução: Certifique-se de que o JDK 17 (ou superior) está instalado e configurado como padrão no terminal (java -version) e selecionado nas configurações da IDE em Java: Configure Java Runtime.

* Erro de Bloqueio do Arquivo de Banco de Dados (Database may be already in use):
  * Causa: O H2 em modo arquivo local (./data/territory_db) cria um lock de exclusividade para o processo em execução. Se uma instância do programa ficar rodando em segundo plano e outra for iniciada, o banco bloqueia a inicialização.
  * Solução: Interrompa todas as sessões anteriores no terminal do VS Code ou encerre o processo Java ativo antes de executar uma nova instância do SistemaMonitoramento.

* Maven não reconhecido no terminal (mvn is not recognized):
  * Causa: O binário do Maven não está instalado globalmente ou não foi adicionado à variável PATH do sistema operacional.
  * Solução: Utilize o comando de execução direta da IDE (Run/Debug na classe principal e Run Test na aba de testes) ou configure as variáveis de ambiente do Maven no sistema operacional.

* Dependências não baixadas ou projeto vermelho ao abrir pela primeira vez:
  * Causa: Falha de conexão inicial ou interrupção do download dos pacotes do Spring Data JPA / H2 pelo Maven no primeiro carregamento do VS Code.
  * Solução: No painel lateral do VS Code, localize a aba Maven, clique com o botão direito no projeto territory-predictive-insights e selecione Reload project (ou execute mvn clean install no terminal).

#### 4. Configuração do Banco de Dados para Correção

A aplicação foi configurada para utilizar o banco de dados **H2**, dispensando qualquer instalação prévia de servidores externos (como Oracle ou MySQL) para a avaliação do professor.

* **Modo de Operação:** O banco opera em modo arquivo local embutido (`./data/territory_db`), garantindo conformidade com o requisito obrigatório de **persistência do histórico entre reinicializações** da aplicação.
* **Criação Automática de Tabelas (DDL):** O Spring Data JPA / Hibernate está parametrizado com `spring.jpa.hibernate.ddl-auto=update`, gerando automaticamente as tabelas `tb_medicao` e `tb_alerta` na primeira inicialização, sem necessidade de execução manual de scripts SQL.
* **Limpeza e Reset:** Caso o avaliador queira reiniciar o banco com o histórico totalmente limpo, basta excluir a pasta `./data/` gerada na raiz do projeto antes de iniciar o sistema.

---

### Organização de Arquivos

Text

```
territory-predictive-insights/
├── data/                                 # Arquivo de dados do H2 (gerado em runtime)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── dominio/                  # Entidades, sensores e regras de POO pura
│   │   │   │   ├── Alerta.java
│   │   │   │   ├── ComponenteTerritorial.java
│   │   │   │   ├── DadosTerritorio.java
│   │   │   │   ├── EstrategiaPrevisao.java
│   │   │   │   ├── Medicao.java
│   │   │   │   ├── NivelAlerta.java
│   │   │   │   ├── PrevisaoComparacao.java
│   │   │   │   ├── PrevisaoMediaMovel.java
│   │   │   │   ├── Sensor.java
│   │   │   │   ├── SensorFilaEspera.java
│   │   │   │   ├── SensorOcupacaoSalas.java
│   │   │   │   ├── SensorTempoAtendimento.java
│   │   │   │   └── UnidadeSaude.java
│   │   │   ├── excecao/                  # Exceções customizadas de negócio
│   │   │   │   ├── DadosInvalidosException.java
│   │   │   │   └── FonteIndisponivelException.java
│   │   │   ├── persistencia/             # Repositórios Spring Data JPA
│   │   │   │   ├── AlertaRepository.java
│   │   │   │   └── MedicaoRepository.java
│   │   │   ├── servico/                  # Orquestração, Singleton e Factory
│   │   │   │   ├── ConfiguracaoSistema.java
│   │   │   │   ├── FabricaPrevisao.java
│   │   │   │   └── ServicoMonitoramento.java
│   │   │   └── SistemaMonitoramento.java # Classe executável e menu interativo
│   │   └── resources/
│   │       └── application.properties    # Configurações do H2 e do Hibernate
│   └── test/
│       └── java/                         # Bateria de testes unitários (AAA + Mockito)
│           ├── MedicaoTest.java
│           ├── PrevisaoMediaMovelTest.java
│           └── ServicoMonitoramentoTest.java
├── pom.xml                               # Gerenciamento de dependências Maven
└── README.md
```



## Detalhes da Atividade e Decisões de Projeto

### 1. Contexto e Público Beneficiado

* **Público Afetado:** Comunidade periférica dependente da Unidade Básica de Saúde (UBS Jardim Esperança) e a equipe de gestão local (coordenadores de enfermagem, médicos e triagem).
* **Decisão que o Sistema Apoia:** Dimensionamento prévio de equipe, abertura de salas de contingência e remanejamento de profissionais de saúde antes que a fila de triagem atinja níveis de desassistência ou colapso estrutural.

### 2. Justificativa dos Padrões Criacionais Adotados

* **Singleton (ConfiguracaoSistema):** Garante uma instância única global em memória para guardar os limiares operacionais da unidade (capacidade, tempos e tolerâncias). Possui construtor estritamente privado e controle de concorrência (synchronized) no ponto de acesso getInstancia(), impedindo estados divergentes de tolerância na aplicação.
* **Factory (FabricaPrevisao):** Centraliza a criação das estratégias preditivas (PrevisaoMediaMovel e PrevisaoComparacao). O código cliente apenas solicita a estratégia desejada por identificador textual, eliminando estruturas condicionais (if/else ou switch) espalhadas pela camada de serviço ou interface.

### 3. Reflexão Crítica (Obrigatória da GS)

* **Quando o sistema deve se calar?**
  O sistema preditivo não deve emitir projeções ou alertas quando não houver densidade histórica suficiente. No ServicoMonitoramento, o algoritmo exige um mínimo de leituras armazenadas no banco de dados para liberar a previsão; caso a amostragem seja inferior a esse limiar, o sistema se cala e lança DadosInvalidosException. Prever com base em 1 ou 2 leituras pontuais constitui ruído estatístico e induz a equipe a erros operacionais graves (pânico desnecessário ou falsa sensação de segurança).
* **O que os dados não representam?**
  A telemetria (sensores de catraca, tempo de prontuário e ocupação de leitos) captura o sintoma quantitativo, mas ignora completamente a complexidade qualitativa da comunidade. Os dados numéricos não revelam se o aumento de demanda decorre de um surto epidemiológico local (como dengue), da interrupção de atendimento em um bairro vizinho, de desabastecimento de remédios ou de condições climáticas adversas. Um sensor mede a quantidade de pessoas na sala, mas não expressa a gravidade clínica individual dos casos presentes.
* **Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?**
  A tecnologia deve atuar como ferramenta de ampliação da consciência situacional, nunca como autoridade decisória final em serviços essenciais de saúde. A responsabilidade clínica e de acolhimento pertence aos profissionais humanos. Um alerta automatizado informa que a taxa de ocupação ou fila está projetada para estourar a capacidade em curto prazo, permitindo que o gestor da UBS tome medidas humanas cabíveis (ex.: solicitar reforço ao município, estender plantões ou acionar leitos de retaguarda). A decisão humana pondera empatia, gravidade e contexto social, fatores que nenhum algoritmo puramente matemático consegue absorver.

---

### Demonstração de Execução

#### Menu Principal Interativo

Text

```
==================================================================
  TERRITORY PREDICTIVE INSIGHTS - CitizenTech (Saude Publica)     
  Equipamento: UBS Jardim Esperanca - Dr. Paulo Freire
  Regiao: Setor Leste - Periferia Urbana
==================================================================

-------------------- MENU PRINCIPAL --------------------
1. Coletar leituras dos Sensores (Telemetria)
2. Rodar Previsoes Preditivas e Analise de Risco
3. Consultar Historico de Medicoes por Sensor
4. Consultar Alertas ja Emitidos (Auditoria)
5. Gerenciar Limiares de Alerta do Territorio
6. Exibir Status Geral da Unidade de Saude
0. Sair
--------------------------------------------------------
```

#### Coleta e Persistência de Telemetria

Text

```
>>> [COLETA] Interrogando sensores do territorio...
[SUCESSO] 3 medicoes coletadas e gravadas no banco de dados:
   * [1] Sensor: FILA_ESPERA        | Leitura: 42.00  | Registrado em: 2026-11-15T14:20:10
   * [2] Sensor: TEMPO_ATENDIMENTO  | Leitura: 31.40  | Registrado em: 2026-11-15T14:20:10
   * [3] Sensor: OCUPACAO_SALAS     | Leitura: 78.50  | Registrado em: 2026-11-15T14:20:10
```

#### Análise Preditiva e Emissão de Alerta Automático

Text

```
[PROCESSANDO] Executando estrategia: Media Movel Simples
[RESULTADO DA PREVISAO] 58.00 (FILA_ESPERA)

[ALERTA DE SEGURANCA] UM LIMIAR FOI VIOLADO:
   [ALERTA CRITICO] Sensor: FILA_ESPERA | Previsto: 58.00 | Limite: 55.00 | Motivo: Fila projetada em 58 pessoas (Limite Critico: 55). Risco iminente de tumulto e desassistencia.
```

---

### Divisão de Tarefas

* **Manoela Oliveira | RM563952:** Arquitetura geral orientada a objetos, implementação da camada de serviços (ServicoMonitoramento), regras de persistência com Spring Data JPA, tratamento de exceções customizadas, padrões criacionais (Singleton e Factory), Modelagem e implementação das entidades de domínio territorial e das classes de telemetria (interfaces e implementações dos sensores) e elaboração da suíte de testes unitários com Mockito.
* **Felipe Rodrigues | RM565274)** Configuração do arquivo de propriedades do banco de dados (application.properties), testes manuais do fluxo do menu de console e auxílio na documentação técnica.

### Uso de Inteligência Artificial

* **Ferramenta Consultada:** Claude / Gemini.
* **Finalidade do Uso:** Auxílio na estruturação de exemplos de testes com Mockito, esclarecimento de dúvidas pontuais de configuração de escopo de pacotes no VS Code e revisão de coerência na modelagem das classes em conformidade com os critérios avaliativos da Global Solution.
* **Revisão Humana:** Todo o código gerado foi revisado, testado e adaptado manualmente pelo estudante para garantir aderência estrita às boas práticas de Clean Code ensinadas nas aulas de POO.
