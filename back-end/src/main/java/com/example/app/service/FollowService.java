package com.example.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Follow;
import com.example.app.entity.User;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    //Follow a user
    @Transactional
    public ResponseEntity<?> follow(Long followerId, Long followedId) {

        if (followerId.equals(followedId)) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "You can't follow yourself"
            ));
        }

        boolean alreadyFollowing = followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);

        if (alreadyFollowing) {
            unfollow(followerId, followedId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Unfollowed successfully"
            ));
        }


        User follower = userRepository.findById(followerId)
            .orElse(new User("user deleted"));
        User followed = userRepository.findById(followedId)
            .orElse(new User("user deleted"));


        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);

        followRepository.save(follow);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Followed successfully"
        ));
    }

    //Unfollow a user
    @Transactional
    public void unfollow(Long followerId, Long followedId) {

        followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
            .ifPresent(followRepository::delete);
    }

    //Check if already following
    public boolean isFollowing(Long followerId, Long followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    //Count followers of a user
    public int countFollowers(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    //Count followeds of a user
    public int countFolloweds(Long userId) {
        return followRepository.countByFollowerId(userId);
    }
}
