package com.invoiceapp.invoice_generator.DTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceRequestDTO {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotEmpty(message = "Invoice must have at least one item")
    @Valid
    private List<InvoiceItemDTO> items;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal discount = BigDecimal.ZERO;

    public InvoiceRequestDTO() {}

    // GS
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}