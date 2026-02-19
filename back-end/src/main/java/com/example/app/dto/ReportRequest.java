package com.example.app.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class ReportRequest {

    private Long reportedId;
    private String type;
    private String reason;
}
