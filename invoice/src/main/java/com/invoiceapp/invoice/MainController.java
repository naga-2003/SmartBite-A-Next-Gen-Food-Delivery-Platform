package com.invoiceapp.invoice;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FoodItemRepository foodRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = userRepo.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid credentials");
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String email,
                         @RequestParam String password,
                         RedirectAttributes redirectAttributes) {
        if (userRepo.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/signup";
        }
        userRepo.save(new User(email, password));
        return "redirect:/";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }
        List<FoodItem> items = foodRepo.findAll();
        model.addAttribute("items", items);
        return "home";
    }

    // Handle placing order
    @PostMapping("/placeOrder")
    public String placeOrder(@RequestParam List<String> itemIds,
                             @RequestParam String customerName,
                             RedirectAttributes redirectAttributes) {
        List<FoodItem> items = foodRepo.findAllById(itemIds);
        double total = items.stream().mapToDouble(FoodItem::getPrice).sum();

        Invoice invoice = new Invoice(customerName, items, total);
        invoiceRepo.save(invoice);

        redirectAttributes.addFlashAttribute("invoiceId", invoice.getId());
        return "redirect:/invoiceDetails/" + invoice.getId();
    }

    @GetMapping("/invoiceDetails/{id}")
    public String invoiceDetails(@PathVariable String id, Model model) {
        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/home";
        }
        model.addAttribute("invoice", invoice);
        return "invoice_details";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Invoice> invoices = invoiceRepo.findAll();
        model.addAttribute("invoices", invoices);
        return "order_list";
    }

    @GetMapping("/orders/{id}/invoice")
    public void generateInvoicePdf(@PathVariable String id, HttpServletResponse response) throws IOException {
        Invoice invoice = invoiceRepo.findById(id).orElse(null);
        if (invoice == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Invoice", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice ID: " + invoice.getId()));
        document.add(new Paragraph("Customer Name: " + invoice.getCustomerName()));
        document.add(new Paragraph("Date: " + new Date().toString()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2});
        table.addCell("Item Name");
        table.addCell("Quantity");
        table.addCell("Price");

        invoice.getItems().forEach(item -> {
            table.addCell(item.getName());
            table.addCell("1");  // Change if you add quantity support
            table.addCell(String.valueOf(item.getPrice()));
        });

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total: " + invoice.getTotalAmount()));

        document.close();
    }
}



