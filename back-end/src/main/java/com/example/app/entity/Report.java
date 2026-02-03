package com.example.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    @Column(name = "reported_by")
    private Long reported_by;
    @Column(name = "reported")
    private Long reported;
    private String reason;
    private Long time;
    private String status;

    public Report() {
    }

    public Report(String type, Long reported_by, Long reported, String reason, Long time, String status) {
        this.type = type;
        this.reported_by = reported_by;
        this.reported = reported;
        this.reason = reason;
        this.time = time;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReported(Long reported) {
        this.reported = reported;
    }

    public Long getReported() {
        return this.reported;
    }

    public void setReportedBy(Long reported_by) {
        this.reported_by = reported_by;
    }

    public Long getReportedBy() {
        return this.reported_by;
    }

    public String getReason() {
        return this.reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
