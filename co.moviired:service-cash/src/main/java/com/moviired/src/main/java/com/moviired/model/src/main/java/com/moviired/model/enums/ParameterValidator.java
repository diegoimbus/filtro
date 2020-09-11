package com.moviired.model.enums;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */
public enum ParameterValidator {
    AUTHORIZATION("Error de parámetro authorization", "^\\d{10}+[:]+\\d{4}$"),
    MSISDN("Error de parámetro msisdn", "^\\d{10}$"),
    DATE("Error de parámetro fecha", "^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"),
    CORRELATIONID("Error de parámetro correlationId", "^\\w{1,100}$"), STATUS("Error de parámetro status", "^[0-2]{1}$"),
    BANKID("Error de parámetro bankId", "^[a-zA-Z0-9\\.\\-]{1,20}$"), AMOUNT("Error de parámetro AMOUNT", "^\\d{1,10}$"),
    AGREMENTNUMBER("Error de parámetro agreementNumber", "^\\w{1,100}"),
    PAYMENTREFERENCE("Error de parámetro agreementNumber", "^\\w{1,50}"),
    ADDRESS("Error de parámetro", "^[a-zA-ZáéíóúÁÉÍÓÚ\\s-]{1,30}$"),
    BRANCHOFFICE("Error de parámetro branchOffice", "^[0-9a-zA-ZáéíóúÁÉÍÓÚ\\!\\_\\s\\-]{1,100}$"),
    REASON("Error de parámetro reason", "^[a-zA-z0-9\\sáéíóúÁÉÍÓÚ\\-\\>]{1,150}$"),
    IMAGE_B64("Error de parámetro voucher", "^data:image\\/png;base64,.+");

    private String msgError;
    private String regex;

    ParameterValidator(String pMsgError, String pRegex) {
        this.msgError = pMsgError;
        this.regex = pRegex;
    }

    public String getMsgError() {
        return msgError;
    }

    public String getRegex() {
        return regex;
    }

}

