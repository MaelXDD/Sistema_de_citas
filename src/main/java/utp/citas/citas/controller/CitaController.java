package utp.citas.citas.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Cita;
import utp.citas.citas.repository.CitaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaRepository citaRepository;

        this.citaRepository = citaRepository;
    }

    @PostMapping
    public ResponseEntity<Cita> crear(@Valid @RequestBody Cita cita) {
        cita.setEstado("PENDIENTE");
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<Cita>> porPaciente(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(citaRepository.findByPaciente_IdPaciente(idPaciente));
    }
}