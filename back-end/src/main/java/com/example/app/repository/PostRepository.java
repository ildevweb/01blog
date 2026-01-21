package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.entity.Post;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerId(Long ownerId);
}
