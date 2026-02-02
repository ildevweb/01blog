package com.example.app.dto;

public class ReportInfos {
    private Long id;
    private String type;
    private String reported_by;
    private String reported;
    private String reason;
    private String time;
    private String status;

    public ReportInfos(Long id, String type, String reported_by, String reported, String reason, String time, String status) {
        this.id = id;
        this.type = type;
        this.reported_by = reported_by;
        this.reported = reported;
        this.reason = reason;
        this.time = time;
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }
    public String getType() {
        return this.type;
    }
    public String getReportedBy() {
        return this.reported_by;
    }
    public String getReported() {
        return this.reported;
    }
    public String getReason() {
        return this.reason;
    }
    public String getTime() {
        return this.time;
    }
    public String getStatus() {
        return this.status;
    }

}
