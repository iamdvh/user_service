package com.iamdvh.UserService.repository;

import com.iamdvh.UserService.dto.request.UserRequest;
import com.iamdvh.UserService.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Page<UserEntity> findAll(Pageable pageable);
    Optional<UserEntity> findByUsername(String username);
}
