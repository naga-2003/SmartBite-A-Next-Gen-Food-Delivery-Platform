package com.invoiceapp.invoice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InvoiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(InvoiceApplication.class, args);
  }
  

  @Bean
  CommandLineRunner runner(FoodItemRepository foodRepo) {
      return args -> {
          if(foodRepo.count() == 0) {
              foodRepo.save(new FoodItem("Pizza", 8.99));
              foodRepo.save(new FoodItem("Burger", 5.49));
              foodRepo.save(new FoodItem("Salad", 4.99));
              foodRepo.save(new FoodItem("Pasta", 7.25));
          }
      };
  }
}
