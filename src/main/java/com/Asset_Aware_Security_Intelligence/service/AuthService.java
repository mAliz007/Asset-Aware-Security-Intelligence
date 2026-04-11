package com.Asset_Aware_Security_Intelligence.service;

import com.Asset_Aware_Security_Intelligence.dto.SignupRequest;
import com.Asset_Aware_Security_Intelligence.dto.LoginRequest;
import com.Asset_Aware_Security_Intelligence.model.PasswordResetToken;
import com.Asset_Aware_Security_Intelligence.model.User;
import com.Asset_Aware_Security_Intelligence.model.VerificationToken;
import com.Asset_Aware_Security_Intelligence.repository.UserRepository;
import com.Asset_Aware_Security_Intelligence.repository.VerificationTokenRepository;
import com.Asset_Aware_Security_Intelligence.repository.PasswordResetTokenRepository;
import com.Asset_Aware_Security_Intelligence.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository; // Added this field
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;

    @Transactional
    public void registerUser(SignupRequest request) throws Exception {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(null)
                .isEnabled(false)
                .isOnboarded(false)
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Transactional
    public String verifyEmail(String token) {
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or non-existent token."));

        if (vToken.isExpired()) {
            throw new RuntimeException("Verification link has expired.");
        }

        User user = vToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(vToken);
        return "Email verified successfully! You can now log in.";
    }

    public User authenticateUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        return user;
    }

    @Transactional
    public void initiatePasswordReset(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email."));

        // Clean up any old tokens for this user
        resetTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        resetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken rToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));

        if (rToken.isExpired()) {
            throw new RuntimeException("Reset token has expired.");
        }

        User user = rToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(rToken);
    }
}