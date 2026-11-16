package persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dominio.Alerta;
import dominio.NivelAlerta;

/*
Repositório Spring Data JPA para gerenciamento e auditoria dos alertas gerados.
*/
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /*
    Recupera todos os alertas ordenados do mais recente para o mais antigo.
    */
    List<Alerta> findAllByOrderByDataHoraDesc();

    /*
    Recupera alertas filtrados por nível de criticidade (ex: CRITICO, ALERTA).
    */
    List<Alerta> findByNivelOrderByDataHoraDesc(NivelAlerta nivel);

    /*
    Recupera alertas emitidos para um sensor específico.
    */
    List<Alerta> findByTipoSensorOrderByDataHoraDesc(String tipoSensor);
}