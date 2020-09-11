package co.moviired.digitalcontent.business.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum Channel {
    TAT(1),
    ALLIANCE(2);

    private int code;

    Channel(int s) {
        this.code = s;
    }

    public static Channel valueOf(Integer ord) {
        if ((ord > (Channel.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(Channel.class, "The value: " + ord + ", is not part of this list");
        }

        return Channel.values()[ord];
    }

    public int getCode() {
        return this.code;
    }

    public String getValue() {
        return this.name();
    }

    public Integer getOrdinal() {
        return this.ordinal();
    }
}

