package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.entity.Post;
import com.example.app.entity.User;
import com.example.app.repository.PostRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.PostInfos;
import com.example.app.dto.LikePostRequest;
import com.example.app.service.PostService;
import com.example.app.service.PostLikeService;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostRepository postRepository;
    private final PostService postService;
    private final PostLikeService postLikeService;

    private static final String UPLOAD_DIR = "uploads/";

    public PostController(PostRepository postRepository, PostService postService, PostLikeService postLikeService) {
        this.postRepository = postRepository;
        this.postService = postService;
        this.postLikeService = postLikeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (content.isEmpty() && image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("inputs empty");
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        //get time now with second
        Long nowSeconds = Instant.now().getEpochSecond();

        if (image == null) {
            Post post = new Post(content, userId, nowSeconds);
            postRepository.save(post);
            PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(), content);
            return ResponseEntity.ok(infos);
        }

        // Create upload folder
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        

        // Save image to disk
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // Save Post to DB
        Post post = new Post(content, filePath, userId, nowSeconds);

        postRepository.save(post);

        PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(), content, filePath);

        return ResponseEntity.ok(infos);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PostInfos>> getPosts() {
        List<PostInfos> allPosts = postService.getAllPosts();
        return ResponseEntity.ok(allPosts);
    }

    @PostMapping("/like")
    public ResponseEntity<?> toggleLike( @RequestBody LikePostRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();
        
        System.out.println("this is the current user:");
        System.out.println(currentUser.getName());
        boolean liked = postLikeService.toggleLike(
            request.getPostId(),
            currentUser
        );

        return ResponseEntity.ok(Map.of(
            "liked", liked
        ));
    }
}


