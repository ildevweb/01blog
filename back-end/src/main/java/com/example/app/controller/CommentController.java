package com.example.app.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.app.repository.CommentRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.CommentRequest;
import com.example.app.dto.CommentInfos;
import com.example.app.entity.Comment;
import com.example.app.service.CommentService;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private CommentRepository commentRepository;
    private CommentService commentService;

    public CommentController(CommentRepository commentRepository, CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
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
    public ResponseEntity<List<CommentInfos>> getComments(
            @RequestParam Long postId
    ) {
        List<CommentInfos> comments = commentService.getByPostId(postId);
        return ResponseEntity.ok(comments);
    }

}
