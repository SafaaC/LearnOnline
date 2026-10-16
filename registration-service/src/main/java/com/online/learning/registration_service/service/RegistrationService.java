package com.online.learning.registration_service.service;

import com.online.learning.registration_service.client.*;
import com.online.learning.registration_service.model.Registration;
import com.online.learning.registration_service.repository.RegistrationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationService {

    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private CourseClient courseClient;
    @Autowired private PaymentClient paymentClient;

    public Registration registerUserForCourse(String username, Long courseId) {

        // Call 1: ask course-service about the course
        CourseDto course;
        try {
            course = courseClient.getCourse(courseId);
        } catch (FeignException.NotFound e) {
            throw new IllegalStateException("Course not found");
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Course service unavailable");
        }

        if (registrationRepository.existsByUsernameAndCourseId(username, courseId)) {
            throw new IllegalStateException("User already registered for this course");
        }

        // Call 2: paid course? ask payment-service
        if (course.price() != null && course.price() > 0) {
            boolean paid;
            try {
                paid = paymentClient.verify(username, courseId);
            } catch (FeignException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Payment service unavailable");
            }
            if (!paid) {
                throw new IllegalStateException("Payment not completed for this course");
            }
        }

        return registrationRepository.save(new Registration(username, courseId));
    }
}