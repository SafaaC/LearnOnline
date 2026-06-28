package com.online.learning.course_service.service;

import com.online.learning.course_service.model.Registration;
import com.online.learning.course_service.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    public Registration registerUserForCourse(String username, Long courseId) {
        if (registrationRepository.existsByUsernameAndCourseId(username, courseId)) {
            throw new IllegalStateException("User already registered for this course");
        }
        return registrationRepository.save(new Registration(username, courseId));
    }
}