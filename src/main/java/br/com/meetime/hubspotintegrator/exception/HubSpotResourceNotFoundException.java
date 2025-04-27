package br.com.meetime.hubspotintegrator.exception;

public class HubSpotResourceNotFoundException extends RuntimeException {
    public HubSpotResourceNotFoundException(String message) {
        super(message);
    }
}
