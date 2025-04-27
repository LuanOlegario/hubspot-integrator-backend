package br.com.meetime.hubspotintegrator.exception;

public class HubSpotApiException extends RuntimeException {
    public HubSpotApiException(String message) {
        super(message);
    }
}