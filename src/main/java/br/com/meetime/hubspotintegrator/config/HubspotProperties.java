package br.com.meetime.hubspotintegrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "hubspot")
public class HubspotProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String authorizationUrl;
    private String tokenUrl;

}