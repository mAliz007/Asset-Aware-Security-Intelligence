package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "advisory_products", indexes = {
        @Index(name = "idx_canonical_name", columnList = "canonicalName"),
        @Index(name = "idx_version_start_padded", columnList = "versionStartPadded"),
        @Index(name = "idx_version_end_padded", columnList = "versionEndPadded")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdvisoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendor;
    private String productName;

    // Remove (index = true) from here
    private String canonicalName;

    private String cpe;

    private String versionStart;
    private String versionEnd;

    // Remove (index = true) from here
    private String versionStartPadded;

    // Remove (index = true) from here
    private String versionEndPadded;

    @ManyToOne
    @JoinColumn(name = "vulnerability_id")
    private Vulnerability vulnerability;
}