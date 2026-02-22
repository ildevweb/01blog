package com.example.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.repository.FollowRepository;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.PostRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.UserPrincipal;
import com.example.app.dto.LikeRequest;
import com.example.app.dto.PostInfos;
import com.example.app.entity.User;
import com.example.app.entity.Follow;
import com.example.app.entity.Post;
import com.example.app.entity.Report;
import com.example.app.entity.Notification;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.apache.tika.Tika;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    private static final Set<String> ALLOWED_MIME = Set.of(
        "image/png",
        "image/jpeg",
        "image/gif",
        "video/mp4"
    );
    private static final String UPLOAD_DIR = "uploads/";

    public ResponseEntity<?> createPost(String content, MultipartFile image) throws IOException {
        if (content.isEmpty() && image == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post cannot be empty"
            ));
        }

        if (content.trim().length() > 100) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post content is too large"
            ));
        }

        // --- Authentication stuff ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();
        User currentUser = user.getUser();
        Long nowSeconds = Instant.now().getEpochSecond();

        if (image == null) {
            Post post = new Post(content, userId, nowSeconds, "active");
            postRepository.save(post);
            saveNotifications(user.getUser(), nowSeconds);

            boolean liked = postLikeService.isLiked(post.getId(), currentUser);
            PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(),
                    content, liked, postLikeRepository.countByPostId(post.getId()), true, "active");

            return ResponseEntity.ok(Map.of("success", true, "infos", infos));
        }

        // --- Create upload folder ---
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // --- Use Tika to detect MIME type ---
        Tika tika = new Tika();
        String detectedType = tika.detect(image.getInputStream());

        if (!ALLOWED_MIME.contains(detectedType)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Invalid file type"
            ));
        }

        // --- Save file ---
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // --- Save post ---
        Post post = new Post(content, filePath, userId, nowSeconds, "active");
        postRepository.save(post);
        saveNotifications(user.getUser(), nowSeconds);

        // --- Return response ---
        boolean liked = postLikeService.isLiked(post.getId(), currentUser);
        PostInfos infos = new PostInfos(post.getId(), user.getUsername(), nowSeconds.toString(),
                content, filePath, liked, postLikeRepository.countByPostId(post.getId()), true, "active");

        return ResponseEntity.ok(Map.of("success", true, "infos", infos));
    }

    // save notifications
    private void saveNotifications(User owner, Long timestamp) {
        List<Follow> followers = followRepository.findByFollowedId(owner.getId());
        List<Notification> notifications = followers.stream()
                .map(follower -> new Notification(owner, follower.getFollower(), false, timestamp))
                .toList();
        notificationRepository.saveAll(notifications);
    }


    public ResponseEntity<?> updatePost(Long id, String content, MultipartFile image) throws IOException {
        if (content.isEmpty() && image == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post cannot be empty"
            ));
        }

        if (content.trim().length() > 100) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post content is too large"
            ));
        }

        // --- Get owner id ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        Long userId = user.getId();

        // --- Fetch post from DB ---
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // --- If no new image, update content only ---
        if (image == null) {
            post.setContent(content);
            post.setOwnerId(userId);
            postRepository.save(post);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Post updated successfully"
            ));
        }

        // --- Ensure upload folder exists ---
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // --- Use Tika to detect MIME type ---
        Tika tika = new Tika();
        String detectedType = tika.detect(image.getInputStream());

        if (!ALLOWED_MIME.contains(detectedType)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Invalid file type"
            ));
        }

        // --- Generate unique filename and save ---
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;
        Files.copy(image.getInputStream(), Paths.get(filePath));

        // --- Update post ---
        post.setContent(content);
        post.setMedia(filePath);
        post.setOwnerId(userId);
        postRepository.save(post);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Post updated successfully"
        ));
    }

    public ResponseEntity<?> deletePost(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User user = userPrincipal.getUser();

        if (!postRepository.existsByOwnerIdAndId(user.getId(), id) && !user.getRole().equals("admin")) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post undefined or you don't have permit to delete it"
            ));
        }

        //notificationRepository.deleteByFromUserOrToUser()
        postLikeRepository.deleteByPostId(id);
        postRepository.deleteById(id);

        List<Report> reports = reportRepository.findByReportedAndType(id, "post");
        for (Report report : reports) {
            report.setStatus("resolved");
            reportRepository.save(report);
        }

        return ResponseEntity.ok(Map.of(
            "success", true
        ));
    }

    public ResponseEntity<?> toggleLike(LikeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        User currentUser = user.getUser();
        
        boolean liked = postLikeService.toggleLike(
            request.getPostId(),
            currentUser
        );

        return ResponseEntity.ok(Map.of(
            "success", true,
            "liked", liked
        ));
    }

    public List<PostInfos> getPosts(int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        User user = userPrincipal.getUser();

        // Get followings + own ID
        List<Long> ownerIds = followRepository.findByFollowerId(user.getId()).stream()
            .map(f -> f.getFollowed().getId())
            .collect(Collectors.toList());
        ownerIds.add(user.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        Page<Post> postPage;
        if (user.getRole().equals("admin")) {
            postPage = postRepository.findAll(pageable);
        } else {
            postPage = postRepository.findByOwnerIdInAndStatus(ownerIds, "active", pageable);
        }

        return postPage.getContent().stream()
            .map(post -> {
                String time = timeAgo(post.getTime());
                boolean liked = postLikeService.isLiked(post.getId(), user);
                User owner = userRepository.findById(post.getOwnerId())
                .orElse(new User("Deleted user"));

                return new PostInfos(
                    post.getId(),
                    owner.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId()),
                    postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                    post.getStatus()
                );
            })
            .toList();
    }



    public List<PostInfos> getMinePosts(int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        Long userId = loggedUser.getId();
        User user = loggedUser.getUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        return postRepository.findByOwnerIdAndStatus(userId, "active", pageable)
            .stream()
            .map(post -> {
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                User owner = userRepository.findById(post.getOwnerId())
                .orElse(new User("Deleted user"));


                return new PostInfos(
                    post.getId(),
                    owner.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId()),
                    postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                    post.getStatus()
                );
            })
            .toList();
    }


    public List<PostInfos> getPostsByUser(Long userId, int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        return postRepository.findByOwnerIdAndStatus(userId, "active", pageable)
            .stream()
            .map(post -> {
                String time = timeAgo(post.getTime());

                boolean liked = postLikeService.isLiked(post.getId(), user);

                User owner = userRepository.findById(post.getOwnerId())
                .orElse(new User("Deleted user"));


                return new PostInfos(
                    post.getId(),
                    owner.getName(),
                    time,
                    post.getContent(),
                    post.getMedia(),
                    liked,
                    postLikeRepository.countByPostId(post.getId()),
                    postRepository.existsByOwnerIdAndId(user.getId(), post.getId()),
                    post.getStatus()
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


    public ResponseEntity<?> banPost(Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal loggedUser = (UserPrincipal) auth.getPrincipal();
        User user = loggedUser.getUser();

        if (!postRepository.existsById(postId) || !user.getRole().equals("admin")) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Post undefined or you don't have permit to ban it"
            ));
        }

        Post post = postRepository.findById(postId)
        .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getStatus().equals("active")) {
            post.setStatus("banned");
        } else if (post.getStatus().equals("banned")) {
            post.setStatus("active");
        }

        postRepository.save(post);


        List<Report> reports = reportRepository.findByReportedAndType(postId, "post");

        for (Report report : reports) {
            report.setStatus("resolved");
            reportRepository.save(report);
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "successful ban post"
        ));
    }
}
