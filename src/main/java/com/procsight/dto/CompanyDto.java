package com.procsight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String id;
    private String name;
    private String industry;
    private String sizeCategory;
    private BigDecimal annualRevenue;
    private String headquartersCountry;
    private String taxId;
    private String subscriptionTier;
    private Boolean isActive;
    private String[] complianceRequirements;
    private LocalDateTime createdAt;
    private Integer userCount;
    private Integer invoiceCount;
}