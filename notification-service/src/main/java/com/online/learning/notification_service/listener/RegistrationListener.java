package com.online.learning.notification_service.listener;

import com.online.learning.notification_service.event.RegistrationConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener {

    @KafkaListener(topics = "registration-events", groupId = "notification")
    public void onRegistration(RegistrationConfirmedEvent event) {
        System.out.println("EMAIL: Welcome " + event.username()
                + ", you are registered for course " + event.courseId());
    }
}