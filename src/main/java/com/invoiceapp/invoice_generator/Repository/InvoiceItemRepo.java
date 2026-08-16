package com.invoiceapp.invoice_generator.Repository;

import com.invoiceapp.invoice_generator.Entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long> {

}
