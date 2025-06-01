package com.procsight.repositories;

import com.procsight.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    Optional<Company> findByName(String name);

    @Query("SELECT c FROM Company c WHERE c.industry = :industry AND c.isActive = true")
    List<Company> findByIndustry(@Param("industry") String industry);

    @Query("SELECT c FROM Company c WHERE c.subscriptionTier = :tier AND c.isActive = true")
    List<Company> findBySubscriptionTier(@Param("tier") String tier);

    List<Company> findByIsActiveTrue();
}