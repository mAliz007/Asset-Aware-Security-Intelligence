package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "advisory_products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdvisoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendor;
    private String productName;

    // The "Clean Name" for high-speed matching (e.g., "siemenss71500")
    @Column(index = true)
    private String canonicalName;

    private String cpe; // Standardized string if available

    // Version Boundaries (Human Readable)
    private String versionStart;
    private String versionEnd;

    // Version Boundaries (Padded for Math/SQL Comparison)
    // Example: 00001.00010.00000.00000
    @Column(index = true)
    private String versionStartPadded;

    @Column(index = true)
    private String versionEndPadded;

    @ManyToOne
    @JoinColumn(name = "vulnerability_id")
    private Vulnerability vulnerability;
}