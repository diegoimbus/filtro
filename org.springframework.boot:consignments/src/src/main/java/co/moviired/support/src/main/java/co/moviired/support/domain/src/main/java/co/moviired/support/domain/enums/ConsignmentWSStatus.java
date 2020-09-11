package co.moviired.support.domain.enums;

public enum ConsignmentWSStatus {
    PENDING("0"),
    APPROVED("1"),
    REJECTED("2"),
    DUPLICATED("3"),

    REVERSED("4");
    private final String id;

    ConsignmentWSStatus(String id) {
        this.id = id;
    }

    public static String parse(String name) {
        String type = null;
        for (ConsignmentWSStatus status : ConsignmentWSStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                type = status.getId();
                break;
            }
        }
        return type;
    }

    public static String parse(int id) {
        String type = null;
        for (ConsignmentWSStatus status : ConsignmentWSStatus.values()) {
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

    public static ConsignmentWSStatus parseStr(String id) {
        ConsignmentWSStatus type = null;
        for (ConsignmentWSStatus status : ConsignmentWSStatus.values()) {
            if (status.getId().equalsIgnoreCase(String.valueOf(id))) {
                type = status;
                break;
            }
        }
        if (type == null) {
            type = ConsignmentWSStatus.REJECTED;
        }
        return type;
    }

    public String getId() {
        return id;
    }

}

