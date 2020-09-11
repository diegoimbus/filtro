package co.moviired.support.otp.model.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum NotifyChannel {
    SMS("SMS"),
    CALL("CALL"),
    EMAIL("EMAIL");

    private String val;

    NotifyChannel(String val) {
        this.val = val;
    }

    public static Optional<NotifyChannel> resolve(String state) {
        return Stream.of(values())
                .filter(OtpState ->
                        OtpState.name().equals(state) || OtpState.value().equals(state))
                .findFirst();
    }

    public static boolean hasValueIn(String txStateValue, NotifyChannel... OtpStates) {
        return Stream.of(OtpStates).anyMatch(OtpState -> OtpState.value().equals(txStateValue));
    }

    public String value() {
        return val;
    }

}

