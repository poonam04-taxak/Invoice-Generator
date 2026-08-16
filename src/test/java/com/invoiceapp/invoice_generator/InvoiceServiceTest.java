package com.invoiceapp.invoice_generator;


import com.invoiceapp.invoice_generator.DTO.*;
import com.invoiceapp.invoice_generator.Entity.Client;
import com.invoiceapp.invoice_generator.Repository.InvoiceRepo;
import com.invoiceapp.invoice_generator.Service.ClientService;
import com.invoiceapp.invoice_generator.Service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepo invoiceRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void testGrandTotalCalculation_isCorrect() {
        // Arrange
        Client client = new Client("Test Client", "test@test.com", "123", "Pune");
        client.setId(1L);
        when(clientService.getClientEntityById(1L)).thenReturn(client);
        when(invoiceRepository.findMaxInvoiceNumberForYear(anyInt())).thenReturn(null);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceItemDTO item1 = new InvoiceItemDTO();
        item1.setDescription("Item A");
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("100.00"));

        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setClientId(1L);
        request.setTaxRate(new BigDecimal("10"));
        request.setDiscount(new BigDecimal("20"));
        request.setItems(List.of(item1));

        // Act
        InvoiceResponseDTO result = invoiceService.createInvoice(request);

        // Assert
        // subtotal = 2 * 100 = 200
        assertEquals(new BigDecimal("200.00"), result.getSubtotal());
        // tax = 200 * 10% = 20
        assertEquals(new BigDecimal("20.00"), result.getTaxAmount());
        // grandTotal = 200 + 20 - 20 = 200
        assertEquals(new BigDecimal("200.00"), result.getGrandTotal());
    }

    @Test
    void testDiscount_cannotMakeTotalNegative() {
        Client client = new Client("Test Client", "test@test.com", "123", "Pune");
        client.setId(1L);
        when(clientService.getClientEntityById(1L)).thenReturn(client);
        when(invoiceRepository.findMaxInvoiceNumberForYear(anyInt())).thenReturn(null);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceItemDTO item1 = new InvoiceItemDTO();
        item1.setDescription("Item A");
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("50.00"));

        InvoiceRequestDTO request = new InvoiceRequestDTO();
        request.setClientId(1L);
        request.setTaxRate(BigDecimal.ZERO);
        request.setDiscount(new BigDecimal("500")); // discount way bigger than subtotal
        request.setItems(List.of(item1));

        InvoiceResponseDTO result = invoiceService.createInvoice(request);

        // Grand total should be capped at 0, never negative
        assertEquals(new BigDecimal("0.00"), result.getGrandTotal());
    }
}