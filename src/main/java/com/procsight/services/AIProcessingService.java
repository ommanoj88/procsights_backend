package com.procsight.services;

import com.procsight.models.Invoice;
import com.procsight.models.Vendor;
import com.procsight.models.LineItem;
import com.procsight.repositories.InvoiceRepository;
import com.procsight.repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class AIProcessingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Async
    public CompletableFuture<Invoice> processInvoiceAsync(Invoice invoice, MultipartFile file) {
        try {
            // Update status to processing
            invoice.setStatus(Invoice.InvoiceStatus.PROCESSING);
            invoiceRepository.save(invoice);

            // Simulate AI processing delay
            Thread.sleep(2000);

            // Mock AI extraction results
            Map<String, Object> extractedData = mockAIExtraction(file);

            // Update invoice with extracted data
            updateInvoiceWithExtractedData(invoice, extractedData);

            // Update status to completed
            invoice.setStatus(Invoice.InvoiceStatus.COMPLETED);
            invoice.setConfidence(new BigDecimal("0.987"));
            invoice.setProcessingTime(2.3);
            invoice.setAiModel("gemini-pro-1.5");

            return CompletableFuture.completedFuture(invoiceRepository.save(invoice));

        } catch (Exception e) {
            invoice.setStatus(Invoice.InvoiceStatus.ERROR);
            invoiceRepository.save(invoice);
            throw new RuntimeException("AI processing failed: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> mockAIExtraction(MultipartFile file) {
        // Mock AI extraction - in real implementation, this would call Google Gemini API
        Random random = new Random();

        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put("vendorName", "EcoPack Ltd");
        extractedData.put("vendorTaxId", "GB123456789");
        extractedData.put("vendorEmail", "billing@ecopack.com");
        extractedData.put("invoiceNumber", "INV-2024-" + String.format("%03d", random.nextInt(1000)));
        extractedData.put("poNumber", "PO-2024-" + String.format("%03d", random.nextInt(1000)));
        extractedData.put("subtotal", new BigDecimal("7500.00"));
        extractedData.put("taxAmount", new BigDecimal("1500.00"));
        extractedData.put("totalAmount", new BigDecimal("9000.00"));
        extractedData.put("currency", "USD");
        extractedData.put("paymentTerms", "NET 30");
        extractedData.put("dueDate", LocalDateTime.now().plusDays(30));

        return extractedData;
    }

    private void updateInvoiceWithExtractedData(Invoice invoice, Map<String, Object> data) {
        // Create or find vendor
        Vendor vendor = findOrCreateVendor((String) data.get("vendorName"),
                (String) data.get("vendorTaxId"),
                (String) data.get("vendorEmail"));

        invoice.setVendor(vendor);
        invoice.setInvoiceNumber((String) data.get("invoiceNumber"));
        invoice.setPoNumber((String) data.get("poNumber"));
        invoice.setSubtotal((BigDecimal) data.get("subtotal"));
        invoice.setTaxAmount((BigDecimal) data.get("taxAmount"));
        invoice.setTotalAmount((BigDecimal) data.get("totalAmount"));
        invoice.setCurrency((String) data.get("currency"));
        invoice.setPaymentTerms((String) data.get("paymentTerms"));
        invoice.setDueDate((LocalDateTime) data.get("dueDate"));
    }

    private Vendor findOrCreateVendor(String name, String taxId, String email) {
        return vendorRepository.findByNameAndCompanyId(name, "default-company")
                .orElseGet(() -> {
                    Vendor newVendor = Vendor.builder()
                            .name(name)
                            .taxId(taxId)
                            .email(email)
                            .riskScore(85)
                            .performanceRating("A")
                            .build();
                    return vendorRepository.save(newVendor);
                });
    }
}