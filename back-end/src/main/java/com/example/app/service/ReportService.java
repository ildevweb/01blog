package com.example.app.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.dto.ReportInfos;
import com.example.app.repository.ReportRepository;


@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    

    public List<ReportInfos> getAllReports() {

        return reportRepository.findAll()
            .stream()
            .map(report -> {

                return new ReportInfos(
                    report.getId(),
                    report.getType(),
                    report.getReportedBy(),
                    report.getReportedUser(),
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
}
