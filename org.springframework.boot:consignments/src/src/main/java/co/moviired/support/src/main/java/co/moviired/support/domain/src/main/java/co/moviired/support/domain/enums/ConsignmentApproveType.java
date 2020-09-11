package co.moviired.support.domain.enums;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
public enum ConsignmentApproveType {

    MANUALLY(1), AUTOMATICALLY(2);

    private final int id;

    ConsignmentApproveType(int id) {
        this.id = id;
    }

    public static ConsignmentApproveType parse(int id) {
        ConsignmentApproveType type = null;
        for (ConsignmentApproveType status : ConsignmentApproveType.values()) {
            if (status.getId() == id) {
                type = status;
                break;
            }
        }
        if (type == null) {
            type = ConsignmentApproveType.MANUALLY;
        }
        return type;
    }

    public int getId() {
        return this.id;
    }
}

