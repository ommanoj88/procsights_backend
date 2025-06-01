package com.procsight.services;

import com.procsight.models.Invoice;
import com.procsight.repositories.InvoiceRepository;
import com.procsight.exceptions.InvoiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AIProcessingService aiProcessingService;

    public Invoice uploadInvoice(MultipartFile file, String companyId) {
        try {
            // Create invoice record
            Invoice invoice = Invoice.builder()
                    .fileName(file.getOriginalFilename())
                    .originalFileName(file.getOriginalFilename())
                    .status(Invoice.InvoiceStatus.UPLOADED)
                    .build();

            Invoice savedInvoice = invoiceRepository.save(invoice);

            // Process with AI asynchronously
            aiProcessingService.processInvoiceAsync(savedInvoice, file);

            return savedInvoice;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload invoice: " + e.getMessage(), e);
        }
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
    }

    public Page<Invoice> getInvoicesByCompanyId(String companyId, Pageable pageable) {
        return invoiceRepository.findByCompanyId(companyId, pageable);
    }

    public Page<Invoice> getInvoicesByCompanyIdAndStatus(String companyId,
                                                         Invoice.InvoiceStatus status,
                                                         Pageable pageable) {
        return invoiceRepository.findByCompanyIdAndStatus(companyId, status, pageable);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByDateRange(String companyId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate) {
        return invoiceRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);
    }

    public Invoice updateInvoiceStatus(String id, Invoice.InvoiceStatus status) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }

    public Invoice approveInvoice(String id, String approverId) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(String id) {
        invoiceRepository.deleteById(id);
    }
}