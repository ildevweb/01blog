package com.example.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entity.User;
import java.util.*;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByIdNot(Long id, Pageable pageable);
    Page<User> findByIdNotAndStatus(Long id, String status, Pageable pageable);
    Optional<User> findById(Long id);
    boolean existsBy();
    int countBy();

    boolean existsById(Long id);

    @Modifying
    @Transactional
    void deleteById(Long id);
}
