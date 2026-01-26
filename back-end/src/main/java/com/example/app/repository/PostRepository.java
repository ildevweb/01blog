package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Post;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerIdOrderByTimeDesc(Long ownerId);

    boolean existsByOwnerIdAndId(Long ownerId, Long id);

    @Modifying
    @Transactional
    void deleteByIdAndOwnerId(Long id, Long ownerId);
}
