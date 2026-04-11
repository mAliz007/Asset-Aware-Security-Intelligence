package com.Asset_Aware_Security_Intelligence.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${aasi.app.frontend-url}")
    private String frontendUrl;

    // Use a custom variable or server default for the backend URL
    @Value("${APP_BACKEND_URL:http://localhost:8080}")
    private String backendUrl;

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        // Verification usually hits the backend first to process the token
        String verifyUrl = backendUrl + "/api/auth/verify-email?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Verify your AASI Account");
        helper.setText("<html><body>" +
                "<h3>Welcome to AASI!</h3>" +
                "<p>Please click the link below to verify your email and get started:</p>" +
                "<a href=\"" + verifyUrl + "\">Verify Email Address</a>" +
                "<br><p>This link will expire in 24 hours.</p>" +
                "</body></html>", true);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        // Password reset links usually point to the React Frontend form
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Reset your AASI Password");
        helper.setText("<html><body>" +
                "<h3>Password Reset Request</h3>" +
                "<p>Click the link below to set a new password. This link expires in 15 minutes:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>" +
                "<br><p>If you did not request this, please ignore this email.</p>" +
                "</body></html>", true);

        mailSender.send(message);
    }
}