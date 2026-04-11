package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
}