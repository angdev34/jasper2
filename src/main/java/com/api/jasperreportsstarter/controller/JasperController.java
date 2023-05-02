package com.api.jasperreportsstarter.controller;

import com.api.jasperreportsstarter.entity.Lesson;
import com.api.jasperreportsstarter.entity.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/reports")
public class JasperController {
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdfReport() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/templates/pdf_template.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        File jsonFile = new File(getClass().getClassLoader().getResource("datasource/lesson.JSON").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Lesson> lessons  = objectMapper.readValue(jsonFile, new TypeReference<List<Lesson>>(){});

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lessons);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);


        headers.setContentDispositionFormData("report", "example.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/excel")
    public void generateExcelReport(HttpServletResponse response) throws Exception {

        InputStream inputStream = getClass().getResourceAsStream("/templates/excel_template.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        File jsonFile = new File(getClass().getClassLoader().getResource("datasource/grade.JSON").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Student> students  = objectMapper.readValue(jsonFile, new TypeReference<List<Student>>(){});

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);
        Map<String, Object> params = new HashMap<>();
        params.put("lesson", "Bilgisayar Ağları");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        JRXlsxExporter exporter = new JRXlsxExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=lesson-report.xlsx");
        response.setContentLength(baos.size());
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }

    @GetMapping("/doc")
    public void generateDocReport(HttpServletResponse response) throws Exception{
        InputStream inputStream = getClass().getResourceAsStream("/templates/doc_template.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        File jsonFile = new File(getClass().getClassLoader().getResource("datasource/student.JSON").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parameters = objectMapper.readValue(jsonFile, Map.class);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(parameters));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-disposition", "attachment; filename=letter.docx");

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
        exporter.exportReport();
    }
}

