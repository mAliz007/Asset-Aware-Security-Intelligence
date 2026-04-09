package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.AdvisoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdvisoryProductRepository extends JpaRepository<AdvisoryProduct, Long> {
    // This will be used later by the Matching Engine
    List<AdvisoryProduct> findByCanonicalName(String canonicalName);
}