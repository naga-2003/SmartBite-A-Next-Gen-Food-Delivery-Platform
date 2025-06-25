package com.invoiceapp.invoice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private String customerName;
    private List<FoodItem> items;
    private double totalAmount;

    public Invoice() {}

    public Invoice(String customerName, List<FoodItem> items, double totalAmount) {
        this.customerName = customerName;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<FoodItem> getItems() { return items; }
    public void setItems(List<FoodItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}


