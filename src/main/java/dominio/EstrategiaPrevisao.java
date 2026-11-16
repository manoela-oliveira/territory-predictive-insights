package dominio;

import java.util.List;

import excecao.DadosInvalidosException;

// Superclasse abstrata que define o contrato e os comportamentos base para os algoritmos preditivos de demanda territorial

public abstract class EstrategiaPrevisao {

    private final String nomeEstrategia;
    private final String descricao;

    public EstrategiaPrevisao(String nomeEstrategia, String descricao) {
        if (nomeEstrategia == null || nomeEstrategia.trim().isEmpty()) {
            throw new DadosInvalidosException("O nome da estratégia de previsão é obrigatório.");
        }
        this.nomeEstrategia = nomeEstrategia;
        this.descricao = descricao;
    }

    /*
    Contrato polimórfico: cada classe filha executa o cálculo preditivo com base no histórico real fornecido pelo banco de dados.
    @param historico lista de medições cronológicas do sensor.
    @return valor numérico estimado para os próximos períodos.
    */
    public abstract double prever(List<Medicao> historico);

    // Validação comum para garantir que o sistema não faça previsões sem uma base amostral mínima e confiável.
    protected void validarHistorico(List<Medicao> historico, int tamanhoMinimo) {
        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Histórico de medições não pode ser vazio para cálculo preditivo.");
        }
        if (historico.size() < tamanhoMinimo) {
            throw new DadosInvalidosException(
                String.format("Histórico insuficiente para a estratégia '%s'. Necessário no mínimo %d leituras, mas há apenas %d.",
                        nomeEstrategia, tamanhoMinimo, historico.size())
            );
        }
    }

    public String getNomeEstrategia() {
        return nomeEstrategia;
    }

    public String getDescricao() {
        return descricao;
    }
}