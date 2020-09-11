package co.moviired.topups.model.enums;

public enum OperatorType {
    RECHARGE(0), PACKAGE(1);

    private int id;

    OperatorType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getValue() {
        return this.name();
    }

    public Integer getOrdinal() {
        return this.ordinal();
    }
}

