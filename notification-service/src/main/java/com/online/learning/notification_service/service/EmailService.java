package com.online.learning.notification_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailAuthenticationException;



@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@learning-platform.local}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcome(String toEmail, String username, Long courseId) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);   
        msg.setTo(toEmail);
        msg.setSubject("Welcome to the course!");
        msg.setText("Hi " + username + ", you are registered for course " + courseId + ".");
        mailSender.send(msg);
    }
}