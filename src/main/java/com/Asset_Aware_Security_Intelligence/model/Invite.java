package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String targetEmail;
    private String inviteCode;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private LocalDateTime expiresAt;
    private boolean isUsed;
}