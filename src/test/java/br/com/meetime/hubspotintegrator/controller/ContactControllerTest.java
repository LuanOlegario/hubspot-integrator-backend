package br.com.meetime.hubspotintegrator.controller;

import br.com.meetime.hubspotintegrator.dto.request.ContactPropertiesDTO;
import br.com.meetime.hubspotintegrator.dto.request.CreateContactDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponseDto;
import br.com.meetime.hubspotintegrator.dto.response.ContactResponsePropertiesDto;
import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import br.com.meetime.hubspotintegrator.enums.LifecycleStage;
import br.com.meetime.hubspotintegrator.service.ContactService;
import br.com.meetime.hubspotintegrator.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private ContactService contactService;

    @Test
    void testCreateContact() throws Exception {
        ContactPropertiesDTO properties = new ContactPropertiesDTO(
                "luan@gmail.com", "Luan", "Olegario", "11999999999",
                "Meetime", "https://luan.com.br", LifecycleStage.LEAD
        );
        CreateContactDto dto = new CreateContactDto(properties);

        when(oAuthService.getToken()).thenReturn(new TokenResponseDto("fake-token", null, Instant.now().plusSeconds(3600)));

        when(contactService.createContact(any()))
                .thenReturn(new ContactResponseDto(
                        "123",
                        new ContactResponsePropertiesDto(
                                "Meetime", "2025-04-29T12:00:00Z",
                                "luan@gmail.com", "Luan", "Olegario",
                                "LEAD", "11999999999", "https://luan.com.br"
                        ),
                        "2025-04-29T12:00:00Z"
                ));

        mockMvc.perform(post("/api/contacts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                
                                                {
                                  "properties": {
                                    "email": "luan@gmail.com",
                                    "firstname": "Luan",
                                    "lastname": "Olegario",
                                    "phone": "11999999999",
                                    "company": "Meetime",
                                    "website": "https://luan.com.br",
                                    "lifecyclestage": "LEAD"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.properties.company").value("Meetime"))
                .andExpect(jsonPath("$.properties.email").value("luan@gmail.com"))
                .andExpect(jsonPath("$.properties.firstname").value("Luan"))
                .andExpect(jsonPath("$.properties.lastname").value("Olegario"))
                .andExpect(jsonPath("$.properties.lifecyclestage").value("LEAD"));

    }
}