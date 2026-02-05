package com.example.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.entity.Report;
import java.util.List;


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    int countByStatus(String status);

    List<Report> findByReportedAndType(Long reported, String type);

    Page<Report> findAll(Pageable pageable);

}
