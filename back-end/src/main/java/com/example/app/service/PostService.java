package com.example.app.service;

import org.springframework.stereotype.Service;
import com.example.app.repository.PostRepository;
import com.example.app.dto.PostInfos;
import com.example.app.entity.User;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
    }

    public List<PostInfos> getAllPosts() {
        return postRepository.findAll()
            .stream()
            .map(post -> {
                User user = authService.getUserById(post.getOwnerId());

                return new PostInfos(
                    user.getName(),
                    post.getTime(),
                    post.getContent(),
                    post.getMedia()
                );
            })
            .toList();
    }
}
