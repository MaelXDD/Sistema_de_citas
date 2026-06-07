package utp.citas.citas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.Precio;
import utp.citas.citas.repository.PrecioRepository;

@RestController
@RequestMapping("/api/precios")
@CrossOrigin(origins = "*")
public class PrecioController {

    private final PrecioRepository precioRepository;

    public PrecioController(PrecioRepository precioRepository) {
        this.precioRepository = precioRepository;
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    public ResponseEntity<Precio> obtenerPrecioPorEspecialidad(@PathVariable Integer idEspecialidad) {
        return precioRepository.findByEspecialidad_IdEspecialidadAndActivoTrue(idEspecialidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}