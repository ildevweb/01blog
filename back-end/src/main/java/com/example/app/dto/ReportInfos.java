package com.example.app.dto;


import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class ReportInfos {
    private Long id;
    private String type;
    private Long reported_by;
    private Long reported;
    private String reason;
    private String time;
    private String status;
}
