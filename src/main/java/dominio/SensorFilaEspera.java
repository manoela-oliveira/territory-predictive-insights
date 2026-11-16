package dominio;

import java.util.Random;

import excecao.FonteIndisponivelException;

// Sensor/catraca de triagem que monitora a quantidade de pessoas aguardando atendimento na recepção da UBS.

public class SensorFilaEspera implements Sensor {

    private final String identificador;
    private boolean operacional;
    private final Random random = new Random();

    public SensorFilaEspera(String identificador) {
        this.identificador = identificador;
        this.operacional = true;
    }

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(identificador, "Sensor de Fila de Espera desconectado da rede.");
        }
        // Simula demanda de 5 a 65 pessoas na fila de espera
        return 5 + random.nextInt(61);
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.operacional;
    }

    @Override
    public String retornarTipo() {
        return "FILA_ESPERA";
    }

    public void setOperacional(boolean operacional) {
        this.operacional = operacional;
    }

    public String getIdentificador() {
        return identificador;
    }
}