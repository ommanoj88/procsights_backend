package com.procsight.repositories;

import com.procsight.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId")
    Page<Invoice> findByCompanyId(@Param("companyId") String companyId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.status = :status")
    Page<Invoice> findByCompanyIdAndStatus(@Param("companyId") String companyId,
                                           @Param("status") Invoice.InvoiceStatus status,
                                           Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.vendor.id = :vendorId")
    List<Invoice> findByVendorId(@Param("vendorId") String vendorId);

    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.createdAt BETWEEN :startDate AND :endDate")
    List<Invoice> findByCompanyIdAndDateRange(@Param("companyId") String companyId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}