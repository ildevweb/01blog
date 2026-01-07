package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.entity.Post;
import com.example.app.repository.PostRepository;
import com.example.app.security.UserPrincipal;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostRepository postRepository;

    private static final String UPLOAD_DIR = "uploads/";

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
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
            postInfos infos = new postInfos(user.getUsername(), nowSeconds, content);
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

        postInfos infos = new postInfos(user.getUsername(), nowSeconds, content, filePath);

        return ResponseEntity.ok(infos);
    }
}


class postInfos {
    private String username;
    private Long time;
    private String content;
    private String media;

    public postInfos(String username, Long time, String content, String media) {
        this.username = username;
        this.time = time;
        this.content = content;
        this.media = media;
    }

    public postInfos(String username, Long time, String content) {
        this.username = username;
        this.time = time;
        this.content = content;
    }

    public String getUsername() {
        return this.username;
    }
    public Long getTime() {
        return this.time;
    }
    public String getContent() {
        return this.content;
    }
    public String getMedia() {
        return this.media;
    }

}
