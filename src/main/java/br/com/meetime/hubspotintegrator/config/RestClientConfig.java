package br.com.meetime.hubspotintegrator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final TokenInterceptor tokenInterceptor;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor(tokenInterceptor.bearerAuthInterceptor())
                .build();
    }

    @Bean(name = "authRestClient")
    public RestClient authRestClient() {
        return RestClient.create();
    }
}