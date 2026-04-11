package com.Asset_Aware_Security_Intelligence.service;

import com.Asset_Aware_Security_Intelligence.model.User;
import com.Asset_Aware_Security_Intelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1. Check if user already exists (Manual Signup or previous Google Login)
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // 2. BRAND NEW USER: Register them
            User user = User.builder()
                    .email(email)
                    .fullName(name)
                    .isEnabled(true) // Google users are verified by default
                    .isOnboarded(false) // New user must go through onboarding
                    .password(null) // They don't have a local password
                    .build();
            userRepository.save(user);
        } else {
            // 3. EXISTING USER: Just update their name if it's missing
            User existingUser = userOptional.get();
            if (existingUser.getFullName() == null) {
                existingUser.setFullName(name);
                userRepository.save(existingUser);
            }
        }

        return oAuth2User;
    }
}