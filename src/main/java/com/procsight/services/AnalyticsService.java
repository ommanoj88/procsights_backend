package com.procsight.services;

import com.procsight.repositories.InvoiceRepository;
import com.procsight.dto.AnalyticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public AnalyticsDto getSpendAnalytics(String companyId, String period) {
        // Calculate date range based on period
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(endDate, period);

        // Get invoices for the period
        var invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);

        // Calculate total spend
        BigDecimal totalSpend = invoices.stream()
                .map(invoice -> invoice.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Mock additional analytics data
        return AnalyticsDto.builder()
                .totalSpend(totalSpend)
                .invoiceCount(invoices.size())
                .averageInvoiceAmount(invoices.isEmpty() ? BigDecimal.ZERO :
                        totalSpend.divide(BigDecimal.valueOf(invoices.size())))
                .period(period)
                .trends(Map.of("growth", "15%", "comparison", "vs last year"))
                .breakdown(Map.of(
                        "Office", "40%",
                        "IT Equipment", "25%",
                        "Services", "20%",
                        "Marketing", "15%"
                ))
                .insights(java.util.List.of(
                        "Spending increased by 15% compared to last year",
                        "Office supplies represent the largest category",
                        "3 invoices require approval"
                ))
                .build();
    }

    private LocalDateTime calculateStartDate(LocalDateTime endDate, String period) {
        return switch (period.toLowerCase()) {
            case "month" -> endDate.minusMonths(1);
            case "quarter" -> endDate.minusMonths(3);
            case "year" -> endDate.minusYears(1);
            default -> endDate.minusMonths(1);
        };
    }
}