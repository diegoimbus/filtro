package co.moviired.transpiler.jpa.movii.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum SyncStatus {
    GENERATED("G"),
    CALCULATED("C"),
    SYNCHRONIZED("S");

    private final String code;

    SyncStatus(String s) {
        this.code = s;
    }

    public static SyncStatus valueOf(Integer ord) {
        if ((ord > (SyncStatus.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(SyncStatus.class, "The value: " + ord + ", is not part of this list");
        }

        return SyncStatus.values()[ord];
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

