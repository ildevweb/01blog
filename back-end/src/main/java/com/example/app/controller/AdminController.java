package com.example.app.controller;

import org.springframework.web.bind.annotation.*;

import com.example.app.repository.PostRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.repository.UserRepository;
import com.example.app.service.ReportService;
import com.example.app.dto.ReportInfos;

import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    public AdminController(UserRepository userRepository, PostRepository postRepository, ReportService reportService, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/getData")
    public ResponseEntity<Data> getData() {

        int usersCount = userRepository.countBy();
        int postsCount = postRepository.countBy();
        int reportsCount = reportRepository.countByStatus("pending");

        Data data = new Data(usersCount, postsCount, reportsCount);


        return ResponseEntity.ok(data);
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




class Data {
    private int usersCount;
    private int postsCount;
    private int reportsCount;

    public Data(int usersCount, int postsCount, int reportsCount) {
        this.usersCount = usersCount;
        this.postsCount = postsCount;
        this.reportsCount = reportsCount;
    }

    public int getUsersCount() {
        return this.usersCount;
    }
    public int getPostsCount() {
        return this.postsCount;
    }
    public int getReportsCount() {
        return this.reportsCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }
    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }
    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }
}