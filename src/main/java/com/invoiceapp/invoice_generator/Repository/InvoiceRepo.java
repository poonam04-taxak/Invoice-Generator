package com.invoiceapp.invoice_generator.Repository;

import com.invoiceapp.invoice_generator.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    @org.springframework.data.jpa.repository.Query(
            "SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.invoiceNumber LIKE CONCAT('INV-', :year, '-%')"
    )
    String findMaxInvoiceNumberForYear(@org.springframework.data.repository.query.Param("year") int year);
}
