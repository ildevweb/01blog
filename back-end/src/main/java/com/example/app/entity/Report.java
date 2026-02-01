package com.example.app.entity;

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
    private String reported_by;
    private String reason;
    private String time;
    private String status;

    public Report() {
    }

    public Report(String type, String reported_by, String reason, String time, String status) {
        this.type = type;
        this.reported_by = reported_by;
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

    public void setReportedBy(String reported_by) {
        this.reported_by = reported_by;
    }

    public String getReportedBy() {
        return this.reported_by;
    }

    public String getReason() {
        return this.reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
