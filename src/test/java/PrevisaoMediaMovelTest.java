import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import dominio.Medicao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

public class PrevisaoMediaMovelTest {

    @Test
    public void deveCalcularMediaMovelCorretamenteParaJanelaDeTresLeituras() {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);
        List<Medicao> historico = new ArrayList<>();
        historico.add(new Medicao("FILA_ESPERA", 30.0)); // mais recente
        historico.add(new Medicao("FILA_ESPERA", 20.0));
        historico.add(new Medicao("FILA_ESPERA", 10.0));

        // Act (Média esperada: (30 + 20 + 10) / 3 = 20.0)
        double previsao = estrategia.prever(historico);

        // Assert
        assertEquals(20.0, previsao, 0.01);
    }

    @Test
    public void deveLancarDadosInvalidosExceptionQuandoHistoricoForVazio() {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel();
        List<Medicao> historicoVazio = new ArrayList<>();

        // Act & Assert
        assertThrows(
                DadosInvalidosException.class,
                () -> estrategia.prever(historicoVazio)
        );
    }
}