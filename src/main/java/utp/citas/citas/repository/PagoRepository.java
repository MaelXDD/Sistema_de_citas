package utp.citas.citas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utp.citas.citas.model.Pago;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Buscar el pago de una cita específica
    Optional<Pago> findByCita_IdCita(Integer idCita);

    // Historial de pagos de un paciente (join a través de Cita → Paciente)
    @Query("SELECT p FROM Pago p JOIN FETCH p.cita c JOIN FETCH c.paciente pac WHERE pac.idPaciente = :idPaciente")
    List<Pago> findByPaciente(@Param("idPaciente") Integer idPaciente);

    // Pagos completados (para reportes)
    List<Pago> findByEstadoPago(String estadoPago);
}
