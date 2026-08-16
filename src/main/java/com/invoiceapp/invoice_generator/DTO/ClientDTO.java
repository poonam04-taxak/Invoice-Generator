package com.invoiceapp.invoice_generator.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClientDTO {

    private Long id;

    @NotBlank(message = "Client name is required")
    private String name;

    @Email(message = "Enter a valid email")
    private String email;

    @jakarta.validation.constraints.Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    private String phone;

    @jakarta.validation.constraints.NotBlank(message = "Address is required")
    private String address;

    public ClientDTO() {}

    // GS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}