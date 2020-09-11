package co.moviired.topups.model.enums;

public enum OperatorStatusType {
    INACTIVE(0), ACTIVE(1), SUSPENDED(2);

    private int id;

    OperatorStatusType(int id) {
        this.id = id;
    }

    public static OperatorStatusType parse(int id) {
        OperatorStatusType type = null;
        for (OperatorStatusType op : OperatorStatusType.values()) {
            if (id == op.getId()) {
                type = op;
                break;
            }
        }
        if (type == null) {
            throw new EnumConstantNotPresentException(OperationType.class,
                    "The value: " + id + ", is not part of this list");
        }
        return type;
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

