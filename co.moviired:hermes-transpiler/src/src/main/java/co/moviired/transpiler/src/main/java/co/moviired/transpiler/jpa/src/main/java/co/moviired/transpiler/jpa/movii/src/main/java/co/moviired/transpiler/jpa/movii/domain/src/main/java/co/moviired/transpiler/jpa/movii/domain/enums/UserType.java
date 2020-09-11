package co.moviired.transpiler.jpa.movii.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum UserType {
    GENERAL_USER("GU"),
    BALANCE_USER("BU");

    private final String code;

    UserType(String s) {
        this.code = s;
    }

    public static UserType valueOf(Integer ord) {
        if ((ord > (UserType.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(UserType.class, "The value: " + ord + ", is not part of this list");
        }

        return UserType.values()[ord];
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

