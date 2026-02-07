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
import org.springframework.web.server.ResponseStatusException;

import com.example.app.entity.Follow;
import com.example.app.entity.Post;
import com.example.app.entity.Report;
import com.example.app.entity.User;
import com.example.app.entity.Notification;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.PostRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.PostInfos;
import com.example.app.dto.LikeRequest;
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
    private final PostLikeRepository postLikeRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;

    private static final Set<String> ALLOWED_MIME = Set.of(
        "image/png",
        "image/jpeg",
        "image/gif",
        "video/mp4"
    );
    private static final String UPLOAD_DIR = "uploads/";

    public PostController(PostRepository postRepository, PostService postService, PostLikeService postLikeService, PostLikeRepository postLikeRepository, FollowRepository followRepository, NotificationRepository notificationRepository, ReportRepository reportRepository) {
        this.postRepository = postRepository;
        this.postService = postService;
        this.postLikeService = postLikeService;
        this.postLikeRepository = postLikeRepository;
        this.followRepository = followRepository;
        this.notificationRepository = notificationRepository;
        this.reportRepository = reportRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (content.isEmpty() && image == null) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Post cannot be empty"
                )
            );
        }

        if (content.trim().length() > 100) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Post content is too large"
                )
            );
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();
        User currentUser = user.getUser();

        //get time now with second
        Long nowSeconds = Instant.now().getEpochSecond();


        if (image == null) {
            Post post = new Post(content, userId, nowSeconds, "active");
            postRepository.save(post);

            //Save notification to all followers
            List<Follow> followers = followRepository.findByFollowedId(user.getId());

            List<Notification> notifications = followers.stream()
                .map(follower -> new Notification(
                    user.getUser(),
                    follower.getFollower(),
                    false,
                    nowSeconds
                ))
                .toList();

            notificationRepository.saveAll(notifications);

            boolean liked = postLikeService.isLiked(post.getId(), currentUser);
            PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(), content, liked, postLikeRepository.countByPostId(post.getId()), true, "active");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "infos", infos
            ));
        }

        // Create upload folder
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String contentType = image.getContentType(); // MIME type
        if (!ALLOWED_MIME.contains(contentType)) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Invalid file type"
                )
            );
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        

        // Save image to disk
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // Save Post to DB
        Post post = new Post(content, filePath, userId, nowSeconds, "active");

        postRepository.save(post);

        //Save notification to all followers
        List<Follow> followers = followRepository.findByFollowedId(user.getId());

        List<Notification> notifications = followers.stream()
            .map(follower -> new Notification(
                user.getUser(),
                follower.getFollower(),
                false,
                nowSeconds
            ))
            .toList();

        notificationRepository.saveAll(notifications);

        //return a response
        boolean liked = postLikeService.isLiked(post.getId(), currentUser);
        PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(), content, filePath, liked, postLikeRepository.countByPostId(post.getId()), true, "active");

        return ResponseEntity.ok(Map.of(
            "success", true,
            "infos", infos
        ));
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
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();
        
        boolean liked = postLikeService.toggleLike(
            request.getPostId(),
            currentUser
        );

        return ResponseEntity.ok(Map.of(
            "liked", liked
        ));
    }

    @GetMapping("/delete/{id}")
    public void deletePost(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User user = userPrincipal.getUser();

        if (!postRepository.existsByOwnerIdAndId(user.getId(), id) && !user.getRole().equals("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        //notificationRepository.deleteByFromUserOrToUser()
        postLikeRepository.deleteByPostId(id);
        postRepository.deleteById(id);

        List<Report> reports = reportRepository.findByReportedAndType(id, "post");
        for (Report report : reports) {
            report.setStatus("resolved");
            reportRepository.save(report);
        }

    }

    @GetMapping("/ban/{id}")
    public void banPost(@PathVariable Long id) {
        postService.banPost(id);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost(
        @PathVariable Long id,
        @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (content.isEmpty() && image == null) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Post cannot be empty"
                )
            );
        }

        if (content.trim().length() > 100) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Post content is too large"
                )
            );
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        //get post from db
        Post post = postRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Post not found"));

        if (image == null) {
            post.setContent(content);
            post.setOwnerId(userId);
            postRepository.save(post);
            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message", "Post created successfuly"
                )
            );
        }

        // Create upload folder
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String contentType = image.getContentType(); // MIME type
        if (!ALLOWED_MIME.contains(contentType)) {
            return ResponseEntity.ok(
                Map.of(
                    "success", false,
                    "message", "Invalid file type"
                )
            );
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        

        // Save image to disk
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // Save Post to DB
        post.setContent(content);
        post.setMedia(filePath);
        post.setOwnerId(userId);

        postRepository.save(post);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "message", "Post updated successfuly"
            )
        );
    }
}


