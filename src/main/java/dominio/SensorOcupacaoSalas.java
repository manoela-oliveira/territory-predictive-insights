package dominio;

import java.util.Random;

import excecao.FonteIndisponivelException;

/*
Monitor de ocupação das salas de emergência, observação e leitos rápidos
expresso em taxa percentual (0 a 100%).
*/
public class SensorOcupacaoSalas implements Sensor {

    private final String identificador;
    private boolean operacional;
    private final Random random = new Random();

    public SensorOcupacaoSalas(String identificador) {
        this.identificador = identificador;
        this.operacional = true;
    }

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(identificador, "Sensor de ocupação de leitos/salas sem sinal.");
        }
        // Simula ocupação de 20% a 100%
        return 20.0 + (random.nextDouble() * 80.0);
    }

    @Override
    public boolean verificarFuncionamento() {
        return this.operacional;
    }

    @Override
    public String retornarTipo() {
        return "OCUPACAO_SALAS";
    }

    public void setOperacional(boolean operacional) {
        this.operacional = operacional;
    }

    public String getIdentificador() {
        return identificador;
    }
}