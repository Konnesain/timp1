package timp.controller;

import timp.service.SecurityReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/buildings-report")
public class ReportController {

    private final SecurityReportService securityReportService;

    public ReportController(SecurityReportService securityReportService) {
        this.securityReportService = securityReportService;
    }

    @GetMapping(value = "/{buildingId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getBuildingReport(@PathVariable Long buildingId) {
        String report = securityReportService.generateBuildingReport(buildingId);
        String filename = "security-report-building-" + buildingId + "-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(report);
    }
}