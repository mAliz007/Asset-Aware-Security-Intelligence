package com.Asset_Aware_Security_Intelligence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}