package co.moviired.transpiler.jpa.movii.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum ProductType {
    MOBILE_TELEPHONY("MT"), //0
    MOBILE_PACKAGES("MP"), //1
    TRANSPORT("TR"),//2
    TV("TV"),//3
    UTILITIES("UT"),//4
    BANK_SERVICES("BS"),//5
    OTHER("OT"),//6
    DIGITAL_CONTENT_CARD("DCA"), //7
    DIGITAL_CONTENT_PINES("DCP"); //8

    private final String code;

    ProductType(String s) {
        this.code = s;
    }

    public static ProductType valueOf(Integer ord) {
        if ((ord > (ProductType.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(ProductType.class, "The value: " + ord + ", is not part of this list");
        }

        return ProductType.values()[ord];
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

