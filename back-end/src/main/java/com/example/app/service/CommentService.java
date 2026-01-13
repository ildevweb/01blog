package com.example.app.service;

import org.springframework.stereotype.Service;
import com.example.app.repository.CommentRepository;
import com.example.app.dto.CommentInfos;
import com.example.app.entity.User;

import java.util.List;
import java.time.Instant;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthService authService;

    public CommentService(CommentRepository commentRepository, AuthService authService) {
        this.commentRepository = commentRepository;
        this.authService = authService;
    }

    public List<CommentInfos> getByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
            .stream()
            .map(comment -> {
                User user = authService.getUserById(comment.getOwnerId());
                String time = timeAgo(comment.getTime());

                return new CommentInfos(
                    comment.getId(),
                    user.getName(),
                    time,
                    comment.getContent()
                );
            })
            .toList();
    }


    public static String timeAgo(long postSeconds) {

        long now = Instant.now().getEpochSecond();
        long diff = now - postSeconds;

        if (diff < 0) {
            return "just now";
        }

        if (diff < 60) {
            return diff + " seconds ago";
        }

        long minutes = diff / 60;
        if (minutes < 60) {
            return minutes + " minutes ago";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hours ago";
        }

        long days = hours / 24;
        if (days < 30) {
            return days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months + " months ago";
        }

        long years = months / 12;
        return years + " years ago";
    }

}
