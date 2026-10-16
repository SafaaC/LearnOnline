package com.online.learning.registration_service.repository;

import com.online.learning.registration_service.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByUsernameAndCourseId(String username, Long courseId);
}