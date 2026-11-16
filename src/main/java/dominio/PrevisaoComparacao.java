package dominio;

import java.util.List;

// Estratégia de Comparação com Período Anterior: avalia a taxa de crescimento ou salto entre a medição mais recente e a média histórica anterior para projetar picos súbitos.
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final double fatorSensibilidade;

    public PrevisaoComparacao(double fatorSensibilidade) {
        super("Comparação com Período Anterior", "Detecta aceleração ou surto de demanda comparando a última medição com a base.");
        this.fatorSensibilidade = (fatorSensibilidade > 0) ? fatorSensibilidade : 1.15; // 15% de margem padrão
    }

    public PrevisaoComparacao() {
        this(1.15);
    }

    @Override
    public double prever(List<Medicao> historico) {
        // Exige pelo menos 2 medições para comparar
        super.validarHistorico(historico, 2);

        // Medição mais recente
        double ultimaMedicao = historico.get(0).getValor();

        // Média das medições anteriores
        double somaAnteriores = 0.0;
        int contagemAnteriores = historico.size() - 1;
        for (int i = 1; i < historico.size(); i++) {
            somaAnteriores += historico.get(i).getValor();
        }
        double mediaAnterior = somaAnteriores / contagemAnteriores;

        // Se a última medição já superou a média anterior, projeta um pico usando a sensibilidade
        double resultado;
        if (ultimaMedicao > mediaAnterior) {
            resultado = ultimaMedicao * this.fatorSensibilidade;
        } else {
            resultado = (ultimaMedicao + mediaAnterior) / 2.0;
        }

        return Math.round(resultado * 100.0) / 100.0;
    }
}