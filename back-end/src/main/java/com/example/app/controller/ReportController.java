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
import com.example.app.entity.Post;
import com.example.app.repository.ReportRepository;
import com.example.app.repository.UserRepository;
import com.example.app.repository.PostRepository;
import com.example.app.security.UserPrincipal;



@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository, PostRepository postRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    

    @PostMapping("/report")
    public Report report(@RequestBody ReportRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        String type = request.getType();
        String reason = request.getReason();
        Long nowSeconds = Instant.now().getEpochSecond();

        if (type.equals("user")) {
            User userToReport = userRepository.findById(request.getReportedId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
            Report report = new Report(type, user.getId(), userToReport.getId(), reason, nowSeconds, "pending");
            reportRepository.save(report);
            return report;
        }
        

        Post postToReport = postRepository.findById(request.getReportedId()).orElseThrow(() -> new RuntimeException("Post not found"));
        Report report = new Report(type, user.getId(), postToReport.getId(), reason, nowSeconds, "pending");
        reportRepository.save(report);
        return report;
    }
}
