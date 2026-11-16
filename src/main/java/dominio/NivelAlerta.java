package dominio;

/*
Níveis de gravidade dos alertas automáticos emitidos pelo sistema.
*/
public enum NivelAlerta {
    ATENCAO("Nível de Atenção: demanda crescente detectada."),
    ALERTA("Nível de Alerta: proximidade do limite de capacidade operacional."),
    CRITICO("Nível Crítico: capacidade máxima estourada, risco de colapso.");

    private final String descricao;

    NivelAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}