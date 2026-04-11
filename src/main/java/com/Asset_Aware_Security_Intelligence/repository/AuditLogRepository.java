package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Get history for the specific organization
    List<AuditLog> findByTenantIdOrderByTimestampDesc(String tenantId);

    // Get history for a specific person within that organization
    List<AuditLog> findByTenantIdAndUserIdOrderByTimestampDesc(String tenantId, String userId);
}