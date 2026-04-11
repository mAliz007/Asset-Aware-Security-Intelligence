package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.VerificationToken;
import com.Asset_Aware_Security_Intelligence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}