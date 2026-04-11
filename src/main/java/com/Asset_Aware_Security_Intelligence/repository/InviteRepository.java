package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    // Look up the code Alice typed in
    Optional<Invite> findByInviteCodeAndTargetEmailAndIsUsedFalse(String code, String email);
}