package com.invoiceapp.invoice_generator.Controller;

import com.invoiceapp.invoice_generator.DTO.ClientDTO;
import com.invoiceapp.invoice_generator.Service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing clients & exposes basic CRUD op
 */

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;
    
    // creates a new client
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO dto) {
        ClientDTO created = clientService.createClient(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    //   retrieves all clients
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // retrieves a single client by id
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }
}
