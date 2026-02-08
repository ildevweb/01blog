package com.example.app.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.app.dto.ReportInfos;
import com.example.app.entity.Report;
import com.example.app.entity.User;
import com.example.app.repository.ReportRepository;
import com.example.app.security.UserPrincipal;

import java.util.Map;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    

    public List<ReportInfos> getAllReports(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        return reportRepository.findAll(pageable)
            .stream()
            .map(report -> {

                return new ReportInfos(
                    report.getId(),
                    report.getType(),
                    report.getReportedBy(),
                    report.getReported(),
                    report.getReason(),
                    timeAgo(report.getTime()),
                    report.getStatus()
                );
            })
            .toList();
    }

    public static String timeAgo(long postSeconds) {

        long now = Instant.now().getEpochSecond();
        long diff = now - postSeconds;

        if (diff < 0) {
            return "just now";
        }

        if (diff < 60) {
            return diff + " seconds ago";
        }

        long minutes = diff / 60;
        if (minutes < 60) {
            return minutes + " minutes ago";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hours ago";
        }

        long days = hours / 24;
        if (days < 30) {
            return days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months + " months ago";
        }

        long years = months / 12;
        return years + " years ago";
    }


    public ResponseEntity<?> dismissReport(Long reportId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        if (!reportRepository.existsById(reportId)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Report not found"
            ));
        }

        if (!user.getRole().equals("admin")) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Access only to admin"
            ));
        }

        Report report = reportRepository.findById(reportId)
        .orElse(new Report());

        report.setStatus("dismissed");

        reportRepository.save(report);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Reported successfuly"
        ));
    }
}
