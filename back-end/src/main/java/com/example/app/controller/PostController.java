package com.example.app.controller;

import java.io.IOException;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.PostInfos;
import com.example.app.dto.LikeRequest;
import com.example.app.service.PostService;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return postService.createPost(content, image);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PostInfos>> getPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<PostInfos> posts = postService.getPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PostInfos>> getMinePosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<PostInfos> minePosts = postService.getMinePosts(page, size);
        return ResponseEntity.ok(minePosts);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PostInfos>> getUserPosts(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<PostInfos> userPosts = postService.getPostsByUser(id, page, size);
        return ResponseEntity.ok(userPosts);
    }

    
    @PostMapping("/like")
    public ResponseEntity<?> toggleLike( @RequestBody LikeRequest request) {
        return postService.toggleLike(request);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @GetMapping("/ban/{id}")
    public ResponseEntity<?> banPost(@PathVariable Long id) {
        return postService.banPost(id);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost(
        @PathVariable Long id,
        @RequestParam("content") String content,
        @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return postService.updatePost(id, content, image);
    }
}


