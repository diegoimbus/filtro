package co.moviired.register.domain.enums.clevertap;

import co.moviired.register.properties.ClevertapProperties;

import java.util.Arrays;

/**
 * List of events for clevertap
 */
public enum Event {

    ADO_API_APPROVAL,
    ADO_API_REJECT,
    ADO_DO_APPROVAL,
    ADO_DO_REJECT,
    UNKNOWN;

    public static Event getByName(String name, ClevertapProperties clevertapProperties) {
        return Arrays.stream(values()).filter(value -> value.getName(clevertapProperties).equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
    }

    public String getName(ClevertapProperties clevertapProperties) {
        switch (this) {
            case ADO_API_APPROVAL:
                return clevertapProperties.getAdoApiApprovalEvent();
            case ADO_DO_APPROVAL:
                return clevertapProperties.getAdoDoApprovalEvent();
            case ADO_DO_REJECT:
                return clevertapProperties.getAdoDoRejectEvent();
            default:
                return clevertapProperties.getAdoApiRejectEvent();
        }
    }
}

