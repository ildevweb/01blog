package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.entity.Follow;
import java.util.Optional;
import java.util.List;


public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    List<Follow> findByFollowedId(Long followedId); //get followers

    List<Follow> findByFollowerId(Long followerId); //get followeds

    int countByFollowedId(Long followedId); // followers count

    int countByFollowerId(Long followerId); // followeds count
}