package com.invoiceapp.invoice_generator.Service;

import com.invoiceapp.invoice_generator.DTO.InvoiceItemResponseDTO;
import com.invoiceapp.invoice_generator.DTO.InvoiceResponseDTO;
import com.lowagie.text.*;
        import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(InvoiceResponseDTO invoice) {
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(30, 41, 59));
            Font headingFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.GRAY);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Font grandTotalFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(30, 41, 59));

            // --- Header: INVOICE title + invoice number ---
            Paragraph title = new Paragraph("INVOICE", titleFont);
            document.add(title);

            Paragraph invoiceNum = new Paragraph(invoice.getInvoiceNumber(), normalFont);
            invoiceNum.setSpacingAfter(20);
            document.add(invoiceNum);

            // --- Billed To + Dates table (2 columns) ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(20);

            PdfPCell billedToCell = new PdfPCell();
            billedToCell.setBorder(Rectangle.NO_BORDER);
            billedToCell.addElement(new Paragraph("BILLED TO", headingFont));
            billedToCell.addElement(new Paragraph(invoice.getClient().getName(), boldFont));
            if (invoice.getClient().getAddress() != null)
                billedToCell.addElement(new Paragraph(invoice.getClient().getAddress(), normalFont));
            if (invoice.getClient().getEmail() != null)
                billedToCell.addElement(new Paragraph(invoice.getClient().getEmail(), normalFont));
            if (invoice.getClient().getPhone() != null)
                billedToCell.addElement(new Paragraph(invoice.getClient().getPhone(), normalFont));
            headerTable.addCell(billedToCell);

            PdfPCell datesCell = new PdfPCell();
            datesCell.setBorder(Rectangle.NO_BORDER);
            datesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph invDate = new Paragraph("Invoice Date: " + invoice.getInvoiceDate(), normalFont);
            invDate.setAlignment(Element.ALIGN_RIGHT);
            datesCell.addElement(invDate);
            Paragraph dueDate = new Paragraph("Due Date: " + invoice.getDueDate(), normalFont);
            dueDate.setAlignment(Element.ALIGN_RIGHT);
            datesCell.addElement(dueDate);
            Paragraph status = new Paragraph("Status: " + invoice.getStatus(), normalFont);
            status.setAlignment(Element.ALIGN_RIGHT);
            datesCell.addElement(status);
            headerTable.addCell(datesCell);

            document.add(headerTable);

            // --- Line items table ---
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{4, 1.2f, 1.8f, 1.8f});
            itemsTable.setSpacingAfter(15);

            Font tableHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"Description", "Qty", "Unit Price", "Total"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(new Color(30, 41, 59));
                cell.setPadding(8);
                cell.setHorizontalAlignment(h.equals("Description") ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
                itemsTable.addCell(cell);
            }

            for (InvoiceItemResponseDTO item : invoice.getItems()) {
                itemsTable.addCell(cellText(item.getDescription(), normalFont, Element.ALIGN_LEFT));
                itemsTable.addCell(cellText(String.valueOf(item.getQuantity()), normalFont, Element.ALIGN_RIGHT));
                itemsTable.addCell(cellText("Rs. " + item.getUnitPrice(), normalFont, Element.ALIGN_RIGHT));
                itemsTable.addCell(cellText("Rs. " + item.getLineTotal(), normalFont, Element.ALIGN_RIGHT));
            }

            document.add(itemsTable);

            // --- Totals section (right-aligned) ---
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(45);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            addTotalRow(totalsTable, "Subtotal", "Rs. " + invoice.getSubtotal(), normalFont);
            addTotalRow(totalsTable, "Tax (" + invoice.getTaxRate() + "%)", "Rs. " + invoice.getTaxAmount(), normalFont);
            addTotalRow(totalsTable, "Discount", "- Rs. " + invoice.getDiscount(), normalFont);
            addTotalRow(totalsTable, "Grand Total", "Rs. " + invoice.getGrandTotal(), grandTotalFont);

            document.add(totalsTable);

            // --- Footer ---
            Paragraph footer = new Paragraph("\n\nThank you for your business!", normalFont);
            footer.setSpacingBefore(30);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }

        return out.toByteArray();
    }

    private PdfPCell cellText(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new Color(230, 230, 230));
        return cell;
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}