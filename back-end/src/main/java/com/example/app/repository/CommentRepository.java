package com.example.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.entity.Comment;
import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdOrderByTimeDesc(Long postId, Pageable pageable);

    List<Comment> findByPostId(Long postId);
}
