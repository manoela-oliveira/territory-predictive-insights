package servico;

import excecao.DadosInvalidosException;

/*
Padrão Criacional: SINGLETON
Mantém uma única instância centralizada das configurações globais, parâmetros de operação e limiares de tolerância de alerta da UBS.
*/
public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    // Limiares de Alerta para Fila de Espera (quantidade de pessoas)
    private double limiarAtencaoFila = 25.0;
    private double limiarAlertaFila = 40.0;
    private double limiarCriticoFila = 55.0;

    // Limiares de Alerta para Tempo de Atendimento (minutos)
    private double limiarAtencaoTempo = 25.0;
    private double limiarCriticoTempo = 40.0;

    // Limiares de Alerta para Ocupação de Leitos/Salas (%)
    private double limiarAtencaoOcupacao = 75.0;
    private double limiarCriticoOcupacao = 90.0;

    // Mínimo de medições no histórico para permitir previsão confiável
    private int leiturasMinimasParaPrevisao = 3;

    // Construtor estritamente PRIVADO impede instanciação externa com 'new'
    private ConfiguracaoSistema() {
    }

    // Ponto de acesso global thread-safe
    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    // Validações e alterações controladas de limiares
    public void configurarLimiaresFila(double atencao, double alerta, double critico) {
        if (atencao <= 0 || alerta <= atencao || critico <= alerta) {
            throw new DadosInvalidosException("Os limiares de fila devem ser positivos e estritamente crescentes: Atenção < Alerta < Crítico.");
        }
        this.limiarAtencaoFila = atencao;
        this.limiarAlertaFila = alerta;
        this.limiarCriticoFila = critico;
    }

    public void setLeiturasMinimasParaPrevisao(int minimo) {
        if (minimo <= 0) {
            throw new DadosInvalidosException("O número mínimo de leituras para previsão deve ser maior que zero.");
        }
        this.leiturasMinimasParaPrevisao = minimo;
    }

    public double getLimiarAtencaoFila() {
        return limiarAtencaoFila;
    }

    public double getLimiarAlertaFila() {
        return limiarAlertaFila;
    }

    public double getLimiarCriticoFila() {
        return limiarCriticoFila;
    }

    public double getLimiarAtencaoTempo() {
        return limiarAtencaoTempo;
    }

    public double getLimiarCriticoTempo() {
        return limiarCriticoTempo;
    }

    public double getLimiarAtencaoOcupacao() {
        return limiarAtencaoOcupacao;
    }

    public double getLimiarCriticoOcupacao() {
        return limiarCriticoOcupacao;
    }

    public int getLeiturasMinimasParaPrevisao() {
        return leiturasMinimasParaPrevisao;
    }
}