package utp.citas.citas.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Cita;
import utp.citas.citas.repository.CitaRepository;
import utp.citas.citas.repository.PagoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;

    public CitaController(CitaRepository citaRepository, PagoRepository pagoRepository) {
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
    }

    @PostMapping
    public ResponseEntity<Cita> crear(@Valid @RequestBody Cita cita) {
        cita.setEstado("PENDIENTE");
        return ResponseEntity.status(HttpStatus.CREATED).body(citaRepository.save(cita));
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<Cita>> porPaciente(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(citaRepository.findByPaciente_IdPaciente(idPaciente));
    }

    @GetMapping("/ocupadas")
    public ResponseEntity<List<String>> horasOcupadas(
            @RequestParam Integer idDoctor,
            @RequestParam String fecha) {
        LocalDate fechaDate = LocalDate.parse(fecha);
        List<String> horas = citaRepository.findByDoctor_IdDoctorAndFechaCita(idDoctor, fechaDate)
                .stream()
                .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()))
                .map(c -> c.getHoraCita().toString().substring(0, 5))
                .collect(Collectors.toList());
        return ResponseEntity.ok(horas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        pagoRepository.deleteByCita_IdCita(id); // elimina el pago asociado
        citaRepository.findById(id).ifPresent(cita -> {
            cita.setEstado("CANCELADA");
            citaRepository.save(cita);
        });
        return ResponseEntity.noContent().build();
    }
}