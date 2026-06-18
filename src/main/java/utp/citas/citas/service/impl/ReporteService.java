package utp.citas.citas.service.impl;

import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReporteService {

    private final DataSource dataSource;

    public ReporteService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] generarDirectorioDoctoresPDF() throws Exception {
        // 1. Usar ClassPathResource e InputStream (A prueba de archivos .jar)
        java.io.InputStream jrxmlInput = new org.springframework.core.io.ClassPathResource("reportes/reporte_doctores.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInput);

        // 2. Mapa vacío porque es un reporte estático
        Map<String, Object> parameters = new HashMap<>();

        // 3. Llenar el reporte usando la conexión directa a PostgreSQL
        try (Connection conn = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            // 4. Retornar el PDF en arreglo de bytes
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}