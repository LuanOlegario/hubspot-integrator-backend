package br.com.meetime.hubspotintegrator.dto.response;

public record HubspotWebhookEventDto(
        String eventId,
        String subscriptionType,
        String objectId,
        Long occurredAt,
        String changeSource,
        String eventType,
        String propertyName,
        String propertyValue
) {
}
