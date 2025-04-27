package br.com.meetime.hubspotintegrator.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponse {

    private final LocalDateTime dateTime = LocalDateTime.now();
    private String path;
    private String status;
    private List<String> messages;
}
