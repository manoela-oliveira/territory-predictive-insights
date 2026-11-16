package src.main.java.dominio;

import excecao.DadosInvalidosException;

/**
 * Equipamento público de saúde (UBS / UPA / Posto de Atendimento)
 * monitorado para controle de filas e sobrecarga de atendimento.
 */
public class UnidadeSaude extends ComponenteTerritorial {

    private int capacidadeConsultorios;
    private int profissionaisDisponiveis;

    public UnidadeSaude(Long id, String nome, String regiao, String status, int capacidadeConsultorios, int profissionaisDisponiveis) {
        super(id, nome, regiao, status);
        if (capacidadeConsultorios <= 0) {
            throw new DadosInvalidosException("capacidadeConsultorios", capacidadeConsultorios, "A capacidade deve ser maior que zero.");
        }
        if (profissionaisDisponiveis < 0) {
            throw new DadosInvalidosException("profissionaisDisponiveis", profissionaisDisponiveis, "Quantidade de profissionais não pode ser negativa.");
        }
        this.capacidadeConsultorios = capacidadeConsultorios;
        this.profissionaisDisponiveis = profissionaisDisponiveis;
    }

    @Override
    public String verificarStatus() {
        if (profissionaisDisponiveis == 0) {
            super.setStatus("CRÍTICO - SEM EQUIPE MÉDICA");
            return getStatus();
        }
        double ratio = (double) profissionaisDisponiveis / capacidadeConsultorios;
        if (ratio < 0.5) {
            super.setStatus("ALERTA - SUBDIMENSIONADO");
        } else {
            super.setStatus("OPERACIONAL ESTÁVEL");
        }
        return getStatus();
    }

    public int getCapacidadeConsultorios() {
        return capacidadeConsultorios;
    }

    public int getProfissionaisDisponiveis() {
        return profissionaisDisponiveis;
    }

    public void setProfissionaisDisponiveis(int profissionaisDisponiveis) {
        if (profissionaisDisponiveis < 0) {
            throw new DadosInvalidosException("profissionaisDisponiveis", profissionaisDisponiveis, "Quantidade de profissionais não pode ser negativa.");
        }
        this.profissionaisDisponiveis = profissionaisDisponiveis;
    }
}
