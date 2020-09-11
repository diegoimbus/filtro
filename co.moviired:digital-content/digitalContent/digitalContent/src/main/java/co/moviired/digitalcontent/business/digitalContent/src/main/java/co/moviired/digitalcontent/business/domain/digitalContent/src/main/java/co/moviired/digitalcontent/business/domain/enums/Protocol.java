package co.moviired.digitalcontent.business.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum Protocol {
    ISO("ISO-8583"),
    REST("REST"),
    SOAP("SOAP");

    private String code;

    Protocol(String s) {
        this.code = s;
    }

    public static Protocol valueOf(Integer ord) {
        if ((ord > (Protocol.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(Protocol.class, "The value: " + ord + ", is not part of this list");
        }

        return Protocol.values()[ord];
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

