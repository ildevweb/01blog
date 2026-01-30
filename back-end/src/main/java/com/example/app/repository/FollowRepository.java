package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    List<Long> findFollowerIdsByFollowedId(Long followedId); // get followers ids


    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Follow f
        WHERE f.followed.id = :userId
        OR f.follower.id = :userId
    """)
    void deleteByFollowedOrFollower(@Param("userId") Long userId);
}