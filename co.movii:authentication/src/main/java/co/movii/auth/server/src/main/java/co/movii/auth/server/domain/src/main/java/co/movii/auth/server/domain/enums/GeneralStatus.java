package co.movii.auth.server.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum GeneralStatus {
    DISABLED("D"),
    ENABLED("E");

    private final String code;

    GeneralStatus(String s) {
        this.code = s;
    }

    public static GeneralStatus valueOf(Integer ord) {
        if ((ord > (GeneralStatus.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(GeneralStatus.class, "The value: " + ord + ", is not part of this list");
        }

        return GeneralStatus.values()[ord];
    }

    public String getCode() {
        return this.code;
    }

    public String getValue() {
        return this.name();
    }

    public Integer getOrdinal() {
        return this.ordinal();
    }
}

