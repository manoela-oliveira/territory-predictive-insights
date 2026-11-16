package dominio;

import java.util.List;

// Estratégia de Média Móvel: calcula a média aritmética das últimas N medições para estimar uma tendência estável da demanda.
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janelaObservacao;

    public PrevisaoMediaMovel(int janelaObservacao) {
        super("Média Móvel Simples", "Calcula a média das últimas leituras recentes para suavizar variações bruscas.");
        this.janelaObservacao = (janelaObservacao > 0) ? janelaObservacao : 3;
    }

    public PrevisaoMediaMovel() {
        this(3); // Janela padrão de 3 observações
    }

    @Override
    public double prever(List<Medicao> historico) {
        // Validação herdada da classe mãe
        super.validarHistorico(historico, 1);

        int limite = Math.min(historico.size(), this.janelaObservacao);
        double soma = 0.0;

        // As medições chegam ordenadas da mais recente para a mais antiga
        for (int i = 0; i < limite; i++) {
            soma += historico.get(i).getValor();
        }

        double media = soma / limite;
        // Retorna com duas casas decimais arredondadas
        return Math.round(media * 100.0) / 100.0;
    }
}