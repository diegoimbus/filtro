package co.moviired.support.domain.enums;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
public enum HistoryType {

    BAR(1), UNBAR(2);

    private final int id;

    HistoryType(int pid) {
        this.id = pid;
    }

    public int getId() {
        return this.id;
    }
}

