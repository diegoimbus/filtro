package co.moviired.acquisition.model.enums;

import java.util.Arrays;

public enum ProductState {

    INACTIVE("INACTIVE"),
    ACTIVE("ACTIVE"),
    REDEEMED("REDEEMED"),
    RETURNED("RETURNED"),
    CANCELLED("CANCELLED"),
    UNKNOWN(null);

    private final String value;

    ProductState(String valueI) {
        this.value = valueI;
    }

    public String getValue() {
        return value;
    }

    public static ProductState getByValue(String value) {
        return Arrays.stream(values()).filter(val -> val.getValue().equalsIgnoreCase(value)).findFirst().orElse(UNKNOWN);
    }
}

