package com.Asset_Aware_Security_Intelligence.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    @Value("${COOKIE_SECURE:true}")
    private boolean isSecure;

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(900) // 15 mins
                .sameSite("Lax") // Changed to Lax for cross-origin production compatibility
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/api/auth/refresh")
                .maxAge(604800) // 7 days
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie cleanCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .secure(isSecure)
                .build();
    }
}