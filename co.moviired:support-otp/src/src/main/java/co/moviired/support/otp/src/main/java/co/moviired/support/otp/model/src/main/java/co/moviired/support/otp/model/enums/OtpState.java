package co.moviired.support.otp.model.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum OtpState {
    PENDING("PENDING"),
    USED("USED"),
    EXPIRED("EXPIRED"),
    INVALIDATED("INVALIDATED");

    private String val;

    OtpState(String val) {
        this.val = val;
    }

    public static Optional<OtpState> resolve(String state) {
        return Stream.of(values())
                .filter(OtpState ->
                        OtpState.name().equals(state) || OtpState.value().equals(state))
                .findFirst();
    }

    public static boolean isPending(OtpState OtpState) {
        return PENDING.equals(OtpState);
    }

    public static boolean hasValueIn(String txStateValue, OtpState... OtpStates) {
        return Stream.of(OtpStates).anyMatch(OtpState -> OtpState.value().equals(txStateValue));
    }

    public String value() {
        return val;
    }

}

