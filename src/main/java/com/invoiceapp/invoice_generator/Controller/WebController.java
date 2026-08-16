package com.invoiceapp.invoice_generator.Controller;

import com.invoiceapp.invoice_generator.DTO.InvoiceResponseDTO;
import com.invoiceapp.invoice_generator.Service.ClientService;
import com.invoiceapp.invoice_generator.Service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ClientService clientService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();

        BigDecimal totalRevenue = invoices.stream()
                .map(InvoiceResponseDTO::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paidCount = invoices.stream()
                .filter(i -> i.getStatus().name().equals("PAID"))
                .count();

        long unpaidCount = invoices.stream()
                .filter(i -> i.getStatus().name().equals("UNPAID"))
                .count();

        model.addAttribute("invoices", invoices);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCount", invoices.size());
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("unpaidCount", unpaidCount);

        return "dashboard";
    }

    @GetMapping("/invoices/new")
    public String newInvoiceForm(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "create-invoice";
    }

    @GetMapping("/invoices/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        return "invoice-view";
    }
}