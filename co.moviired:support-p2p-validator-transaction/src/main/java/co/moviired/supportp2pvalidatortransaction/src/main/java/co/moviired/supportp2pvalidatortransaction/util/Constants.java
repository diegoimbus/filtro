package co.moviired.supportp2pvalidatortransaction.util;

public class Constants {

    // Yml
    public static final String RATE_VALIDATE_TRANSACTIONS_P2P = "${spring.application.schedulers.validateTransactionsP2p.rate}";

    public static final String PREFIX_SUPPORT_SMS = "providers.support-sms";

    // Beans
    public static final String SUPPORT_SMS_API = "supportSMSAPi";

    // Schedulers
    public static final String SCHEDULER_VALIDATE_TRANSACTIONS_P2P = "validateTransactionsP2p";


    public static final String RESPONSE = "Response: ";
    public static final String REQUEST = "Request: ";
    public static final String REPLACE_MSISDN = "0000000000";
    public static final String TRANSACTION_OK = "200";
    public static final String LBL_INVALID_PARAMETERS = "Parametros invalidos";
    public static final String LBL_END_VALIDATION = "**********Se finaliza la  validación de los usuarios enviados**********";
    public static final String LBL_NO_EXISTEN_TRANSACCIONES = "No existen transacciones pendientes.";
    public static final String ERROR_EXCEPTION = "Ocurrio un error [Exception]: ";
    public static final String LBLREQUEST = "Request enviado: ";
    public static final String LBLRESPONSE = "Response: ";

    public static final String AES = "AES";
    public static final String MD5 = "MD5";

    public static final String INITIALIZED = "Initialized";
    private Constants() {
        //Not is necessary this implementation
    }
}

