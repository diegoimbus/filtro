package com.moviired.model.enums;

public enum CashOutStatus {
    INITIALIZED("TI", 1), // pendiente
    COMPLETED("TS", 2), // pagado
    EXPIRED("TV", 3), // anulado
    DECLINED("TD", 3), // anulado
    REVERSED("TR", 7), // reversado
    PROCESSED("TP", 2), // pagado
    ALTERED("TA", 4), // alterado por firma
    TRANSACTION_COST("TC", 5), //falta cobrar el costo de transacción
    REVERSED_TRANSACTION_COST("TC", 8), //falta reversar el costo de la transacción
    UNFREEZE_WRONG("TC", 9); //No se pudo descongelar el dinero


    private final String val;
    private final Integer giroStatus;

    CashOutStatus(String pVal, Integer pGiroStatus) {
        this.val = pVal;
        this.giroStatus = pGiroStatus;
    }

    public String value() {
        return val;
    }

    public Integer giroCode() {
        return giroStatus;
    }
}

