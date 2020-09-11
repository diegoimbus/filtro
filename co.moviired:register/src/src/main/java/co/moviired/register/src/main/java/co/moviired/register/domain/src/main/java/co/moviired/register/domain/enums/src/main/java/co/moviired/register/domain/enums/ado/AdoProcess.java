package co.moviired.register.domain.enums.ado;

import java.util.Arrays;

import static co.moviired.register.helper.ConstantsHelper.*;

public enum AdoProcess {

    REGISTRATION(REGISTRATION_PROCESS),
    ORDINARY_DEPOSIT(ORDINARY_DEPOSIT_PROCESS),
    UNKNOWN(UNKNOWN_CODE_ENUM);

    private final int id;

    AdoProcess(int pid) {
        this.id = pid;
    }

    public static AdoProcess getById(int pid) {
        return Arrays.stream(values()).filter(value -> value.getId() == pid).findFirst().orElse(UNKNOWN);
    }

    public int getId() {
        return id;
    }
}

