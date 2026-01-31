package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.User;
import java.util.*;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByIdNot(Long id);
    List<User> findByIdNotAndStatus(Long id, String status);
    Optional<User> findById(Long id);
    boolean existsBy();
    int countBy();

    boolean existsById(Long id);

    @Modifying
    @Transactional
    void deleteById(Long id);
}
