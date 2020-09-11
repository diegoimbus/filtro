package co.moviired.support.domain.dto.enums;

import java.util.Arrays;

/**
 * Status of user in this component
 * this is used on user param "status"
 */
public enum Status {

    PENDING(1),
    ACTIVE(2),
    INACTIVE(3),
    ALTERED(4),
    UNKNOWN(5);

    private Integer id;

    Status(Integer pid) {
        this.id = pid;
    }

    public static Status getById(int id) {
        return Arrays.stream(values()).filter(value -> value.getId() == id).findFirst().orElse(UNKNOWN);
    }

    public Integer getId() {
        return id;
    }
}

