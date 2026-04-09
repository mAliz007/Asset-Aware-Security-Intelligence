package com.Asset_Aware_Security_Intelligence.repository;

import com.Asset_Aware_Security_Intelligence.model.Advisory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory, String> {
    // Used to check if we already processed this advisory
    Optional<Advisory> findByCsafId(String csafId);
}