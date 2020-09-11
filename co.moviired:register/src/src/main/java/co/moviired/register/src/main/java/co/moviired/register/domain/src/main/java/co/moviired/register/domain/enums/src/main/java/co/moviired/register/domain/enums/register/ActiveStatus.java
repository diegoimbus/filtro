package co.moviired.register.domain.enums.register;

import java.util.Arrays;

import static co.moviired.register.helper.ConstantsHelper.*;

/**
 * Status user on User for say if is active or not, this param on user is "isActive";
 */
public enum ActiveStatus {

    ACTIVE(ACTIVE_REGISTRY_ID),
    DEFEAT(DEFEATED_REGISTRY_ID),
    UNKNOWN(UNKNOWN_CODE_ENUM);

    private final int id;

    ActiveStatus(Integer pid) {
        this.id = pid;
    }

    public static ActiveStatus getById(int pid) {
        return Arrays.stream(values()).filter(value -> value.getId() == pid).findFirst().orElse(UNKNOWN);
    }

    public Integer getId() {
        return id;
    }

}

