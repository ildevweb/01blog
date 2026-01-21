package com.example.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.PostRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.PostInfos;
import com.example.app.entity.User;

import java.util.List;
import java.time.Instant;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService authService;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;

    public PostService(PostRepository postRepository, AuthService authService, PostLikeRepository postLikeRepository, PostLikeService postLikeService) {
        this.postRepository = postRepository;
        this.authService = authService;
        this.postLikeRepository = postLikeRepository;
        this.postLikeService = postLikeService;
    }

    public List<PostInfos> getAllPosts() {
        return postRepository.findAll()
            .stream()
            .map(post -> {
                User user = authService.getUserById(post.getOwnerId());
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                return new PostInfos(
                    post.getId(),
                    user.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId())
                );
            })
            .toList();
    }


    public List<PostInfos> getMinePosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        Long userId = loggedUser.getId();

        return postRepository.findByOwnerId(userId)
            .stream()
            .map(post -> {
                User user = authService.getUserById(post.getOwnerId());
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                return new PostInfos(
                    post.getId(),
                    user.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId())
                );
            })
            .toList();
    }


    public List<PostInfos> getPostsByUser(Long userId) {

        return postRepository.findByOwnerId(userId)
            .stream()
            .map(post -> {
                User user = authService.getUserById(post.getOwnerId());
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                return new PostInfos(
                    post.getId(),
                    user.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId())
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
