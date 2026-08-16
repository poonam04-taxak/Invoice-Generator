package com.invoiceapp.invoice_generator.Repository;

import com.invoiceapp.invoice_generator.Entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client, Long> {
}
