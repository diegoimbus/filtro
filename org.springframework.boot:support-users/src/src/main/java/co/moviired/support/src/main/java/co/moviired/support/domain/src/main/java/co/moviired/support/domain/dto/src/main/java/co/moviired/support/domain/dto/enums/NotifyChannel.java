package co.moviired.support.domain.dto.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum NotifyChannel {
    SMS("SMS"),
    CALL("CALL"),
    EMAIL("EMAIL");

    private String val;

    NotifyChannel(String pval) {
        this.val = pval;
    }

    public static Optional<NotifyChannel> resolve(String state) {
        return Stream.of(values())
                .filter(otpState ->
                        otpState.name().equals(state) || otpState.value().equals(state))
                .findFirst();
    }

    public static boolean hasValueIn(String txStateValue, NotifyChannel... otpStates) {
        return Stream.of(otpStates).anyMatch(otpState -> otpState.value().equals(txStateValue));
    }

    public String value() {
        return val;
    }

}

