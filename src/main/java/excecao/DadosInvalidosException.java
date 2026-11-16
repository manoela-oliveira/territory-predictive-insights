package src.main.java.excecao;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }

    public DadosInvalidosException(String campo, double valorInvalido, String motivo) {
        super(String.format("Dado inválido para '%s' com valor [%.2f]: %s", campo, valorInvalido, motivo));
    }
}