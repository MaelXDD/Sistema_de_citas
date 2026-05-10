package utp.citas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utp.citas.citas.model.Cita;
import utp.citas.citas.repository.CitaRepository;
import utp.citas.citas.enums.EstadoCita;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    // Caso de Uso 1: Agendar nueva cita (Pre-registro)
    public Cita agendarCita(Cita nuevaCita) {
        // Por regla de negocio, toda cita nueva nace en estado PENDIENTE de pago
        nuevaCita.setEstado(EstadoCita.PENDIENTE);
        nuevaCita.setCreatedAt(LocalDateTime.now());

        return citaRepository.save(nuevaCita);
    }

    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }
}