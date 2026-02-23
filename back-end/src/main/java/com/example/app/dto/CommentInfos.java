package com.example.app.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentInfos {
    private Long id;
    private String username;
    private boolean mine;
    private String time;
    private String content;
    private boolean liked;
    private Long count;
}
