package co.moviired.support.endpoint.util.exceptions;

public enum CodeErrorEnum {
    SUCCESSFULL("00", "successfull"),
    ERRORCHANNEL("03", "errorchannel"),
    PARSERESPONSEERROR("05", "parseresponseerror"),
    WSCLIENTERROR("07", "wsclienterror"),
    GETRESPONSECLIENTERROR("08", "getresponseclienterror"),
    WSEMPTYRESPONSE("09", "wsemptyresponse"),
    EXCEPTION("10", "exception"),
    IOEXCEPTION("12", "ioexception"),
    SQLEXCEPTION("13", "sqlexception"),
    ARRAYINDEXOUTOFBOUNDSEXC("14", "arrayindexoutboundexception"),
    NUMBERFORMATEXCEPTION("15", "numberformatexception"),
    BDCONNEXCEPTION("22", "bdconnectionexception"),
    PARSEEXCEPTION("28", "parseexception"),
    BOERROR("32", "boerror"),
    DAOERROR("33", "daoerror"),
    INDEXOUTOFBOUNDSEXCEPTION("34", "indexoutofboundsexception"),
    ERROR("49", "error"),
    ERRORINCORRECTDATE("55", "errorincorrectdate"),
    ERRORLENGTHINCORRECT("88", "errorlenghtincorrect"),
    ERRORINCORRECTFORMATDATE("89", "errorincorrectformatdate"),
    ERRORREQUIREDFIELD("108", "errorrequiredfield"),
    ERRCONEXIONURL("118", "errorconexionurl"),
    ERRORSEARCHWITHOUTRESULTS("134", "errorsearchwithoutresults"),
    ERRORINTEGERFIELD("136", "errorintegerfield"),
    ERRORSTRINGFIELDVALUE("137", "errorstringfieldvalue"),
    ERRORDBENGINENOTFOUND("168", "dbenginenotfound"),
    PROCEDUREMETADANOTFOUND("167", "proceduremetadatanotfound"),
    ERRORGETTINGDATASOURCE("169", "errorgettingdatasource"),
    METADATAEXCEPTION("170", "metadataexception"),
    ERRORCLOSECONNECTION("171", "errorcloseconnection"),
    ERRORQUERYMAPEMPTY("172", "querymapempty"),
    ERRORQUERYNOTFOUND("173", "querynotfound"),
    CONFIGCHANNELNOTFOUND("174", "configchannelnotfound"),
    ERRORINVALIDVALUE("193", "errorinvalidvalue"),
    ERRORINSERT("194", "errorinsert"),
    ERRORUPDATE("195", "errorupdate"),
    ERRORDELETE("196", "errordelete"),
    ERRORFINDINTVALUE("197", "errorfindintvalue"),
    ERRORFINDLONGVALUE("198", "errorfindlongvalue"),
    ERRORFINDSTRINGVALUE("199", "errorfindstringvalue"),
    ERRORFINDOBJECTS("200", "errorfindobjects"),
    ERRORFINDALL("201", "errorfindall"),
    ERROREXECUTEFUNCTION("202", "errorexecutefunction"),
    ERRORBADSQLGRAMMAR("203", "errorbadsqlgrammar"),
    ERRORDATAINTEGRITYVIOLATION("204", "errordataintegrityviolation"),
    ERROREXECUTESUPPORTEDPROCEDURE("209", "errorexecutesupportedprocedure"),
    ERROR_BD_CONNECTION("361", "errorbdconectionerror"),
    ERRORUNCATEGORIZEDEX("205", "erroruncategorizedex"),
    ERRORTYPEMISMATCHEX("206", "errortypemismatchex"),
    ERRORINCORRECTUPDATESEMANTICS("207", "errorincorrectupdatesemantics"),
    ERRORPERMISSIONDENIED("208", "errorsqlpermissiondenied"),
    ERRORLISTWRONGSIZE("218", "errorlistwrongsize"),
    ERROR_RESPONSE_MAHINDRA("376", "errorresponsemahindra"),
    ERRORREQUIREDALLFIELDS("377", "errorrequiredAllFields"),
    ERRORFACTORY("378", "errorfactory"),
    ERRORCHECKAMOUNT("379", "errorcheckamount"),
    ERROR_OUT_OF_RANGE("380", "erroroutofrange"),
    ERRORMAXLENGTHNOTALLOWED("381", "errormaxlengthnotallowed"),
    ERRORALPHABET("382", "erroralphabet"),
    ERRORREGISTERCONSIGNMENT("383", "errorregisterconsignment"),
    ERRORWRONGCONSIGNMENTCHANNEL("384", "errorwrongconsignmentchannel"),
    ERRORDISPERSE("385", "errordisperse"),
    ERROR_NO_PROPERTY_CONSIGNMENT("386", "propertynotfound.consignment"),
    ERROR_MALFORMED_URL_MAHINDRA("387", "malformedurlmahindra"),
    ERROR_NO_COMMUNICATION_MAHINDRA("388", "nocommunicationmahindra"),
    ERROR_NO_RESPONSE_MAHINDRA("389", "noresponsemahindra"),
    ERROR_MIN_LENGTH_NOT_ALLOWED("390", "errorminlengthnotallowed"),
    PENDING_REVERT("391", "pendingrevert"),
    FAILED_REVERT("392", "failedrevert"),
    PAYMENT_NO_FOUND("393", "paymentnofound"),
    ERROR_TIME_LIMIT("394", "errortimelimit"),
    SUCCESSFUL_REVERT("395", "successfulrevert"),
    SUCCESSFUL_NOTIFICATION("000", "successfullnotification"),
    FAILED_NOTIFICATION("397", "errorfailednotification"),
    ERROR_MAHINDRA("398", "errormahindra"),
    ERROR_REGISTER_DB_CONSIGNMENT("399", "errorregisterdbconsignment"),
    SUCCESSFUL_GET_BILL_AMOUNT("396", "successfullgetbillamount"),
    BILL_DOES_NOT_EXIST("001", "errorbilldoesnotexist"),
    BILL_ALREADY_PAID("002", "errorbillalreadypaid"),
    INVALID_BILL_NUMBER_LENGTH("400", "invalidbillnumberlength"),
    ERROR_DUPLICATE_TRANSACTION("004", "errorduplicateTransaction"),
    ERROR_DUPLICATE_REVERT("099", "errorduplicateRevert"),
    ERROR_STATE_REVERT("099", "errorstateRevert"),
    REJECTED_RESPONSE_MAHINDRA("098", "rejectedresponsemahindra"),
    ERROR_USER("097", "erroruser"),
    AUTHENTICATION_ERROR("999", "errorauthentication");

    private final String code;
    private final String description;

    private CodeErrorEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "CodeErrorEnum[code=" + this.getCode() + ", descripcion=" + this.getDescription() + "]";
    }

    public static CodeErrorEnum getEnum(String key) {
        CodeErrorEnum response = ERROR;
        CodeErrorEnum[] arr = values();
        int len = arr.length;

        for(int i = 0; i < len; ++i) {
            CodeErrorEnum enumeration = arr[i];
            if (enumeration.getCode().equals(key)) {
                response = enumeration;
                break;
            }
        }

        return response;
    }
}

