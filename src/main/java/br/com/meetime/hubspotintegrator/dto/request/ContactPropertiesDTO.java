package br.com.meetime.hubspotintegrator.dto.request;

import br.com.meetime.hubspotintegrator.enums.LifecycleStage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ContactPropertiesDTO(
        @Email(message = "Formato de e-mail inválido")
        @JsonProperty("email")
        String email,

        @NotEmpty(message = "Primeiro nome é obrigatório")
        @JsonProperty("firstname")
        String firstname,

        @NotEmpty(message = "Sobrenome é obrigatório")
        @JsonProperty("lastname")
        String lastname,

        @NotEmpty(message = "É necessário informar ao menos um telefone para este contato")
        @JsonProperty("phone")
        String phone,

        @NotEmpty(message = "É necessário informar a empresa para este contato")
        @JsonProperty("company")
        String company,

        @NotEmpty(message = "Por favor, informe um website")
        @JsonProperty("website")
        String website,

        @NotNull
        @JsonProperty("lifecyclestage")
        LifecycleStage lifecycleStage
) {
}
