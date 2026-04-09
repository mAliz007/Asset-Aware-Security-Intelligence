package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advisories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Advisory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String csafId; // The official ID from the JSON (e.g., CISA-ADP-2026-001)

    private String title;
    private String publisher;
    private LocalDateTime lastUpdated;
    private String trackingStatus; // e.g., "Final", "Interim"

    @OneToMany(mappedBy = "advisory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vulnerability> vulnerabilities;
}