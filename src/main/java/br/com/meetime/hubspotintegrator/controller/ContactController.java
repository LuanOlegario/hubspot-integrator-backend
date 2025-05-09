package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.documentation.ContactApiDoc;
import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contacts")
public class ContactController implements ContactApiDoc {

    private final ContactService contactService;

    @PostMapping("/create")
    public ResponseEntity<ContactResponseDto> createContact(@RequestBody @Valid CreateContactDto createContactDto) {
        ContactResponseDto contactResponse = contactService.createContact(createContactDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(contactResponse);
    }
}



