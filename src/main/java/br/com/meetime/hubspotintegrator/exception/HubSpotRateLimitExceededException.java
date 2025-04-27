package br.com.meetime.hubspotintegrator.exception;

public class HubSpotRateLimitExceededException extends RuntimeException {
    public HubSpotRateLimitExceededException(String message) {
        super(message);
    }
}
