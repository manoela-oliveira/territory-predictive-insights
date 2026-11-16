package src.main.java.dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

/**
 * Sensor/sistema de prontuário eletrônico que mede o tempo médio
 * de permanência e consulta por paciente (em minutos).
 */
public class SensorTempoAtendimento implements Sensor {

    private final String identificador;
    private boolean operacional;
    private final Random random = new Random();

    public SensorTempoAtendimento(String identificador) {
        this.identificador = identificador;
        this.operacional = true;
    }

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(identificador, "Módulo de tempo de atendimento inoperante.");
        }
        // Simula tempo médio entre 8.0 e 48.0 minutos por consulta
        return 8.0 + (random.nextDouble() * 40.0);
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.operacional;
    }

    @Override
    public String retornarTipo() {
        return "TEMPO_ATENDIMENTO";
    }

    public void setOperacional(boolean operacional) {
        this.operacional = operacional;
    }

    public String getIdentificador() {
        return identificador;
    }
}