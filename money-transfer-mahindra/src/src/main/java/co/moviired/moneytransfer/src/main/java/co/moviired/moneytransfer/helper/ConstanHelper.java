package co.moviired.moneytransfer.helper;

public final class ConstanHelper {

    private ConstanHelper() {
    }

    // Yml properties
    public static final String STATUS_CODES_PREFIX = "status-codes";
    // Response codes
    public static final String ERROR_TYPE_DATA = "1";
    public static final String ERROR_TYPE_COMUNICATION = "2";
    public static final String ERROR_TYPE_PROCESING = "3";
    public static final String ERROR_TYPE_OTHER = "4";
    public static final String SUCCESS_CODE_0 = "0";
    public static final String SUCCESS_CODE_00 = "00";
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_CODE_201 = "201";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String ERROR_GENERIC = "901";
    public static final String ERROR_CANCELING = "902";
    public static final String ERROR_PLACING = "903";
    public static final String ERROR_NOT_EXIST_REGISTRY = "904";
    public static final String ERROR_BLACK_LIST = "905";
    public static final String ERROR_NOT_CLIENT_MOVII = "906";
    public static final String EXCEEDS_STOPS = "907";
    public static final String ERROR_FREIGHT = "908";
    public static final String ERROR_TXN = "909";
    //Errores Mh
    public static final String MOVILRED00474 = "MOVILRED00474";
    public static final String AUTHORIZATION_HEADER_INVALID_CODE = "401";
    public static final String REGISTER_ALTERED_CODE = "403";
    public static final String NOT_FOUND_CODE = "404";
    public static final String SERVER_ERROR_CODE = "500";
    //Params Header
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String MERCHANT_ID = "merchantId";
    public static final String POS_ID = "posId";
    //Type of person
    public static final String ORIGINATOR = "originator";
    public static final String BENEFICIARY = "beneficiary";
    public static final String PASSCODE = "PASSCODE";
    public static final String PIN = "PIN";
    public static final String MPIN = "MPIN";

    //Logs
    public static final String LBL_REQUEST = "REQUEST:{}";
    public static final String LBL_RESPONSE = "RESPONSE:{}";

    //Regexp
    public static final String REGEXP_1 = "[0-9]+";
    public static final String REGEXP_2 = "[a-zA-Z]+";
    public static final String REGEXP_3 = "[a-zA-Z0-9]+";
    public static final String REGEXP_4 = "[a-zA-Z0-9.]+";
    public static final String REGEXP_5 = "(CC|CE|PEP|PAS)";
    public static final String REGEXP_6 = "[a-zA-Z0-9.\\s]+";
    public static final String REGEXP_7 = "\\|";

    //Numbers
    public static final int NUMBER_1 = 1;
    public static final int NUMBER_2 = 2;
    public static final int NUMBER_3 = 3;
    public static final int NUMBER_4 = 4;
    public static final int NUMBER_5 = 5;
    public static final int NUMBER_6 = 6;
    public static final int NUMBER_7 = 7;
    public static final int NUMBER_9 = 9;
    public static final int NUMBER_10 = 10;
    public static final int NUMBER_15 = 15;
    public static final int NUMBER_20 = 20;
    public static final int NUMBER_25 = 25;
    public static final int NUMBER_2000000 = 2000000;

}

