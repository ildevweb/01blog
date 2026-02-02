package com.example.app.dto;

public class ReportRequest {

    private Long userToReport;
    private String type;
    private String reason;

    public Long getUserToReport() {
        return this.userToReport;
    }

    public void setUserToReport(Long userToReport) {
        this.userToReport = userToReport;
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
