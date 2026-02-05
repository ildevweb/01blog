package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Post;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);

    Page<Post> findByOwnerIdInAndStatus(List<Long> ownerIds, String status, Pageable pageable);

    boolean existsByOwnerIdAndId(Long ownerId, Long id);

    Optional<Post> findById(Long id);

    Page<Post> findByOwnerIdIn(List<Long> ownerIds, Pageable pageable);

    @Modifying
    @Transactional
    void deleteById(Long id);

    int countBy();
}
