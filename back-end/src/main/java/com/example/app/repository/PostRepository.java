package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Post;

import java.util.*;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerIdAndStatusOrderByTimeDesc(Long ownerId, String status);

    List<Post> findByStatusOrderByTimeDesc(String status);

    boolean existsByOwnerIdAndId(Long ownerId, Long id);

    Optional<Post> findById(Long id);

    @Modifying
    @Transactional
    void deleteById(Long id);

    int countBy();
}
