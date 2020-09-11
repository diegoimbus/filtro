package co.moviired.acquisition.util;

public final class IncommStatusHelper {

    public static final String SUCCESS_0 = "0";
    public static final String SUCCESS_00 = "00";
    public static final String INVALID_REQUEST = "10010";
    public static final String INVALID_AMOUNT = "10021";
    public static final String SYSTEM_ERROR = "10029";
    public static final String CARD_OR_CODE_IS_ALREADY_ACTIVE = "10030";
    public static final String CARD_IS_ALREADY_INACTIVE = "10031";
    public static final String CARD_OR_CODE_IS_REDEEMED = "10038";
    public static final String INVALID_USER = "10040";
    public static final String CARD_OR_ACCOUNT_IS_INVALID = "10043";
    public static final String EXPIRED_CARD_OR_CODE = "10044";
    public static final String CARD_IS_LOST_OR_STOLEN = "10048";
    public static final String NOT_REVERSIBLE = "10067";
    public static final String ORIGINAL_TRANSACTION_NOT_FOUND = "10069";
    public static final String CARD_OR_CODE_IS_IN_INVALID_STATE = "10073";
    public static final String INVALID_CURRENCY_CODE = "10081";
    public static final String SUSPECT_FRAUD = "10083";
    public static final String ACTION_NOT_SUPPORTED = "10090";

    private IncommStatusHelper() {
        //Not is necessary this implementation
    }
}

