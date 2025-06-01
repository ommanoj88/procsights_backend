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
public class UserDto {
    private String id;
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String companyId;
    private String companyName;
    private String roleId;
    private String roleName;
    private String department;
    private String costCenter;
    private BigDecimal approvalLimit;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}