import dominio.Medicao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MedicaoTest {

    @Test
    public void deveCriarMedicaoComSucessoQuandoDadosForemValidos() {
        // Arrange
        String tipo = "FILA_ESPERA";
        Double valor = 28.5;

        // Act
        Medicao medicao = new Medicao(tipo, valor);

        // Assert (Padrão AAA)
        assertNotNull(medicao);
        assertEquals("FILA_ESPERA", medicao.getTipoSensor());
        assertEquals(28.5, medicao.getValor());
        assertNotNull(medicao.getDataHora());
    }

    @Test
    public void deveLancarDadosInvalidosExceptionQuandoValorMedicaoForNegativo() {
        // Arrange
        String tipo = "TEMPO_ATENDIMENTO";
        Double valorNegativo = -15.0;

        // Act & Assert
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(tipo, valorNegativo)
        );

        assertTrue(exception.getMessage().contains("não pode ser negativo"));
    }

    @Test
    public void deveLancarDadosInvalidosExceptionQuandoTipoSensorForNulo() {
        // Act & Assert
        assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(null, 10.0)
        );
    }
}