package com.example.app.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class NotificationInfos {
    private Long id;
    private String username;
    private Long userId;
    private String time;
    private String content;
    private boolean readed;
}
