import dominio.*;
import excecao.DadosInvalidosException;
import persistencia.AlertaRepository;
import persistencia.MedicaoRepository;
import servico.ServicoMonitoramento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testes Unitários com isolamento total (sem banco Oracle/H2 e sem subir o Spring). O Mockito cria o repositório dublê e injeta no serviço.
@ExtendWith(MockitoExtension.class)
public class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @InjectMocks
    private ServicoMonitoramento servicoMonitoramento;

    @Test
    public void deveEmitirAlertaCriticoQuandoFilaProjetadaEstourarLimiarCritico() {
        // Arrange: configurando o dublê para retornar o alerta salvo
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double filaProjetadaCritica = 60.0; // Limiar crítico padrão é 55.0

        // Act: chamando a lógica de negócio
        Alerta alertaEmitido = servicoMonitoramento.analisarRiscoEEmitirAlerta("FILA_ESPERA", filaProjetadaCritica);

        // Assert: verifica se o alerta foi criado com o nível e justificativa esperados
        assertNotNull(alertaEmitido);
        assertEquals(NivelAlerta.CRITICO, alertaEmitido.getNivel());
        assertEquals("FILA_ESPERA", alertaEmitido.getTipoSensor());
        assertTrue(alertaEmitido.getJustificativa().contains("Crítico"));

        // Prova que o repositório de alertas foi acionado
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    public void naoDeveEmitirAlertaQuandoDemandaEstiverDentroDaNormalidade() {
        // Arrange
        double filaDentroDoLimite = 15.0; // Limiar de atenção começa em 25.0

        // Act
        Alerta alerta = servicoMonitoramento.analisarRiscoEEmitirAlerta("FILA_ESPERA", filaDentroDoLimite);

        // Assert
        assertNull(alerta);

        // Prova que o repositório NUNCA foi chamado para salvar alerta
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    public void deveLancarExcecaoQuandoNaoHouverLeiturasSuficientesParaPrevisao() {
        // Arrange: dublê retorna apenas 1 leitura (mínimo necessário é 3)
        List<Medicao> historicoInsuficiente = List.of(new Medicao("FILA_ESPERA", 20.0));
        when(medicaoRepository.findByTipoSensorOrderByDataHoraDesc("FILA_ESPERA"))
                .thenReturn(historicoInsuficiente);

        EstrategiaPrevisao estrategia = new PrevisaoMediaMovel();

        // Act & Assert: o sistema deve se calar e lançar exceção
        DadosInvalidosException ex = assertThrows(
                DadosInvalidosException.class,
                () -> servicoMonitoramento.executarPrevisao("FILA_ESPERA", estrategia)
        );

        assertTrue(ex.getMessage().contains("O sistema deve se calar"));
    }
}