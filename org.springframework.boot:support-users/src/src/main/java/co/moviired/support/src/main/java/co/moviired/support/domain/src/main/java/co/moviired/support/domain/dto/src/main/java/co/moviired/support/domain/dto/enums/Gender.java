package co.moviired.support.domain.dto.enums;

import java.util.Arrays;

/**
 * Status of user in this component
 * this is used on user param "status"
 */
public enum Gender {

    GEN_MAS(1),
    GEN_FEM(2),
    UNKNOWN(3);

    private Integer id;

    Gender(Integer pid) {
        this.id = pid;
    }

    public static Gender getById(int id) {
        return Arrays.stream(values()).filter(value -> value.getId() == id).findFirst().orElse(UNKNOWN);
    }

    public Integer getId() {
        return id;
    }
}

