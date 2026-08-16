package com.invoiceapp.invoice_generator.DTO;
import com.invoiceapp.invoice_generator.Entity.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceResponseDTO {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private ClientDTO client;
    private List<InvoiceItemResponseDTO> items;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private InvoiceStatus status;

    public InvoiceResponseDTO() {}

}