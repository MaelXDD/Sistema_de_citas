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

    @GetMapping
    public List<Horario> listarTodos() {
        return horarioRepository.listarHorariosConMedicos();
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Integer id) {
        horarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
