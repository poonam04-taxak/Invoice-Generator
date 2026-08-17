package com.invoiceapp.invoice_generator.Controller;
import com.invoiceapp.invoice_generator.Service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.invoiceapp.invoice_generator.DTO.InvoiceRequestDTO;
import com.invoiceapp.invoice_generator.DTO.InvoiceResponseDTO;
import com.invoiceapp.invoice_generator.Entity.InvoiceStatus;
import com.invoiceapp.invoice_generator.Service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest api for the core invoice lifecycle & main controller
 */

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    // handles invoice calc logic
    @Autowired
    private InvoiceService invoiceService;

    // handles - downloadable PDF doc
    @Autowired
    private PdfService pdfService;

    // creates new invoice
    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        InvoiceResponseDTO created = invoiceService.createInvoice(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Fetch Invoice
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // retrieves a single invoice by id
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // generates & download a pdf
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(id);
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", invoice.getInvoiceNumber() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    //partial update status - paid, unpaid
    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateStatus(id, status));
    }

    // delete invoice & invoice num r never reused after deletion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
