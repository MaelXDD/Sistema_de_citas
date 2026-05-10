package utp.citas.citas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utp.citas.citas.model.Horario;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    // Obtener los horarios activos de un doctor específico
    @Query("SELECT h FROM Horario h JOIN FETCH h.doctor d WHERE d.idDoctor = :idDoctor AND h.activo = true")
    List<Horario> findActivosByDoctor(@Param("idDoctor") Integer idDoctor);

    // Consultar disponibilidad por doctor y día de la semana
    List<Horario> findByDoctor_IdDoctorAndDiaSemanaAndActivoTrue(Integer idDoctor, String diaSemana);
}
