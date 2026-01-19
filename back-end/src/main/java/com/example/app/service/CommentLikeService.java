package com.example.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.example.app.repository.CommentLikeRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.entity.User;
import com.example.app.entity.Comment;
import com.example.app.entity.CommentLike;


@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public boolean toggleLike(Long commentId, User currentUser) {

        Optional<CommentLike> existingLike =
            commentLikeRepository.findByCommentIdAndUserId(commentId, currentUser.getId());

        if (existingLike.isPresent()) {
            // UNLIKE
            commentLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // LIKE
            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

            CommentLike like = new CommentLike();
            like.setComment(comment);
            like.setUser(currentUser);

            commentLikeRepository.save(like);
            return true;
        }
    }

    public boolean isLiked(Long commentId, User currentUser) {
        Optional<CommentLike> existingLike =
            commentLikeRepository.findByCommentIdAndUserId(commentId, currentUser.getId());

        if (existingLike.isPresent()) {
            return true;
        } else {
            commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
            
            return false;
        }
    }
}
