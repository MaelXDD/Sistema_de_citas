package utp.citas.citas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.citas.citas.model.Cita;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByPaciente_IdPaciente(Integer idPaciente);

    List<Cita> findByDoctor_IdDoctorAndFechaCita(Integer idDoctor, LocalDate fechaCita);

    List<Cita> findByEstado(String estado);
}
