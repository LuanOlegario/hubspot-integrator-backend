package br.com.meetime.hubspotintegrator.dto.response;

public record HubspotWebhookEventDto(
        long appId,
        long eventId,
        Long subscriptionId,
        Long portalId,
        Long occurredAt,
        String subscriptionType,
        Integer attemptNumber,
        Long objectId,
        String changeSource,
        String changeFlag
) {
}
