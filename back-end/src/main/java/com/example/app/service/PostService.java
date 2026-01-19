package com.example.app.service;

import org.springframework.stereotype.Service;
import com.example.app.repository.PostRepository;
import com.example.app.dto.PostInfos;
import com.example.app.entity.User;

import java.util.List;
import java.time.Instant;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
    }

    public List<PostInfos> getAllPosts() {
        return postRepository.findAll()
            .stream()
            .map(post -> {
                User user = authService.getUserById(post.getOwnerId());
                String time = timeAgo(post.getTime());

                return new PostInfos(
                    post.getId(),
                    user.getName(),
                    time,
                    post.getContent(),
                    post.getMedia()
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
