package com.example.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.PostRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.PostInfos;
import com.example.app.entity.User;
import com.example.app.entity.Post;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.time.Instant;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository, PostLikeService postLikeService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postLikeService = postLikeService;
        this.userRepository = userRepository;
    }

    public List<PostInfos> getAllPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User user = userPrincipal.getUser();

        if (user.getRole().equals("admin")) {
            return postRepository.findAll(Sort.by(Sort.Direction.DESC, "time"))
                .stream()
                .map(post -> {
                    String time = timeAgo(post.getTime());

                    boolean liked = postLikeService.isLiked(post.getId(), user);

                    User owner = userRepository.findById(post.getOwnerId())
                        .orElseThrow(() -> new RuntimeException("Owner not found"));

                    return new PostInfos(
                        post.getId(),
                        owner.getName(),
                        time,
                        post.getContent(),
                        post.getMedia(),
                        liked,
                        postLikeRepository.countByPostId(post.getId()),
                        postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                        post.getStatus()
                    );
                })
                .toList();
        } else {
            return postRepository.findByStatusOrderByTimeDesc("active")
                .stream()
                .map(post -> {
                    String time = timeAgo(post.getTime());

                    boolean liked = postLikeService.isLiked(post.getId(), user);

                    User owner = userRepository.findById(post.getOwnerId())
                        .orElseThrow(() -> new RuntimeException("Owner not found"));

                    return new PostInfos(
                        post.getId(),
                        owner.getName(),
                        time,
                        post.getContent(),
                        post.getMedia(),
                        liked,
                        postLikeRepository.countByPostId(post.getId()),
                        postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                        post.getStatus()
                    );
                })
                .toList();
        }

    }


    public List<PostInfos> getMinePosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        Long userId = loggedUser.getId();
        User user = loggedUser.getUser();

        return postRepository.findByOwnerIdAndStatusOrderByTimeDesc(userId, "active")
            .stream()
            .map(post -> {
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                User owner = userRepository.findById(post.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));

                return new PostInfos(
                    post.getId(),
                    owner.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId()),
                    postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                    post.getStatus()
                );
            })
            .toList();
    }


    public List<PostInfos> getPostsByUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();


        return postRepository.findByOwnerIdAndStatusOrderByTimeDesc(userId, "active")
            .stream()
            .map(post -> {
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                User owner = userRepository.findById(post.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));

                return new PostInfos(
                    post.getId(),
                    owner.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId()),
                    postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                    post.getStatus()
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


    public void banPost(Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        if (!postRepository.existsById(postId) || !user.getRole().equals("admin")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getStatus().equals("active")) {
            post.setStatus("banned");
        } else if (post.getStatus().equals("banned")) {
            post.setStatus("active");
        }

        postRepository.save(post);
    }
}
