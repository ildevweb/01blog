package com.example.app.controller;


import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.CommentRequest;
import com.example.app.dto.LikeRequest;
import com.example.app.dto.CommentInfos;
import com.example.app.service.CommentService;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
        return commentService.createComment(request);
    }
    

    @GetMapping("/all")
    public ResponseEntity<List<CommentInfos>> getComments(
        @RequestParam Long postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<CommentInfos> comments = commentService.getByPostId(postId, page, size);
        return ResponseEntity.ok(comments);
    }


    @PostMapping("/like")
    public ResponseEntity<?> toggleLike( @RequestBody LikeRequest request) {
        return commentService.likeComment(request);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete( @RequestBody LikeRequest request) {
        return commentService.deleteComment(request);
    }
}
