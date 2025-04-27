package br.com.meetime.hubspotintegrator.exception;

public class HubSpotBadRequestException extends RuntimeException {
    public HubSpotBadRequestException(String message) {
        super(message);
    }
}
