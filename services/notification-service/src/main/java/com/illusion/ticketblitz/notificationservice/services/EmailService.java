package com.illusion.ticketblitz.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTicketEmail(String reservationId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@ticketblitz.com");
            // TODO: Look up actual user email from auth service DB
            message.setTo("customer@example.com");
            message.setSubject("Your TicketBlitz Reservation is Confirmed");
            message.setText("Your reservation (" + reservationId + ") has been confirmed and paid.\n\nShow this email at the venue entrance.\n\nEnjoy the event!");

            mailSender.send(message);
            log.info("Confirmation email sent for reservation: {}", reservationId);
        } catch (Exception e) {
            log.error("Failed to send confirmation email for reservation: {}", reservationId, e);
        }
    }
}