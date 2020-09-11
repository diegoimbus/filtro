package co.moviired.register.domain.enums.ado;

import java.util.Arrays;

import static co.moviired.register.helper.ConstantsHelper.UNKNOWN_CODE_ENUM;

/**
 * Possibles responses of ado
 * this is used in user on param "adoStatus"
 */
public enum AdoCaseStatus {
    PENDING(1, true, false),
    SUCCESS_PROCESS(2, false, true),
    INCONSISTENT_PROCESS(3, false, false),
    DOCUMENT_SUCCESS_FACE_NOT_MATCH(4, true, false),
    CAPTURE_WRONG(5, false, false),
    INVALID_DOCUMENT(6, false, false),
    VERIFICATION_PROCESS(7, true, false),
    ALTERED_DOCUMENT(8, false, false),
    FALSE_DOCUMENT(9, false, false),
    FACE_NOT_MATCH(10, false, false),
    PHONE_OR_IMEI_PENDING_TRANSACTION(12, true, false),
    PHONE_REGISTERED(13, false, true),
    PERSON_REGISTERED(14, false, true),
    ERROR(15, false, false),
    UNKNOWN(UNKNOWN_CODE_ENUM, false, false);

    private final int id;
    private final boolean isPending;
    private final boolean isApproved;

    AdoCaseStatus(int pid, boolean pisPending, boolean pisApproved) {
        this.id = pid;
        this.isPending = pisPending;
        this.isApproved = pisApproved;
    }

    public static AdoCaseStatus getById(int pid) {
        return Arrays.stream(values()).filter(value -> value.getId() == pid).findFirst().orElse(UNKNOWN);
    }

    public int getId() {
        return id;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isApproved() {
        return isApproved;
    }
}

