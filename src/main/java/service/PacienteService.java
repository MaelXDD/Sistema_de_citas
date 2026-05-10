package service;

import model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PacienteRepository;

import java.util.Optional;

// Archivo: service/PacienteService.java
@Service
public class PacienteService {
    @Autowired
    private PacienteRepository repository;

    public Paciente guardar(Paciente p) {
        // Aquí podrías validar que el DNI no exista antes de guardar
        return repository.save(p);
    }

    public Optional<Paciente> buscarPorDni(String dni) {
        return repository.findByDni(dni);
    }
}