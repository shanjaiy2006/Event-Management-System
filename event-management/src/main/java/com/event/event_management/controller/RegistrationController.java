package com.event.event_management.controller;

import com.event.event_management.entity.Registration;
import com.event.event_management.service.RegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;


@Tag(name = "Registration APIs")
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public Registration register(
            @RequestBody Registration registration) {

        return registrationService.register(registration);
    }

    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=registrations.csv"
        );

        List<Registration> registrations =
                registrationService.getAllRegistrations();

        PrintWriter writer = response.getWriter();

        writer.println(
                "Id,Student Name,Student Email,Event Id"
        );

        for (Registration r : registrations) {

            writer.println(
                    r.getId() + "," +
                            r.getStudentName() + "," +
                            r.getStudentEmail() + "," +
                            r.getEventId()
            );
        }

        writer.flush();
        writer.close();


    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=registrations.xlsx"
        );

        List<Registration> registrations =
                registrationService.getAllRegistrations();

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Registrations");

        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Student Name");
        headerRow.createCell(2).setCellValue("Student Email");
        headerRow.createCell(3).setCellValue("Event ID");

        int rowNum = 1;

        for (Registration registration : registrations) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(registration.getId());

            row.createCell(1)
                    .setCellValue(registration.getStudentName());

            row.createCell(2)
                    .setCellValue(registration.getStudentEmail());

            row.createCell(3)
                    .setCellValue(registration.getEventId());
        }

        workbook.write(response.getOutputStream());

        workbook.close();
    }
}