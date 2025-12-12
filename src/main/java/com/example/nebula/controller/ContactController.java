package com.example.nebula.controller;

import com.example.nebula.dto.ApiResponse;
import com.example.nebula.dto.contact.ContactDto;
import com.example.nebula.model.ContactMessage;
import com.example.nebula.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> enviarContacto(@Valid @RequestBody ContactDto contactDto) {
        try {
            ContactMessage contactMessage = new ContactMessage();
            contactMessage.setNombre(contactDto.getNombre());
            contactMessage.setEmail(contactDto.getEmail());
            contactMessage.setTelefono(contactDto.getTelefono());
            contactMessage.setAsunto(contactDto.getAsunto());
            contactMessage.setMensaje(contactDto.getMensaje());

            contactService.saveContactMessage(contactMessage);

            return ResponseEntity.ok(ApiResponse.success("Mensaje enviado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error al enviar el mensaje: " + e.getMessage()));
        }
    }
}