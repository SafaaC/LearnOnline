        package com.online.learning.course_service.service;
        import com.online.learning.course_service.client.PaymentClient;
        import com.online.learning.course_service.exception.PaymentUnavailableException;
        import com.online.learning.course_service.model.Course;
        import com.online.learning.course_service.model.Registration;
        import com.online.learning.course_service.repository.RegistrationRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import feign.FeignException;


        @Service
        public class RegistrationService {
            @Autowired
            private PaymentClient paymentClient;  
            
            @Autowired
            private CourseService courseService;

            @Autowired
            private RegistrationRepository registrationRepository;

            public Registration registerUserForCourse(String username, Long courseId) {
                    // 1. Course must exist
                    Course course = courseService.getCourseById(courseId)
                            .orElseThrow(() -> new IllegalStateException("Course not found"));

                    // 2. Already registered?
                    if (registrationRepository.existsByUsernameAndCourseId(username, courseId)) {
                        throw new IllegalStateException("User already registered for this course");
                    }

                    // 3. Paid course: payment-service must confirm payment
                    boolean paidCourse = course.getPrice() != null && course.getPrice() > 0;
                    if (paidCourse) {
                        boolean paid;
                        try {
                            paid = paymentClient.verify(username, courseId);
                        } catch (FeignException e) {
                            throw new PaymentUnavailableException("Payment service unavailable, please try again");
                        }
                        if (!paid) {
                            throw new IllegalStateException("Payment not completed for this course");
                        }
                    }

                    // 4. Save
                    return registrationRepository.save(new Registration(username, courseId));
            }
        }