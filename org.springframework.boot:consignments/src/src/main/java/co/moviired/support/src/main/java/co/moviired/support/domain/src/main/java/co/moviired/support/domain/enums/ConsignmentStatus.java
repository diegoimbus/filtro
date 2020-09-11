package co.moviired.support.domain.enums;


public enum ConsignmentStatus {
    PENDING("0"), APPROVED("1"), REJECTED("2"), PROCESS("3");
    private final String id;

    ConsignmentStatus(String id) {
        this.id = id;
    }

    public static String parse(String name) {
        String type = null;
        for (ConsignmentStatus status : ConsignmentStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                type = status.getId();
                break;
            }
        }
        return type;
    }

    public static String parse(int id) {
        String type = null;
        for (ConsignmentStatus status : ConsignmentStatus.values()) {
            if (status.getId().equalsIgnoreCase(String.valueOf(id))) {
                type = status.name();
                break;
            }
        }
        if (type == null) {
            type = "Unknown";
        }
        return type;
    }

    public static ConsignmentStatus parseStr(String id) {
        ConsignmentStatus type = null;
        for (ConsignmentStatus status : ConsignmentStatus.values()) {
            if (status.getId().equalsIgnoreCase(String.valueOf(id))) {
                type = status;
                break;
            }
        }
        if (type == null) {
            type = ConsignmentStatus.REJECTED;
        }
        return type;
    }

    public String getId() {
        return id;
    }

}

