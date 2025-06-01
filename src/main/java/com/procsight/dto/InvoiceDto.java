package com.procsight.dto;

import com.procsight.models.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private String id;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private VendorDto vendor;
    private String invoiceNumber;
    private String poNumber;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentTerms;
    private LocalDateTime dueDate;
    private Invoice.InvoiceStatus status;
    private BigDecimal confidence;
    private Double processingTime;
    private String aiModel;
    private Boolean reviewRequired;
    private String[] anomalies;
    private String duplicateCheck;
    private LocalDateTime createdAt;
    private List<LineItemDto> lineItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorDto {
        private String id;
        private String name;
        private String taxId;
        private String email;
        private String phone;
        private Integer riskScore;
        private String performanceRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItemDto {
        private String id;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private String category;
        private String glCode;
    }
}