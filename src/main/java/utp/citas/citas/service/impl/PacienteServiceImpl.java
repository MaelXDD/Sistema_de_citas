package utp.citas.citas.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.citas.citas.model.Paciente;
import utp.citas.citas.repository.PacienteRepository;
import utp.citas.citas.service.PacienteService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente buscarPorId(Integer id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Paciente registrar(Paciente paciente) {
        if (pacienteRepository.existsByCorreo(paciente.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        String passwordHasheada = passwordEncoder.encode(paciente.getPassword());
        paciente.setPassword(passwordHasheada);

        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public Paciente actualizar(Integer id, Paciente paciente) {
        Paciente pacienteExistente = buscarPorId(id);

        pacienteExistente.setNombres(paciente.getNombres());
        pacienteExistente.setApellidos(paciente.getApellidos());
        pacienteExistente.setTelefono(paciente.getTelefono());

        if (paciente.getPassword() != null && !paciente.getPassword().isEmpty()) {
            pacienteExistente.setPassword(passwordEncoder.encode(paciente.getPassword()));
        }

        return pacienteRepository.save(pacienteExistente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Paciente paciente = buscarPorId(id);
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
    }
}
