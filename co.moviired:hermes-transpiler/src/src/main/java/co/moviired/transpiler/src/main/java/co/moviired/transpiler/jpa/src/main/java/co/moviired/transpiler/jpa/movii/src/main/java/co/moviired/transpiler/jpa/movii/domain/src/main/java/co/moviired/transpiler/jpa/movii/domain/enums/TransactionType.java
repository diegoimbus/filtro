package co.moviired.transpiler.jpa.movii.domain.enums;

/*
 * Copyright @2017. ASKI, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-10-24
 * @since 1.0
 */
public enum TransactionType {
    OTHER("O"),
    SALES("TOPUP"),
    MONEY_TRANSFER("MT"),
    BILL_PAYMENT("BP"),
    BANKING("B"),
    UTILITIES("U");

    private final String code;

    TransactionType(String s) {
        this.code = s;
    }

    public static TransactionType valueOf(Integer ord) {
        if ((ord > (TransactionType.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(TransactionType.class, "The value: " + ord + ", is not part of this list");
        }

        return TransactionType.values()[ord];
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

