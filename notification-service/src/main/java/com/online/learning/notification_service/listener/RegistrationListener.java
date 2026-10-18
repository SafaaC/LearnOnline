package com.online.learning.notification_service.listener;

import com.online.learning.notification_service.event.RegistrationConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener {
     private static final Logger log = LoggerFactory.getLogger(RegistrationListener.class);

    private final UserClient userClient;
    private final EmailService emailService;

    public RegistrationListener(UserClient userClient, EmailService emailService) {
        this.userClient = userClient;
        this.emailService = emailService;
    }
    @KafkaListener(topics = "registration-events", groupId = "notification")
    public void onRegistration(RegistrationConfirmedEvent event) {
        log.info("Received event: user={} course={}", event.username(), event.courseId());

        // 1. Find the user's email (sync call to user-service)
        String email;
        try {
            email = userClient.getEmail(event.username());
        } catch (FeignException.NotFound e) {
            // Retrying can never help, so log and finish. The message counts as processed.
            log.warn("No email on file for {}, skipping notification", event.username());
            return;
        }
        // Any other FeignException (user-service down or slow) is NOT caught:
        // the method throws, Kafka does not commit the offset, and the message is retried.

        // 2. Send the email
        emailService.sendWelcome(email, event.username(), event.courseId());
        log.info("Welcome email sent to {} for course {}", email, event.courseId());
    }
}