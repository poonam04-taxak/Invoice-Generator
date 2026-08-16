package com.invoiceapp.invoice_generator.Service;

import com.invoiceapp.invoice_generator.DTO.*;
import com.invoiceapp.invoice_generator.Entity.*;
import com.invoiceapp.invoice_generator.Repository.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepository;

    @Autowired
    private ClientService clientService;

    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request) {

        // 1. Fetch the client (throws if not found)
        Client client = clientService.getClientEntityById(request.getClientId());

        // 2. Build the Invoice shell
        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(15)); // default: due in 15 days
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTaxRate(request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO);
        invoice.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        invoice.setStatus(InvoiceStatus.UNPAID);

        // 3. Build line items and attach to invoice
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItemDTO itemDTO : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setDescription(itemDTO.getDescription());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());

            // lineTotal = quantity * unitPrice, rounded to 2 decimal places
            BigDecimal lineTotal = itemDTO.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            item.setInvoice(invoice); // maintain both sides of the relationship
            invoice.getItems().add(item);

            subtotal = subtotal.add(lineTotal);
        }

        // 4. Calculate tax, discount, grand total
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = subtotal
                .multiply(invoice.getTaxRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Guard: discount should never exceed subtotal + tax (avoid negative totals)
        BigDecimal discount = invoice.getDiscount();
        BigDecimal preDiscountTotal = subtotal.add(taxAmount);
        if (discount.compareTo(preDiscountTotal) > 0) {
            discount = preDiscountTotal; // cap discount, don't let total go negative
        }

        BigDecimal grandTotal = preDiscountTotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setDiscount(discount);
        invoice.setGrandTotal(grandTotal);

        // 5. Save (cascade saves items automatically)
        Invoice saved = invoiceRepository.save(invoice);

        return toResponseDTO(saved);
    }

    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        return toResponseDTO(invoice);
    }

    public Invoice getInvoiceEntityById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDTO updateStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        invoice.setStatus(status);
        return toResponseDTO(invoiceRepository.save(invoice));
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    // Generates invoice numbers like INV-2026-0001, sequential per year
    private String generateInvoiceNumber() {
        int year = Year.now().getValue();
        String maxInvoiceNumber = invoiceRepository.findMaxInvoiceNumberForYear(year);

        int nextNumber = 1;
        if (maxInvoiceNumber != null) {
            String[] parts = maxInvoiceNumber.split("-");
            int lastNumber = Integer.parseInt(parts[2]);
            nextNumber = lastNumber + 1;
        }

        return String.format("INV-%d-%04d", year, nextNumber);
    }

    private InvoiceResponseDTO toResponseDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(invoice.getClient().getId());
        clientDTO.setName(invoice.getClient().getName());
        clientDTO.setEmail(invoice.getClient().getEmail());
        clientDTO.setPhone(invoice.getClient().getPhone());
        clientDTO.setAddress(invoice.getClient().getAddress());
        dto.setClient(clientDTO);

        List<InvoiceItemResponseDTO> itemDTOs = invoice.getItems().stream()
                .map(item -> new InvoiceItemResponseDTO(
                        item.getDescription(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxRate(invoice.getTaxRate());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscount(invoice.getDiscount());
        dto.setGrandTotal(invoice.getGrandTotal());
        dto.setStatus(invoice.getStatus());

        return dto;
    }
}