package co.moviired.register.domain.enums.register;

import static co.moviired.register.helper.ConstantsHelper.*;

/**
 * List of possibles status responses of this component
 */
public enum ServiceStatusCode {

    SUCCESS(SUCCESS_CODE, STATUS_TYPE_SUCCESS),
    BAD_REQUEST_PHONE_NUMBER(BAD_REQUEST_PHONE_NUMBER_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST_PHONE_SERIAL_NUMBER(BAD_REQUEST_PHONE_SERIAL_NUMBER_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST_ADO_TRANSACTION_ID(BAD_REQUEST_ADO_TRANSACTION_ID_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST_USER(BAD_REQUEST_USER_CODE, STATUS_TYPE_FAIL),
    USER_HAS_A_PREVIOUS_PENDING_REGISTRY_ON_ADO(USER_HAS_A_PREVIOUS_PENDING_REGISTRY_ON_ADO_CODE, STATUS_TYPE_FAIL),
    NOT_FOUND(NOT_FOUND_CODE, STATUS_TYPE_FAIL),
    REGISTER_ALTERED(REGISTER_ALTERED_CODE, STATUS_TYPE_FAIL),
    PROCESS_NOT_FOUND(PROCESS_NOT_FOUND_CODE, STATUS_TYPE_FAIL),
    USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM(USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST_STATUS_IS_REQUIRED(BAD_REQUEST_STATUS_IS_REQUIRED_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST_USER_IS_NOT_IN_FORM_STATUS(BAD_REQUEST_USER_IS_NOT_IN_FORM_STATUS_CODE, STATUS_TYPE_FAIL),
    SERVER_ERROR(SERVER_ERROR_CODE, STATUS_TYPE_FAIL),
    BAD_REQUEST(BAD_REQUEST_CODE, STATUS_TYPE_FAIL),
    AUTHORIZATION_HEADER_INVALID(AUTHORIZATION_HEADER_INVALID_CODE, STATUS_TYPE_FAIL),
    USER_IN_BLACK_LIST(USER_IN_BLACK_LIST_CODE, STATUS_TYPE_FAIL),
    HASH_NOT_MATCH(HASH_NOT_MATCH_CODE, STATUS_TYPE_FAIL);

    private String statusCode;
    private String statusType;

    ServiceStatusCode(String pStatusCode, String pStatusType) {
        this.statusCode = pStatusCode;
        this.statusType = pStatusType;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusType() {
        return statusType;
    }
}

