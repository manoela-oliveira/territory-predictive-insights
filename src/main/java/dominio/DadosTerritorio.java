package dominio;

import excecao.DadosInvalidosException;

/*
Encapsula os parâmetros territoriais, demográficos e os limiares de tolerância
da região atendida pelo equipamento público.
*/
public class DadosTerritorio {

    private String identificadorRegiao;
    private int populacaoAtendida;
    
    // Limiares de alerta para o recorte de Demanda em Saúde
    private double limiarAtencaoFila;
    private double limiarCriticoFila;
    private double limiarAtencaoTempoEsperaMinutos;

    public DadosTerritorio(String identificadorRegiao, int populacaoAtendida) {
        if (identificadorRegiao == null || identificadorRegiao.trim().isEmpty()) {
            throw new DadosInvalidosException("O identificador da região não pode ser nulo ou vazio.");
        }
        if (populacaoAtendida <= 0) {
            throw new DadosInvalidosException("populacaoAtendida", populacaoAtendida, "A população atendida deve ser positiva.");
        }
        this.identificadorRegiao = identificadorRegiao;
        this.populacaoAtendida = populacaoAtendida;
        
        // Valores padrão de limiares
        this.limiarAtencaoFila = 25.0; // Pessoas
        this.limiarCriticoFila = 50.0;  // Pessoas
        this.limiarAtencaoTempoEsperaMinutos = 30.0; // Minutos
    }

    public void atualizarLimiaresFila(double limiarAtencao, double limiarCritico) {
        if (limiarAtencao <= 0) {
            throw new DadosInvalidosException("limiarAtencaoFila", limiarAtencao, "Limiar de atenção deve ser maior que zero.");
        }
        if (limiarCritico <= limiarAtencao) {
            throw new DadosInvalidosException("limiarCriticoFila", limiarCritico, "Limiar crítico deve ser estritamente maior que o de atenção.");
        }
        this.limiarAtencaoFila = limiarAtencao;
        this.limiarCriticoFila = limiarCritico;
    }

    public void setLimiarAtencaoTempoEsperaMinutos(double tempoMinutos) {
        if (tempoMinutos <= 0) {
            throw new DadosInvalidosException("limiarAtencaoTempoEsperaMinutos", tempoMinutos, "Tempo deve ser positivo.");
        }
        this.limiarAtencaoTempoEsperaMinutos = tempoMinutos;
    }

    public String getIdentificadorRegiao() {
        return identificadorRegiao;
    }

    public int getPopulacaoAtendida() {
        return populacaoAtendida;
    }

    public double getLimiarAtencaoFila() {
        return limiarAtencaoFila;
    }

    public double getLimiarCriticoFila() {
        return limiarCriticoFila;
    }

    public double getLimiarAtencaoTempoEsperaMinutos() {
        return limiarAtencaoTempoEsperaMinutos;
    }
}