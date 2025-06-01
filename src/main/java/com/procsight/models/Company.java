package com.procsight.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "companies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String industry;
    private String sizeCategory; // SME, Mid-Market, Enterprise

    @Column(precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    private String headquartersCountry;
    private String taxId;
    private String subscriptionTier; // Starter, Professional, Enterprise

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "text[]")
    private String[] complianceRequirements;

    @Column
    private String customFields;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users;
}