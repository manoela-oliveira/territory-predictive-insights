package src.main.java.excecao;

public class FonteIndisponivelException extends RuntimeException {

    public FonteIndisponivelException(String identificadorSensor) {
        super("A fonte de dados/sensor [" + identificadorSensor + "] está indisponível ou inoperante.");
    }

    public FonteIndisponivelException(String identificadorSensor, String motivo) {
        super(String.format("Sensor [%s] indisponível: %s", identificadorSensor, motivo));
    }
}