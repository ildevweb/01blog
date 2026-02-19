package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.ReportRequest;
import com.example.app.service.ReportService;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {
    private final ReportService reportService;
    

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestBody ReportRequest request) {
        return reportService.report(request);
    }
}
