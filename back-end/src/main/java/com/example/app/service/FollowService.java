package com.example.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.Follow;
import com.example.app.entity.User;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    //Follow a user
    @Transactional
    public Follow follow(Long followerId, Long followedId) {

        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        boolean alreadyFollowing =
            followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);

        if (alreadyFollowing) {
            unfollow(followerId, followedId);
            Follow follow = new Follow();
            return follow;
        }


        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followed = userRepository.findById(followedId)
            .orElseThrow(() -> new RuntimeException("Followed not found"));


        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);

        followRepository.save(follow);

        return follow;
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
