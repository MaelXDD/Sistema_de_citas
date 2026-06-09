package utp.citas.citas.service;

import utp.citas.citas.model.Doctor;
import utp.citas.citas.model.Horario;

import java.util.List;

public interface DoctorService {

    List<Doctor> listarTodos();

    List<Doctor> listarActivos();

    List<Doctor> listarActivosPorEspecialidad(Integer idEspecialidad);

    Doctor buscarPorId(Integer id);

    Doctor crear(Doctor doctor);

    Doctor actualizar(Integer id, Doctor doctor);

    void desactivar(Integer id);

    // Horarios del doctor
    List<Horario> obtenerHorarios(Integer idDoctor);

    List<Horario> obtenerHorariosPorDia(Integer idDoctor, String diaSemana);
}
