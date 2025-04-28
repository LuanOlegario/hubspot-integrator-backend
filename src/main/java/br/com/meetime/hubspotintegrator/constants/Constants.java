package br.com.meetime.hubspotintegrator.constants;

public class Constants {

    // OAuth Tokens
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";

    // Rate Limiting
    public static final String HUBSPOT_API_RATE_LIMITER = "hubspotApi";
    public static final String HUBSPOT_RATE_LIMIT_ERROR_MESSAGE = "Limite de requisições da API do HubSpot excedido.";

    // Contact Errors
    public static final String HUBSPOT_CONTACT_CONFLICT = "Já existe um contato com o e-mail informado.";
    public static final String HUBSPOT_CONTACT_CREATE_ERROR = "Erro ao criar contato no HubSpot.";

    // Authentication Errors
    public static final String HUBSPOT_AUTH_ERROR = "Tokens não encontrados na sessão.";
    public static final String HUBSPOT_REFRESH_ERROR = "Falha ao renovar o Access Token.";
    public static final String HUBSPOT_REFRESH_TOKEN_NOT_FOUND = "Refresh token não encontrado para esta sessão.";

    // Communication and Response Errors
    public static final String HUBSPOT_COMMUNICATION_ERROR = "Erro ao comunicar com HubSpot: ";
    public static final String HUBSPOT_RESPONSE_ERROR = "Erro ao processar resposta do HubSpot.";

    // Success Messages
    public static final String CONTACT_CREATION_OK = "contact.creation";
}


