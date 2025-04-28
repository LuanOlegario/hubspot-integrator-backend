package br.com.meetime.hubspotintegrator.enums;

import br.com.meetime.hubspotintegrator.exception.HubSpotBadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LifecycleStage {
    SUBSCRIBER("subscriber"),
    LEAD("lead"),
    MQL("marketingqualifiedlead"),
    SQL("salesqualifiedlead"),
    OPPORTUNITY("opportunity"),
    CUSTOMER("customer"),
    OTHER("other");

    private final String value;

    LifecycleStage(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LifecycleStage fromValue(String value) {
        for (LifecycleStage lifecycleStage : values()) {
            if (lifecycleStage.value.equalsIgnoreCase(value)) return lifecycleStage;
        }
        throw new HubSpotBadRequestException("Invalid stage: " + value);
    }
}
