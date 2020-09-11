package co.moviired.digitalcontent.business.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum SellerChannel {
    SUBSCRIBER("S"),
    CHANNEL("C"),
    RETAIL("R");

    private String code;

    SellerChannel(String s) {
        this.code = s;
    }

    public static SellerChannel valueOf(Integer ord) {
        if ((ord > (SellerChannel.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(SellerChannel.class, "The value: " + ord + ", is not part of this list");
        }

        return SellerChannel.values()[ord];
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

