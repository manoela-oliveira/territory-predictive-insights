package dominio;

import java.time.LocalDateTime;

import excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entidade JPA que representa um registro histórico coletado por um sensor.
@Entity
@Table(name = "tb_medicao")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Medicao() {
    // Construtor padrão exigido pelo JPA
    }

    public Medicao(String tipoSensor, Double valor) {
        if (tipoSensor == null || tipoSensor.trim().isEmpty()) {
            throw new DadosInvalidosException("O tipo do sensor é obrigatório na medição.");
        }
        if (valor == null || valor < 0) {
            throw new DadosInvalidosException("valorMedicao", valor != null ? valor : -1, "O valor da medição não pode ser negativo ou nulo.");
        }
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public Double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("[%s] Sensor: %-18s | Leitura: %-6.2f | Registrado em: %s",
                id != null ? id : "NOVO", tipoSensor, valor, dataHora.toString());
    }
}