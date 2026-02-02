package com.example.app.controller;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.app.dto.ReportRequest;
import com.example.app.entity.Report;
import com.example.app.entity.User;
import com.example.app.repository.ReportRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;



@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }
    

    @PostMapping("/report")
    public Report report(@RequestBody ReportRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();
        
        User userToReport = userRepository.findById(request.getUserToReport())
                    .orElseThrow(() -> new RuntimeException("User not found"));


        String type = request.getType();
        String reason = request.getReason();
        Long nowSeconds = Instant.now().getEpochSecond();

        Report report = new Report(type, user.getName(), userToReport.getName(), reason, nowSeconds, "pending");

        reportRepository.save(report);

        return report;
    }
}
