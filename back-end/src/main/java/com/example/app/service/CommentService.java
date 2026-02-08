package com.example.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.app.repository.CommentLikeRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.CommentInfos;
import com.example.app.entity.User;

import java.util.List;
import java.time.Instant;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CommentLikeService commentLikeService, CommentLikeRepository commentLikeRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeService = commentLikeService;
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
    }

    public List<CommentInfos> getByPostId(Long postId, int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        return commentRepository.findByPostIdOrderByTimeDesc(postId, pageable)
            .stream()
            .map(comment -> {
                String time = timeAgo(comment.getTime());

                boolean liked = commentLikeService.isLiked(comment.getId(), user);

                User owner = userRepository.findById(comment.getOwnerId())
                    .orElse(new User("deleted user"));

                return new CommentInfos(
                    comment.getId(),
                    owner.getName(),
                    time,
                    comment.getContent(),
                    liked,
                    commentLikeRepository.countByCommentId(comment.getId())
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
