package dominio;

// Contrato de comportamento para qualquer dispositivo de telemetria conectado à rede de monitoramento da comunidade.
public interface Sensor {

    /*
    Efetua a leitura atual do parâmetro monitorado.
    @return valor numérico da leitura.
    */
    double lerValor();

    /*
    Verifica a conectividade e integridade do hardware do sensor.
    @return true se o sensor estiver operando normalmente, false caso contrário.
    */
    boolean verificarFuncionamento();

    /*
    Retorna o identificador textual do tipo do sensor.
    @return String descritiva (ex: FILA_ESPERA, TEMPO_ATENDIMENTO).
    */
    String retornarTipo();
}