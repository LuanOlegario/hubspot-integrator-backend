package br.com.meetime.hubspotintegrator.config;

import br.com.meetime.hubspotintegrator.service.TokenStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenInterceptor {

    private final TokenStoreService tokenStore;

    public ClientHttpRequestInterceptor bearerAuthInterceptor() {
        return (request, body, execution) -> {
            var token = tokenStore.get("token");
            if (token == null) {
                throw new RuntimeException("Token não encontrado no interceptor!");
            }

            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken());
            return execution.execute(request, body);
        };
    }
}