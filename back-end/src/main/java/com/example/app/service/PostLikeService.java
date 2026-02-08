package com.example.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.example.app.repository.PostLikeRepository;
import com.example.app.repository.PostRepository;
import com.example.app.entity.User;
import com.example.app.entity.Post;
import com.example.app.entity.PostLike;


@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public boolean toggleLike(Long postId, User currentUser) {

        Optional<PostLike> existingLike =
            postLikeRepository.findByPostIdAndUserId(postId, currentUser.getId());

        if (existingLike.isPresent()) {
            // UNLIKE
            postLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // LIKE
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return false;
            }

            PostLike like = new PostLike();
            like.setPost(postOpt.get());
            like.setUser(currentUser);

            postLikeRepository.save(like);
            return true;
        }
    }

    public boolean isLiked(Long postId, User currentUser) {
        Optional<PostLike> existingLike =
            postLikeRepository.findByPostIdAndUserId(postId, currentUser.getId());

        if (existingLike.isPresent()) {
            return true;
        } else {
            return false;
        }
    }
}
