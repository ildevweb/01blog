package com.example.app.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.app.repository.CommentRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.CommentRequest;
import com.example.app.dto.LikeRequest;
import com.example.app.dto.CommentInfos;
import com.example.app.entity.Comment;
import com.example.app.entity.User;
import com.example.app.service.CommentLikeService;
import com.example.app.service.CommentService;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private CommentRepository commentRepository;
    private CommentService commentService;
    private CommentLikeService commentLikeService;

    public CommentController(CommentRepository commentRepository, CommentService commentService, CommentLikeService commentLikeService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request) throws IOException {
        if (request.getContent().isEmpty() || request.getPostId() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("inputs empty");
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        //get time now with second
        Long nowSeconds = Instant.now().getEpochSecond();

        Comment comment = new Comment(request.getContent(), request.getPostId(), userId, nowSeconds);
        commentRepository.save(comment);
        return ResponseEntity.ok(comment);
    }
    

    @GetMapping("/all")
    public ResponseEntity<List<CommentInfos>> getComments(@RequestParam Long postId) {
        List<CommentInfos> comments = commentService.getByPostId(postId);
        return ResponseEntity.ok(comments);
    }


    @PostMapping("/like")
    public ResponseEntity<?> toggleLike( @RequestBody LikeRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();

        System.out.println("this is the user");
        System.out.println(currentUser.getName());
        System.out.println("this is the comment id");
        System.out.println(request.getCommentId());
        
        boolean liked = commentLikeService.toggleLike(
            request.getCommentId(),
            currentUser
        );

        return ResponseEntity.ok(Map.of(
            "liked", liked
        ));
    }
}
