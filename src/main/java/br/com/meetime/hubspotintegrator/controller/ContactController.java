package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.service.ContactService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @PostMapping(value = "/create")
    public ResponseEntity<ContactResponseDto> createContact(@RequestBody CreateContactDto createContactDto, HttpSession session) {
        ContactResponseDto contactResponse = contactService.createContact(createContactDto, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(contactResponse);
    }
}

