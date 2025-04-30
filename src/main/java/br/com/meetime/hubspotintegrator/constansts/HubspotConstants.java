package br.com.meetime.hubspotintegrator.constansts;

public final class HubspotConstants {
    public static final String CONTACT_CREATION = "contact.creation";
    public static final String DEFAULT_TOKEN_KEY = "token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String INVALID_SIGNATURE = "Assinatura inválida.";
    public static final String TOKEN_NOT_FOUND = "Token não encontrado.";
    public static final String SIGNATURE_HEADER = "X-HubSpot-Signature";
    public static final String HUBSPOT_API_RATELIMITER = "hubspotApi";
    public static final String RATE_LIMIT_EXCEEDED = "Limite de requisições excedido.";
    public static final String CONFLICT_ON_CREATE = "Erro de conflito ao criar contato.";
    public static final String HUBSPOT_API_ERROR_PREFIX = "Erro na API do HubSpot: ";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String EXPIRES_IN_KEY = "expires_in";

    // Error massages
    public static final String ERROR_PROCESSING_TOKENS = "Erro ao processar a resposta de tokens.";
    public static final String TOKEN_OR_REFRESH_TOKEN_NOT_FOUND = "Token ou RefreshToken não encontrado.";
    public static final String COMMUNICATION_ERROR_WITH_HUBSPOT = "Erro de comunicação com o HubSpot ao renovar o token.";
}
