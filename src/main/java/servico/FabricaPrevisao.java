package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

/*
Padrão Criacional: FACTORY (Fábrica)
Centraliza e encapsula a criação de estratégias de previsão, evitando instanciações diretas e condicionais espalhadas na aplicação.
*/
public class FabricaPrevisao {

    /*
    Cria a estratégia correspondente com base no identificador solicitado.
    @param tipoIdentificador "MEDIA_MOVEL", "COMPARACAO" ou equivalentes.
    @return instância concreta de EstrategiaPrevisao.
    */
    public static EstrategiaPrevisao criarEstrategia(String tipoIdentificador) {
        if (tipoIdentificador == null || tipoIdentificador.trim().isEmpty()) {
            throw new DadosInvalidosException("O tipo de estratégia não pode ser nulo ou vazio.");
        }

        String tipoNormalizado = tipoIdentificador.trim().toUpperCase();

        switch (tipoNormalizado) {
            case "MEDIA_MOVEL":
            case "MEDIA":
                return new PrevisaoMediaMovel(3);

            case "COMPARACAO":
            case "COMPARACAO_ANTERIOR":
                return new PrevisaoComparacao(1.15);

            default:
                throw new DadosInvalidosException("Tipo de estratégia desconhecido: " + tipoIdentificador +
                        ". Opções válidas: MEDIA_MOVEL, COMPARACAO.");
        }
    }
}