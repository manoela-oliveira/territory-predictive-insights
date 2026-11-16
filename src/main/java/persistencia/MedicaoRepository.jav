package persistencia;

import dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositório Spring Data JPA para operações de persistência e consulta sobre o histórico de medições coletadas.
@Repository
public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    /*
    Consulta todo o histórico de um sensor específico em ordem decrescente (mais recentes primeiro).
    @param tipoSensor tipo do sensor (ex: FILA_ESPERA, TEMPO_ATENDIMENTO).
    @return lista de medições registradas.
    */
    List<Medicao> findByTipoSensorOrderByDataHoraDesc(String tipoSensor);

    // Conta quantas medições já foram gravadas para um determinado sensor - importante para checar se temos dados suficientes para previsões confiáveis.
    long countByTipoSensor(String tipoSensor);
}