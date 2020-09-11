package co.moviired.transpiler.jpa.movii.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum BalanceModality {
    GENERAL("G"),
    PRODUCT("P");

    private final String code;

    BalanceModality(String s) {
        this.code = s;
    }

    public static BalanceModality valueOf(Integer ord) {
        if ((ord > (BalanceModality.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(BalanceModality.class, "The value: " + ord + ", is not part of this list");
        }

        return BalanceModality.values()[ord];
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

