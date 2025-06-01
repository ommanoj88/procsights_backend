package com.procsight.controllers;

import com.procsight.models.Invoice;
import com.procsight.services.InvoiceService;
import com.procsight.services.FirebaseAuthService;
import com.procsight.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*"
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadInvoice(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String companyId = "default-company"; // Default for testing

            // Try to get user from token if provided
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    String uid = firebaseAuthService.verifyToken(token);
                    var user = userService.getUserByUid(uid);
                    if (user.isPresent() && user.get().getCompany() != null) {
                        companyId = user.get().getCompany().getId();
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Could not verify token, using default company");
                }
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Please select a file to upload"
                ));
            }

            // Upload and process invoice
            Invoice invoice = invoiceService.uploadInvoice(file, companyId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Invoice uploaded successfully",
                    "invoiceId", invoice.getId(),
                    "fileName", invoice.getFileName(),
                    "status", invoice.getStatus().toString(),
                    "estimatedCompletion", "2-3 seconds"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Upload failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String vendor,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String companyId = "default-company"; // Default for testing

            // Try to get user from token if provided
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    String uid = firebaseAuthService.verifyToken(token);
                    var user = userService.getUserByUid(uid);
                    if (user.isPresent() && user.get().getCompany() != null) {
                        companyId = user.get().getCompany().getId();
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Could not verify token, using default company");
                }
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Invoice> invoices;

            if (status != null) {
                Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
                invoices = invoiceService.getInvoicesByCompanyIdAndStatus(companyId, invoiceStatus, pageable);
            } else {
                invoices = invoiceService.getInvoicesByCompanyId(companyId, pageable);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "invoices", invoices.getContent().stream().map(invoice -> Map.of(
                            "id", invoice.getId(),
                            "fileName", invoice.getFileName(),
                            "originalFileName", invoice.getOriginalFileName(),
                            "invoiceNumber", invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A",
                            "vendor", invoice.getVendor() != null ? Map.of(
                                    "name", invoice.getVendor().getName(),
                                    "email", invoice.getVendor().getEmail() != null ? invoice.getVendor().getEmail() : "N/A"
                            ) : null,
                            "totalAmount", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0,
                            "currency", invoice.getCurrency() != null ? invoice.getCurrency() : "USD",
                            "status", invoice.getStatus().toString(),
                            "confidence", invoice.getConfidence() != null ? invoice.getConfidence() : 0,
                            "createdAt", invoice.getCreatedAt()
                    )).toList(),
                    "pagination", Map.of(
                            "currentPage", invoices.getNumber(),
                            "totalPages", invoices.getTotalPages(),
                            "totalElements", invoices.getTotalElements(),
                            "size", invoices.getSize()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch invoices: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInvoice(@PathVariable String id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", true);
                put("invoice", new HashMap<String, Object>() {{
                    put("id", invoice.getId());
                    put("fileName", invoice.getFileName());
                    put("originalFileName", invoice.getOriginalFileName());
                    put("invoiceNumber", invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A");
                    put("poNumber", invoice.getPoNumber() != null ? invoice.getPoNumber() : "N/A");
                    put("vendor", invoice.getVendor() != null ? new HashMap<String, Object>() {{
                        put("id", invoice.getVendor().getId());
                        put("name", invoice.getVendor().getName());
                        put("taxId", invoice.getVendor().getTaxId() != null ? invoice.getVendor().getTaxId() : "N/A");
                        put("email", invoice.getVendor().getEmail() != null ? invoice.getVendor().getEmail() : "N/A");
                        put("riskScore", invoice.getVendor().getRiskScore() != null ? invoice.getVendor().getRiskScore() : 0);
                        put("performanceRating", invoice.getVendor().getPerformanceRating() != null ? invoice.getVendor().getPerformanceRating() : "N/A");
                    }} : null);
                    put("subtotal", invoice.getSubtotal() != null ? invoice.getSubtotal() : 0);
                    put("taxAmount", invoice.getTaxAmount() != null ? invoice.getTaxAmount() : 0);
                    put("totalAmount", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0);
                    put("currency", invoice.getCurrency() != null ? invoice.getCurrency() : "USD");
                    put("paymentTerms", invoice.getPaymentTerms() != null ? invoice.getPaymentTerms() : "N/A");
                    put("dueDate", invoice.getDueDate());
                    put("status", invoice.getStatus().toString());
                    put("confidence", invoice.getConfidence() != null ? invoice.getConfidence() : 0);
                    put("processingTime", invoice.getProcessingTime() != null ? invoice.getProcessingTime() : 0);
                    put("aiModel", invoice.getAiModel() != null ? invoice.getAiModel() : "N/A");
                    put("reviewRequired", invoice.getReviewRequired() != null ? invoice.getReviewRequired() : false);
                    put("createdAt", invoice.getCreatedAt());
                    put("updatedAt", invoice.getUpdatedAt());
                }});
            }});

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to fetch invoice: " + e.getMessage()
            ));
        }
    }

    // For testing - get all invoices without authentication
    @GetMapping("/test/all")
    public ResponseEntity<Map<String, Object>> getAllInvoices() {
        try {
            var invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", invoices.size(),
                    "invoices", invoices.stream().map(invoice -> Map.of(
                            "id", invoice.getId(),
                            "fileName", invoice.getFileName(),
                            "status", invoice.getStatus().toString(),
                            "totalAmount", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0,
                            "createdAt", invoice.getCreatedAt()
                    )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to get invoices: " + e.getMessage()
            ));
        }
    }
}