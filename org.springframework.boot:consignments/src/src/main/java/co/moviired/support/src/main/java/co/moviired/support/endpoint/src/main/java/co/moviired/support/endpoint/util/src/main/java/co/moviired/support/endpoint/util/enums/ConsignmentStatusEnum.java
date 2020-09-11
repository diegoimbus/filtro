package co.moviired.support.endpoint.util.enums;

public enum ConsignmentStatusEnum {
    PENDING_APPROVAL(1),
    APPROVED(2),
    PENDING_REVERT(3),
    REVERSED(4),
    REJECTED(5);

    private final int statusId;

    ConsignmentStatusEnum(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return this.statusId;
    }
}
