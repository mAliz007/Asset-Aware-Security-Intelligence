package com.Asset_Aware_Security_Intelligence.security;

import com.Asset_Aware_Security_Intelligence.model.User;
import com.Asset_Aware_Security_Intelligence.repository.UserRepository;
import com.Asset_Aware_Security_Intelligence.util.CookieUtils;
import com.Asset_Aware_Security_Intelligence.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;

    @Value("${aasi.app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseThrow();

        // 1. Generate our own tokens
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        // 2. Set Cookies
        ResponseCookie accessCookie = cookieUtils.createAccessTokenCookie(accessToken);
        ResponseCookie refreshCookie = cookieUtils.createRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 3. Redirect to Frontend Dashboard
        // Use the injected frontendUrl instead of a hardcoded localhost address
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard");
    }
}