package utp.citas.citas.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.citas.citas.model.PacienteEspecialidadDTO;
import utp.citas.citas.model.ReporteAfluenciaDTO;
import utp.citas.citas.model.DoctorCitasDTO;
import utp.citas.citas.controller.EspecialidadDTO;
import utp.citas.citas.repository.CitaRepository;
import utp.citas.citas.service.ReporteAfluenciaService;
import utp.citas.citas.service.impl.ReporteService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**

 * Controlador REST encargado de la generación de reportes y estadísticas del sistema.
 *
 * <p>Este controlador centraliza la lógica de exposición de datos analíticos,
 * incluyendo reportes en PDF y consultas estadísticas para la toma de decisiones.</p>
 *
 * <p><b>Base URL:</b> /api/reportes</p>
 *
 * <p>Incluye:</p>
 * <ul>
 * ```
 <li>Reportes de afluencia de pacientes</li>
 ```
 * ```
 <li>Generación de reportes en PDF (citas, doctores, especialidades)</li>
 ```
 * ```
 <li>Estadísticas por doctores, pacientes y especialidades</li>
 ```
 * </ul>
 *
 * <p>Se permite acceso desde cualquier origen (CORS habilitado).</p>

 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    /**
     * Servicio encargado de la generación de reportes en PDF.
     */
    private final ReporteService reporteService;

    /**
     * Repositorio para consultas avanzadas relacionadas a citas.
     */
    private final CitaRepository citaRepository;

    /**
     * Servicio encargado del reporte de afluencia de pacientes.
     */
    private final ReporteAfluenciaService afluenciaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param reporteService servicio de reportes
     * @param citaRepository repositorio de citas
     * @param afluenciaService servicio de afluencia
     */
    public ReporteController(ReporteService reporteService, CitaRepository citaRepository, ReporteAfluenciaService afluenciaService) {
        this.reporteService = reporteService;
        this.citaRepository = citaRepository;
        this.afluenciaService = afluenciaService;
    }

    /**
     * Obtiene el reporte de afluencia de pacientes.
     *
     * @return lista de datos de afluencia
     */
    @GetMapping("/afluencia")
    public ResponseEntity<List<ReporteAfluenciaDTO>> getAfluencia() {
        return ResponseEntity.ok(afluenciaService.obtenerReporteAfluencia());
    }

    /**
     * Genera y descarga el directorio de doctores en formato PDF.
     *
     * @return archivo PDF como arreglo de bytes
     */
    @GetMapping("/doctores/pdf")
    public ResponseEntity<byte[]> descargarDirectorioDoctores() {
        try {
            byte[] pdfBytes = reporteService.generarDirectorioDoctoresPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Directorio_Doctores.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera y descarga el comprobante de una cita en PDF.
     *
     * @param idCita identificador de la cita
     * @return archivo PDF del comprobante
     */
    @GetMapping("/citas/{idCita}/pdf")
    public ResponseEntity<byte[]> descargarReporteCita(@PathVariable Integer idCita) {
        try {
            byte[] pdfBytes = reporteService.generarReporteCitaPDF(idCita);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Comprobante_Cita_" + idCita + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera y descarga el reporte de especialidades en PDF.
     *
     * @return archivo PDF del reporte
     */
    @GetMapping("/especialidades/pdf")
    public ResponseEntity<byte[]> descargarReporteEspecialidades() {
        try {
            byte[] pdfBytes = reporteService.generarReporteEspecialidadesPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reporte_Especialidades.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera un reporte personalizado filtrado por día, turno y especialidad.
     *
     * @param dia día de la semana
     * @param turno turno (mañana/tarde)
     * @param especialidad especialidad médica
     * @return archivo PDF generado
     */
    @GetMapping("/consulta3/pdf")
    public ResponseEntity<byte[]> descargarReporteConsulta3(
            @RequestParam(value = "dia", defaultValue = "Todos") String dia,
            @RequestParam(value = "turno", defaultValue = "Todos") String turno,
            @RequestParam(value = "especialidad", defaultValue = "Todos") String especialidad) {
        try {
            byte[] pdfBytes = reporteService.generarReporteConsulta3PDF(dia, turno, especialidad);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "Reporte_Consulta3.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene estadísticas de citas por doctor.
     *
     * @param idEspecialidad filtro opcional por especialidad
     * @param estado filtro opcional por estado de cita
     * @return lista de estadísticas
     */
    @GetMapping("/estadisticas/doctores-citas")
    public ResponseEntity<List<DoctorCitasDTO>> estadisticasDoctoresCitas(
            @RequestParam(required = false) Integer idEspecialidad,
            @RequestParam(required = false) String estado) {

        List<Object[]> rows = citaRepository.estadisticasDoctoresCitas(
                idEspecialidad,
                (estado == null || estado.isBlank()) ? null : estado
        );

        List<DoctorCitasDTO> result = rows.stream()
                .map(r -> new DoctorCitasDTO(
                        (String) r[0],
                        (String) r[2],
                        (String) r[3],
                        ((Number) r[4]).longValue(),
                        (BigDecimal) r[5]
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene la lista de pacientes por especialidad.
     *
     * @param idEspecialidad identificador de la especialidad
     * @return lista de pacientes con métricas
     */
    @GetMapping("/estadisticas/pacientes-especialidad")
    public ResponseEntity<List<PacienteEspecialidadDTO>> pacientesPorEspecialidad(
            @RequestParam Integer idEspecialidad) {

        List<Object[]> datos = citaRepository.pacientesPorEspecialidad(idEspecialidad);

        List<PacienteEspecialidadDTO> lista = datos.stream()
                .map(fila -> new PacienteEspecialidadDTO(
                        (String) fila[0],
                        (String) fila[1],
                        (BigDecimal) fila[2],
                        ((Number) fila[3]).longValue()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    /**
     * Obtiene estadísticas de ingresos por especialidad.
     *
     * @return lista de estadísticas de especialidades
     */
    @GetMapping("/estadisticas/ingresos-especialidad")
    public List<EspecialidadDTO> especialidadDTOS() {
        List<Object[]> datos = citaRepository.ingresosEspecialidad();
        List<EspecialidadDTO> lista = new ArrayList<>();

        for (Object[] fila : datos) {
            lista.add(
                    new EspecialidadDTO(
                            fila[0].toString(),
                            ((Number) fila[1]).longValue(),
                            ((Number) fila[2]).longValue(),
                            (BigDecimal) fila[3]
                    )
            );
        }
        return lista;
    }

    /**
     * Genera un reporte PDF de pacientes por especialidad.
     *
     * @param idEspecialidad identificador de la especialidad
     * @return archivo PDF generado
     */
    @GetMapping("/pacientes-especialidad/{idEspecialidad}/pdf")
    public ResponseEntity<byte[]> descargarReportePacientesEspecialidad(@PathVariable Integer idEspecialidad) {
        try {
            byte[] pdfBytes = reporteService.generarReportePacientesPorEspecialidadPDF(idEspecialidad);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reporte_Pacientes_Especialidad_" + idEspecialidad + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Genera un reporte PDF de estadísticas de doctores y citas.
     *
     * @param idEspecialidad filtro opcional
     * @param estado filtro opcional
     * @return archivo PDF generado
     */
    @GetMapping("/estadisticas/doctores-citas/pdf")
    public ResponseEntity<byte[]> descargarReporteDoctoresCitas(
            @RequestParam(required = false) Integer idEspecialidad,
            @RequestParam(required = false) String estado) {
        try {
            byte[] pdfBytes = reporteService.generarReporteDoctoresCitasPDF(idEspecialidad, estado);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reporte_Doctores_Citas.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
