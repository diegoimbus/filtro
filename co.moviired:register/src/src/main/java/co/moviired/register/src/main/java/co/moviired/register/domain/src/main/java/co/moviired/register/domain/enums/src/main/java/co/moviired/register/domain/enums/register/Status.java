package co.moviired.register.domain.enums.register;

import java.util.Arrays;

import static co.moviired.register.helper.ConstantsHelper.*;

/**
 * Status of user in this component
 * this is used on user param "status"
 */
public enum Status {

    PENDING(PENDING_STATUS_ID),
    APPROVED(APPROVED_STATUS_ID),
    DECLINED(DECLINED_STATUS_ID),
    ALTERED(ALTERED_STATUS_ID),
    UNKNOWN(UNKNOWN_CODE_ENUM);

    private Integer id;

    Status(Integer pId) {
        this.id = pId;
    }

    public static Status getById(int id) {
        return Arrays.stream(values()).filter(value -> value.getId() == id).findFirst().orElse(UNKNOWN);
    }

    public Integer getId() {
        return id;
    }
}

