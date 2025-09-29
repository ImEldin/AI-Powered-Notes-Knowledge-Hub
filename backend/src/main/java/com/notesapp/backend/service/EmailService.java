package com.notesapp.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.mail_from}")
    private String fromEmail;

    @Value("${app.mail.mail_from_name}")
    private String fromName;

    public void sendEmailVerification(String toEmail, String firstName, String verificationToken) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(fromEmail, fromName);
            helper.setSubject("Verify Your Email - Notes App");

            String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + verificationToken;

            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("verificationUrl", verificationUrl);

            String htmlContent = templateEngine.process("email-verification", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email verification sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email verification to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}