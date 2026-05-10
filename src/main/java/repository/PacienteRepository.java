package repository;

import model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // DAO personalizado: buscar por DNI
    Optional<Paciente> findByDni(String dni);

    // DAO personalizado: listar solo pacientes activos
    List<Paciente> findAllByActivoTrue();
}