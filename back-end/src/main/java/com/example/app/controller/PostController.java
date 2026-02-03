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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("inputs empty");
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

        return ResponseEntity.ok(infos);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PostInfos>> getPosts() {
        List<PostInfos> allPosts = postService.getAllPosts();
        return ResponseEntity.ok(allPosts);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PostInfos>> getMinePosts() {
        List<PostInfos> minePosts = postService.getMinePosts();
        return ResponseEntity.ok(minePosts);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PostInfos>> getUserPosts(@PathVariable Long id) {
        List<PostInfos> userPosts = postService.getPostsByUser(id);
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

        reportRepository.findByReportedAndType(id, "post")
        .ifPresent(report -> {
            report.setStatus("resolved");
            reportRepository.save(report);
        });

    }

    @GetMapping("/ban/{id}")
    public void banPost(@PathVariable Long id) {
        postService.banPost(id);
    }


    @PostMapping("/update/{id}")
    public void updatePost(
        @PathVariable Long id,
        @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (content.isEmpty() && image == null) {
            System.out.println("inputs are empty");
            new RuntimeException("inputs empty");
            return;
        }

        //get owner id
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        //get time now with second
        Long nowSeconds = Instant.now().getEpochSecond();

        //get post from db
        Post post = postRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Post not found"));

        if (image == null) {
            post.setContent(content);
            post.setOwnerId(userId);
            post.setTime(nowSeconds);
            postRepository.save(post);
            return;
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
        post.setContent(content);
        post.setMedia(filePath);
        post.setOwnerId(userId);
        post.setTime(nowSeconds);

        postRepository.save(post);
    }
}


