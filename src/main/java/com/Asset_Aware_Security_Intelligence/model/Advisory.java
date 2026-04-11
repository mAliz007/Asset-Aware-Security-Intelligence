package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advisories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advisory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String csafId;

    private String title;
    private String publisher;

    // 🔹 Actual CSAF update timestamp (used for version comparison)
    private LocalDateTime csafLastUpdated;

    // 🔹 Your system ingestion timestamp
    private LocalDateTime lastUpdated;

    private String trackingStatus;

    // 🔥 FIX: orphanRemoval ensures old vulnerabilities are deleted from DB
    @OneToMany(
            mappedBy = "advisory",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Vulnerability> vulnerabilities;
}