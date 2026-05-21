package utp.citas.citas.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Cita;
import utp.citas.citas.repository.CitaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaRepository citaRepository;

    public CitaController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
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
        List<Cita> citas = citaRepository.findByDoctor_IdDoctorAndFechaCita(idDoctor, fechaDate);
        List<String> horas = citas.stream()
                .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()))
                .map(c -> c.getHoraCita().toString().substring(0, 5))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(horas);
    }
}