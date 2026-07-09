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

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;
    private final CitaRepository citaRepository;
    private final ReporteAfluenciaService afluenciaService;

    // Constructor con todas las dependencias inyectadas correctamente
    public ReporteController(ReporteService reporteService, CitaRepository citaRepository, ReporteAfluenciaService afluenciaService) {
        this.reporteService = reporteService;
        this.citaRepository = citaRepository;
        this.afluenciaService = afluenciaService;
    }

    // ==========================================
    // ENDPOINT: CONTROL DE FLUJO INTERACTIVO
    // ==========================================
    @GetMapping("/afluencia")
    public ResponseEntity<List<ReporteAfluenciaDTO>> getAfluencia() {
        return ResponseEntity.ok(afluenciaService.obtenerReporteAfluencia());
    }

    // ==========================================
    // ENDPOINTS DE REPORTES Y DESCARGAS EN PDF
    // ==========================================
    @GetMapping("/doctores/pdf")
    public ResponseEntity<byte[]> descargarDirectorioDoctores() {
        try {
            byte[] pdfBytes = reporteService.generarDirectorioDoctoresPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Directorio_Doctores.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/citas/{idCita}/pdf")
    public ResponseEntity<byte[]> descargarReporteCita(@PathVariable Integer idCita) {
        try {
            byte[] pdfBytes = reporteService.generarReporteCitaPDF(idCita);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Comprobante_Cita_" + idCita + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/especialidades/pdf")
    public ResponseEntity<byte[]> descargarReporteEspecialidades() {
        try {
            byte[] pdfBytes = reporteService.generarReporteEspecialidadesPDF();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reporte_Especialidades.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================
    // ENDPOINTS DE ESTADÍSTICAS ADICIONALES
    // ==========================================
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
                        (java.math.BigDecimal) r[5]
                ))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

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

    @GetMapping("/estadisticas/ingresos-especialidad")
    public List<EspecialidadDTO> especialidadDTOS(){
        List<Object[]> datos = citaRepository.ingresosEspecialidad();
        List<EspecialidadDTO> lista = new ArrayList<>();

        for(Object[] fila : datos){
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
    @GetMapping("/pacientes-especialidad/{idEspecialidad}/pdf")
    public ResponseEntity<byte[]> descargarReportePacientesEspecialidad(@PathVariable Integer idEspecialidad) {
        try {
            byte[] pdfBytes = reporteService.generarReportePacientesPorEspecialidadPDF(idEspecialidad);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reporte_Pacientes_Especialidad_" + idEspecialidad + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}