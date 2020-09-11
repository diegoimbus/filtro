package co.moviired.topups.model.enums;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
public enum OperationType {
    RTMMREQ("0300");//cual codigo debe ir aqui cipabemo escribi de ejemplo 0300


    private String isoCode;

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


