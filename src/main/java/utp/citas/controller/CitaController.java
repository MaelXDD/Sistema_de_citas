package utp.citas.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Cita;
import utp.citas.citas.service.CitaService;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    // Endpoint para obtener todas las citas
    @GetMapping
    public List<Cita> obtenerCitas() {
        return citaService.listarTodas();
    }

    // Endpoint para agendar una cita (POST)
    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) {
        Cita citaGuardada = citaService.agendarCita(cita);
        return ResponseEntity.ok(citaGuardada);
    }
}