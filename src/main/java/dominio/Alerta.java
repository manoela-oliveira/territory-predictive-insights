package dominio;

import java.time.LocalDateTime;

import excecao.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que registra um alerta emitido pelo sistema a partir
 * de previsões que ultrapassaram os limiares de segurança.
 */
@Entity
@Table(name = "tb_alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private Double valorPrevisto;

    @Column(nullable = false)
    private Double limiarViolado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 500)
    private String justificativa;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Alerta() {
        // Construtor padrão exigido pelo JPA
    }

    public Alerta(String tipoSensor, Double valorPrevisto, Double limiarViolado, NivelAlerta nivel, String justificativa) {
        if (tipoSensor == null || tipoSensor.trim().isEmpty()) {
            throw new DadosInvalidosException("Sensor de origem do alerta não informado.");
        }
        if (valorPrevisto == null || valorPrevisto < 0) {
            throw new DadosInvalidosException("valorPrevisto", valorPrevisto != null ? valorPrevisto : -1, "Valor previsto inválido.");
        }
        if (nivel == null) {
            throw new DadosInvalidosException("Nível do alerta deve ser especificado.");
        }
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new DadosInvalidosException("Justificativa do alerta é obrigatória.");
        }

        this.tipoSensor = tipoSensor;
        this.valorPrevisto = valorPrevisto;
        this.limiarViolado = limiarViolado;
        this.nivel = nivel;
        this.justificativa = justificativa;
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

    public Double getValorPrevisto() {
        return valorPrevisto;
    }

    public Double getLimiarViolado() {
        return limiarViolado;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("[ALERTA %-7s] Sensor: %-18s | Previsto: %-6.2f | Limite: %-6.2f | Motivo: %s",
                nivel, tipoSensor, valorPrevisto, limiarViolado, justificativa);
    }
}