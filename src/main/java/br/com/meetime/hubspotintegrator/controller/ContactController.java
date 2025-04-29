package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.service.ContactService;
import br.com.meetime.hubspotintegrator.service.OAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/create")
    public ResponseEntity<ContactResponseDto> createContact(@RequestBody @Valid CreateContactDto createContactDto) {
        String accessToken =  OAuthService.tokenStore.get("token").accessToken();
        ContactResponseDto contactResponse = contactService.createContact(createContactDto, accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(contactResponse);
    }
}




