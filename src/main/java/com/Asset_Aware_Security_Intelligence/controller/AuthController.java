package com.Asset_Aware_Security_Intelligence.controller;

import com.Asset_Aware_Security_Intelligence.dto.*;
import com.Asset_Aware_Security_Intelligence.model.User;
import com.Asset_Aware_Security_Intelligence.service.AuthService;
import com.Asset_Aware_Security_Intelligence.util.CookieUtils;
import com.Asset_Aware_Security_Intelligence.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok("Registration successful. Please check your email to verify your account.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verify(@RequestParam String token) {
        try {
            String result = authService.verifyEmail(token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.authenticateUser(request);

            String accessToken = jwtUtils.generateAccessToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);

            ResponseCookie accessCookie = cookieUtils.createAccessTokenCookie(accessToken);
            ResponseCookie refreshCookie = cookieUtils.createRefreshTokenCookie(refreshToken);

            AuthResponse response = AuthResponse.builder()
                    .message("Login successful")
                    .isOnboarded(user.isOnboarded())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cleanAccess = cookieUtils.cleanCookie("accessToken");
        ResponseCookie cleanRefresh = cookieUtils.cleanCookie("refreshToken");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanAccess.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefresh.toString())
                .body("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        } catch (Exception e) {
            // We return OK even if user doesn't exist for security (prevents email enumeration)
            return ResponseEntity.ok("If that email exists, a reset link has been sent.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}