package utp.citas.citas.model;

import java.math.BigDecimal;

public class EspecialidadDTO {
    private String especialidad;
    private Long totalCitas;
    private Long pacientesUnicos;
    private BigDecimal ingresos;

    public EspecialidadDTO() {
    }

    public EspecialidadDTO(String especialidad,
                                  Long totalCitas,
                                  Long pacientesUnicos,
                                  BigDecimal ingresos) {

        this.especialidad = especialidad;
        this.totalCitas = totalCitas;
        this.pacientesUnicos = pacientesUnicos;
        this.ingresos = ingresos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Long getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(Long totalCitas) {
        this.totalCitas = totalCitas;
    }

    public Long getPacientesUnicos() {
        return pacientesUnicos;
    }

    public void setPacientesUnicos(Long pacientesUnicos) {
        this.pacientesUnicos = pacientesUnicos;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

}

