package com.invoiceapp.invoice_generator.DTO;

import jakarta.validation.constraints.NotBlank;

// Req body for AI description gen endpoint

public class DescriptionRequestDTO {

    @NotBlank(message = "Note is required")
    private String note;

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
