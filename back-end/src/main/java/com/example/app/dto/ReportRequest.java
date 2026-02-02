package com.example.app.dto;

public class ReportRequest {

    private Long reportedId;
    private String type;
    private String reason;

    public Long getReportedId() {
        return this.reportedId;
    }

    public void getReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String getReason() {
        return this.reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
