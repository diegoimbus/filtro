package co.moviired.transpiler.jpa.movii.domain.enums;

public enum OperationType {
    TOPUP("0200"),
    TOPUP_RESPONSE("0210"),
    CASH_OUT("0400"),
    BILL_PAY("0500"),
    VALIDATE_BILL_REFERENCE("0510"),
    VALIDATE_BILL_EAN("0520"),
    DIGITAL_CONTENT_CARD("8100"),
    DIGITAL_CONTENT_PINES("8200"),
    ECHO("0800"),
    ECHO_RESPONSE("0810");

    private final String isoCode;

    OperationType(String pisoCode) {
        this.isoCode = pisoCode;
    }

    public static OperationType valueOf(Integer ord) {
        if ((ord > (OperationType.values().length - 1)) || (ord < 0)) {
            throw new EnumConstantNotPresentException(OperationType.class, "The value: " + ord + ", is not part of this list");
        }

        return OperationType.values()[ord];
    }

    public static OperationType parse(String code) {
        OperationType type = null;
        for (OperationType op : OperationType.values()) {
            if (code.equals(op.getCode())) {
                type = op;
                break;
            }
        }

        if (type == null) {
            throw new EnumConstantNotPresentException(OperationType.class, "The value: " + code + ", is not part of this list");
        }

        return type;
    }

    public String getCode() {
        return this.isoCode;
    }

    public String getValue() {
        return this.name();
    }

    public Integer getOrdinal() {
        return this.ordinal();
    }

}
