package utp.citas.citas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utp.citas.citas.model.Precio;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Integer> {
    // Busca el precio activo de una especialidad específica
    Optional<Precio> findByEspecialidad_IdEspecialidadAndActivoTrue(Integer idEspecialidad);
}