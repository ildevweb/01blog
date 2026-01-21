package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.entity.Follow;
import java.util.Optional;


public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    long countByFollowedId(Long followedId); // followers count
}