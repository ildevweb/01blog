package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.entity.Post;
import com.example.app.repository.PostRepository;

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
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        // 1️⃣ Create upload folder
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 2️⃣ Generate unique file name
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        // 3️⃣ Save image to disk
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // 4️⃣ Save Post to DB
        Post post = new Post();
        post.setContent(content);
        post.setMedia(filePath);

        postRepository.save(post);

        return ResponseEntity.ok("Post created successfully");
    }
}
