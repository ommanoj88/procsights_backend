package com.procsight.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {
    private BigDecimal totalSpend;
    private Integer invoiceCount;
    private BigDecimal averageInvoiceAmount;
    private String period;
    private Map<String, String> trends;
    private Map<String, String> breakdown;
    private List<String> insights;
    private List<TopVendorDto> topVendors;
    private List<AlertDto> alerts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopVendorDto {
        private String vendorName;
        private BigDecimal totalSpend;
        private Integer invoiceCount;
        private String performanceRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertDto {
        private String type;
        private String message;
        private String severity;
        private Integer count;
    }
}