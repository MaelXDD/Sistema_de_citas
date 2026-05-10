package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    private Boolean activo = true;

    // Getters y Setters
}