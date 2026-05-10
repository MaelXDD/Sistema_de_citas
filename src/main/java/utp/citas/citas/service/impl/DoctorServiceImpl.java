package utp.citas.citas.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utp.citas.citas.model.Doctor;
import utp.citas.citas.model.Especialidad;
import utp.citas.citas.model.Horario;
import utp.citas.citas.repository.DoctorRepository;
import utp.citas.citas.repository.EspecialidadRepository;
import utp.citas.citas.repository.HorarioRepository;
import utp.citas.citas.service.DoctorService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final EspecialidadRepository especialidadRepository;
    private final HorarioRepository horarioRepository;

    // Inyección por constructor
    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             EspecialidadRepository especialidadRepository,
                             HorarioRepository horarioRepository) {
        this.doctorRepository = doctorRepository;
        this.especialidadRepository = especialidadRepository;
        this.horarioRepository = horarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> listarTodos() {
        return doctorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> listarActivos() {
        return doctorRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> listarActivosPorEspecialidad(Integer idEspecialidad) {
        // Validar que la especialidad existe antes de buscar
        especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new NoSuchElementException(
                        "Especialidad no encontrada con id: " + idEspecialidad));
        return doctorRepository.findActivosByEspecialidad(idEspecialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor buscarPorId(Integer id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Doctor no encontrado con id: " + id));
    }

    @Override
    public Doctor crear(Doctor doctor) {
        // Validar DNI único
        if (doctorRepository.findByDni(doctor.getDni()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un doctor con el DNI: " + doctor.getDni());
        }
        // Validar correo único
        if (doctorRepository.existsByCorreo(doctor.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un doctor con el correo: " + doctor.getCorreo());
        }
        // Resolver la entidad Especialidad completa desde BD
        Especialidad especialidad = especialidadRepository
                .findById(doctor.getEspecialidad().getIdEspecialidad())
                .orElseThrow(() -> new NoSuchElementException(
                        "Especialidad no encontrada con id: " + doctor.getEspecialidad().getIdEspecialidad()));
        doctor.setEspecialidad(especialidad);
        doctor.setActivo(true);
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor actualizar(Integer id, Doctor datos) {
        Doctor existente = buscarPorId(id);
        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setCorreo(datos.getCorreo());
        existente.setTelefono(datos.getTelefono());
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }
        // Actualizar especialidad si se envía
        if (datos.getEspecialidad() != null && datos.getEspecialidad().getIdEspecialidad() != null) {
            Especialidad especialidad = especialidadRepository
                    .findById(datos.getEspecialidad().getIdEspecialidad())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Especialidad no encontrada con id: " + datos.getEspecialidad().getIdEspecialidad()));
            existente.setEspecialidad(especialidad);
        }
        return doctorRepository.save(existente);
    }

    @Override
    public void desactivar(Integer id) {
        Doctor doctor = buscarPorId(id);
        doctor.setActivo(false);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horario> obtenerHorarios(Integer idDoctor) {
        buscarPorId(idDoctor); // valida que el doctor existe
        return horarioRepository.findActivosByDoctor(idDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horario> obtenerHorariosPorDia(Integer idDoctor, String diaSemana) {
        buscarPorId(idDoctor); // valida que el doctor existe
        return horarioRepository.findByDoctor_IdDoctorAndDiaSemanaAndActivoTrue(idDoctor, diaSemana.toUpperCase());
    }
}
