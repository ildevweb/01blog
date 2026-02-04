package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Post;

import java.util.*;
import org.springframework.data.domain.Sort;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerIdAndStatusOrderByTimeDesc(Long ownerId, String status);

    List<Post> findByOwnerIdInAndStatusOrderByTimeDesc(List<Long> ownerIds, String status);

    boolean existsByOwnerIdAndId(Long ownerId, Long id);

    Optional<Post> findById(Long id);

    List<Post> findByOwnerIdIn(List<Long> ownerIds, Sort sort);

    @Modifying
    @Transactional
    void deleteById(Long id);

    int countBy();
}
