package dominio;

import excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private Long id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(Long id, String nome, String regiao, String status) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException("O nome do componente territorial não pode ser vazio.");
        }
        if (regiao == null || regiao.trim().isEmpty()) {
            throw new DadosInvalidosException("A região territorial deve ser informada.");
        }
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = (status != null && !status.trim().isEmpty()) ? status : "OPERACIONAL";
    }

    /*
    Método abstrato obrigatório: cada equipamento público avalia seu estado
    operacional conforme suas próprias métricas de demanda e capacidade.
    */
    public abstract String verificarStatus();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException("Nome inválido para o componente territorial.");
        }
        this.nome = nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        if (regiao == null || regiao.trim().isEmpty()) {
            throw new DadosInvalidosException("Região inválida.");
        }
        this.regiao = regiao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new DadosInvalidosException("Status não pode ser nulo.");
        }
        this.status = status;
    }
}