package co.moviired.acquisition.model.enums;

import java.util.Arrays;

public enum TransactionType {

    ACTIVATION_PRE_AUTHORIZATION("preauthact"),
    ACTIVATION_AUTHORIZATION("act"),
    ACTIVATION_REVERSAL("reverseact"),
    DEACTIVATION("deact"),
    DEACTIVATION_REVERSAL("revdeact"),
    PRODUCT_VALIDATION("preauthredeem"),
    REDEEM("redeem"),
    UNKNOWN(null);

    private final String value;

    TransactionType(String valueI) {
        this.value = valueI;
    }

    public String getValue() {
        return value;
    }

    public static TransactionType getByValue(String value) {
        return Arrays.stream(values()).filter(val -> val.getValue().equalsIgnoreCase(value)).findFirst().orElse(UNKNOWN);
    }
}

