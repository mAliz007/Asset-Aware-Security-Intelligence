package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.Membership;
import com.Asset_Aware_Security_Intelligence.model.Tenant;
import com.Asset_Aware_Security_Intelligence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    // Find all organizations a user belongs to
    List<Membership> findByUserId(String userId);

    // Find all team members of a specific organization
    List<Membership> findByTenantId(String tenantId);

    // Check if Alice is actually in Org B
    Optional<Membership> findByUserIdAndTenantId(String userId, String tenantId);

    // Ensure only one Admin exists per tenant (used during role updates)
    boolean existsByTenantIdAndRole(String tenantId, com.Asset_Aware_Security_Intelligence.model.UserRole role);

    // Remove a member from an org
    void deleteByUserIdAndTenantId(String userId, String tenantId);
}