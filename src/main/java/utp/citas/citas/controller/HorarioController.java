package utp.citas.citas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Horario;
import utp.citas.citas.repository.HorarioRepository;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    private final HorarioRepository horarioRepository;

    public HorarioController(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    // GET /api/horarios → todos los horarios activos con doctor y especialidad
    @GetMapping
    public ResponseEntity<List<Horario>> listarTodos() {
        return ResponseEntity.ok(horarioRepository.findAllActivos());
    }

    // GET /api/horarios/doctor/{idDoctor}
    @GetMapping("/doctor/{idDoctor}")
    public ResponseEntity<List<Horario>> porDoctor(@PathVariable Integer idDoctor) {
        return ResponseEntity.ok(horarioRepository.findActivosByDoctor(idDoctor));
    }

    // POST /api/horarios
    @PostMapping
    public ResponseEntity<?> asignarHorario(@RequestBody Horario horario) {
        List<Horario> existentes = horarioRepository.findByDoctor_IdDoctorAndDiaSemanaAndActivoTrue(
                horario.getDoctor().getIdDoctor(),
                horario.getDiaSemana()
        );

        if (!existentes.isEmpty()) {
            return ResponseEntity.badRequest().body("El especialista médico ya cuenta con un turno asignado para el día: " + horario.getDiaSemana());
        }

        horario.setActivo(true);
        Horario guardado = horarioRepository.save(horario);
        return ResponseEntity.ok(guardado);
    }

    // DELETE /api/horarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Integer id) {
        horarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}