package co.movii.auth.server.domain.enums;

import java.util.Arrays;

/**
 *
 */
public enum Origin {
    NOTHING(1),
    MAHINDRA(2),
    CML(3);


    private final Integer id;

    Origin(Integer pid) {
        this.id = pid;
    }

    public static Origin getById(int id) {
        return Arrays.stream(values()).filter(value -> value.getId() == id).findFirst().orElse(NOTHING);
    }

    public Integer getId() {
        return id;
    }
}

