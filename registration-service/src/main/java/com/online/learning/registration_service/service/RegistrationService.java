package com.online.learning.registration_service.service;

import com.online.learning.registration_service.client.*;
import com.online.learning.registration_service.model.Registration;
import com.online.learning.registration_service.repository.RegistrationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.online.learning.registration_service.event.RegistrationConfirmedEvent;   
import org.springframework.kafka.core.KafkaTemplate;                                


@Service
public class RegistrationService {

    @Autowired private 
    RegistrationRepository registrationRepository;
    
    @Autowired 
    private CourseClient courseClient;

    @Autowired 
    private PaymentClient paymentClient;

     @Autowired                                                                      
    private KafkaTemplate<String, RegistrationConfirmedEvent> kafkaTemplate;

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
        // already registered?
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
        // save
         Registration registration = new Registration(username, courseId);
         registration.setPaid(paidCourse);       
         Registration saved = registrationRepository.save(registration);
         // announce it on Kafka (the part you pasted)
        try {
            kafkaTemplate.send("registration-events", username,
                    new RegistrationConfirmedEvent(username, courseId))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Event NOT published: " + ex.getMessage());
                    } else {
                        System.out.println("Event published to partition "
                            + result.getRecordMetadata().partition()
                            + " offset " + result.getRecordMetadata().offset());
                    }
                });
        } catch (Exception e) {
            System.out.println("Could not publish event: " + e.getMessage());
        }

        return saved;
    }
}