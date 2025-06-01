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
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String fileName;

    private String originalFileName;
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String invoiceNumber;
    private String poNumber;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String currency;
    private String paymentTerms;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(precision = 3, scale = 2)
    private BigDecimal confidence;

    private Double processingTime;
    private String aiModel;

    @Builder.Default
    private Boolean reviewRequired = false;

    @Column(columnDefinition = "text[]")
    private String[] anomalies;

    private String duplicateCheck;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LineItem> lineItems;

    public enum InvoiceStatus {
        UPLOADED, PROCESSING, COMPLETED, ERROR, APPROVED, PAID
    }
}