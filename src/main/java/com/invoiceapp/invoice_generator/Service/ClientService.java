package com.invoiceapp.invoice_generator.Service;


import com.invoiceapp.invoice_generator.Repository.ClientRepo;
import com.invoiceapp.invoice_generator.DTO.ClientDTO;
import com.invoiceapp.invoice_generator.Entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// handles the conversion b/w Client & ClientDTO

@Service
public class ClientService {

    @Autowired
    private ClientRepo clientRepository;

    // creates & persists a new client from validated dto input
    public ClientDTO createClient(ClientDTO dto) {
        Client client = new Client(dto.getName(), dto.getEmail(), dto.getPhone(), dto.getAddress());
        Client saved = clientRepository.save(client);
        return toDTO(saved);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // retrieves all clients mapped to DTOs
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        return toDTO(client);
    }

    public Client getClientEntityById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

     // converts a client entity into its API-facing DTO rep
    private ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAddress(client.getAddress());
        return dto;
    }
}
