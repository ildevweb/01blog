package com.example.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.app.repository.CommentLikeRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.CommentInfos;
import com.example.app.dto.CommentRequest;
import com.example.app.dto.LikeRequest;
import com.example.app.entity.User;
import com.example.app.entity.Comment;

import java.util.List;
import java.util.Map;
import java.time.Instant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;


    public ResponseEntity<?> createComment(CommentRequest request) {
        if (request.getContent().isEmpty() || request.getPostId() == 0) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "input cannot be empty"
            ));
        }

        if (request.getContent().trim().length() > 100) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Max 100 character in comment"
            ));
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        //get time now with second
        Long nowSeconds = Instant.now().getEpochSecond();

        Comment comment = new Comment(request.getContent(), request.getPostId(), userId, nowSeconds);
        commentRepository.save(comment);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Comment created successfuly"
        ));
    }

    public ResponseEntity<?> likeComment(LikeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();
        
        boolean liked = commentLikeService.toggleLike(
            request.getCommentId(),
            currentUser
        );

        return ResponseEntity.ok(Map.of(
            "liked", liked
        ));
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
