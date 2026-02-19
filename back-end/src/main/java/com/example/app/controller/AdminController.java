package com.example.app.controller;

import org.springframework.web.bind.annotation.*;

import com.example.app.service.ReportService;
import com.example.app.dto.ReportInfos;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    
    private final ReportService reportService;

    @GetMapping("/getData")
    public ResponseEntity<?> getData() {
        return reportService.getData();
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportInfos>> getReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<ReportInfos> reports = reportService.getAllReports(page, size);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/dismiss/{id}")
    public ResponseEntity<?> dismissReport(@PathVariable Long id) {
        return reportService.dismissReport(id);
    }
}

