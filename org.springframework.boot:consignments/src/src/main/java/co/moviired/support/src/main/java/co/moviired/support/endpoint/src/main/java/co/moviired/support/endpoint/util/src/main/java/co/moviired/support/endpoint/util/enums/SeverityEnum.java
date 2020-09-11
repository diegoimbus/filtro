package co.moviired.support.endpoint.util.enums;

public enum SeverityEnum {
    ERROR("E"),
    INFO("I"),
    WARNING("W");

    private final String severity;

    SeverityEnum(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return this.severity;
    }
}
