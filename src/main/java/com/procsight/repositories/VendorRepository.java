package com.procsight.repositories;

import com.procsight.models.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

    @Query("SELECT v FROM Vendor v WHERE v.company.id = :companyId")
    List<Vendor> findByCompanyId(@Param("companyId") String companyId);

    Optional<Vendor> findByNameAndCompanyId(String name, String companyId);
    Optional<Vendor> findByVendorIdAndCompanyId(String vendorId, String companyId);
    Optional<Vendor> findByTaxIdAndCompanyId(String taxId, String companyId);
}