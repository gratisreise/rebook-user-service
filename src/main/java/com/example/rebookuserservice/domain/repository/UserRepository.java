package com.example.rebookuserservice.domain.repository;

import com.example.rebookuserservice.domain.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
