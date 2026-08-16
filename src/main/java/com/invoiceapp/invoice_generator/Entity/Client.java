package com.invoiceapp.invoice_generator.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Client name is required")
    private String name;

    @Email(message = "Enter a valid email")
    private String email;

    private String phone;

    private String address;

    public Client() {}

    public Client(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;


        this.phone = phone;
        this.address = address;
    }
}