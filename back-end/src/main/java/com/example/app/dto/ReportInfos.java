package com.example.app.dto;

public class ReportInfos {
    private Long id;
    private String type;
    private Long reported_by;
    private Long reported;
    private String reason;
    private String time;
    private String status;

    public ReportInfos(Long id, String type, Long reported_by, Long reported, String reason, String time, String status) {
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
    public Long getReportedBy() {
        return this.reported_by;
    }
    public Long getReported() {
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
