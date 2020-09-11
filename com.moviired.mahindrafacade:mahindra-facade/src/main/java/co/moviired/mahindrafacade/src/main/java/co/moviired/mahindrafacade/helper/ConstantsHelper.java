package co.moviired.mahindrafacade.helper;

public final class ConstantsHelper {

    // yml properties
    public static final String STATUS_CODES_PREFIX = "status-codes";
    public static final String MPIN = "MPIN";
    public static final String PIN = "PIN";
    public static final String PASSCODE = "PASSCODE";

    // 200 TRANSACTION SUCCESS
    public static final String TRANSACTION_200 = "200";

    // 401 TRANSACTION FAILED
    public static final String TRANSACTION_401 = "401";

    public static final String GEN_MAL = "GEN_MAL";
    public static final String GEN_FEM = "GEN_FEM";
    public static final String DOB_COMPARE = " 00:00:00.0";

    // Error MH
    public static final String ERROR_TYPE = "GENERAL";
    public static final String ERROR_TXN_STATUS = "99033";
    public static final String ERROR_MESSAGE = "Initiator is invalid";

    private ConstantsHelper() {
        super();
    }

}

