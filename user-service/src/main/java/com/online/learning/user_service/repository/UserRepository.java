package com.online.learning.user_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.online.learning.user_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
