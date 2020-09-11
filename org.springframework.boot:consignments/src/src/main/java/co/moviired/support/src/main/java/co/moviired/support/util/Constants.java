package co.moviired.support.util;

public class Constants {

    public static final String PREFIX_EMAIL_GENERATOR = "email-sender";
    public static final String PREFIX_SERVICE_MANAGER = "service-manager";

    // Response codes
    public static final String SUCCESS_CODE = "200";

    // Logs constants
    public static final String LBL_START = "STARTING TRANSACTION";
    public static final String LBL_END = "END TRANSACTION";
    public static final String LBL_REQUEST_TYPE = "REQUEST  - Type  [{}] - [{}]";
    public static final String LBL_RESPONSE = "{} RESPONSE - Value [{}]";
    public static final String LBL_ERROR = "{} ERROR RESPONSE - VALUE  [{}]";
    public static final String LOG_THIRD_REQUEST = "INVOKE {}: {URL: '{}', body : '{}'}";
    public static final String OK = "OK";
    public static final String LOG_NUMBER_OF_REQUEST = "USER OWN OF REQUEST [{}]";

    public static final String OPERATION_GET_AVAILABLE_EXTRACTS = "OPERATION GET AVAILABLE EXTRACTS";
    public static final String OPERATION_GENERATE_EXTRACT = "OPERATION GENERATE EXTRACTS";

    public static final String LOG_ERROR_EXECUTING_GET_AVAILABLE_EXTRACTS_STEPS = "Error executing get available extracts steps: {}";
    public static final String LOG_ERROR_GETTING_AVAILABLE_EXTRACTS = "Error getting available extracts: {}";

    public static final String LOG_ERROR_EXECUTING_GENERATE_EXTRACT_STEPS = "Error executing generate extract steps: {}";
    public static final String LOG_ERROR_GENERATING_EXTRACT = "Error generating extract: {}";

    public static final String LOG_DOCUMENT_GENERATED = "Document generated successfully for token {}";
    public static final String LOG_ERROR_GENERATING_DOCUMENT_FOR_TOKEN = "Error generating document fot token {}: {}";
    public static final String LOG_DOCUMENT_NOT_FOUND_FOR_TOKEN = "Document not found for token {}";
    public static final String LOG_VALIDATING_SIGNATURE_DOCUMENT = "Validating signature of document with token {}";
    public static final String LOG_ERROR_VALIDATING_SIGNATURE_OF_DOCUMENT = "Error validating signature of document {}: {}";
    public static final String LOG_DOCUMENT_NOT_HAS_SIGNATURE = "Error document not has signature: {}";
    public static final String LOG_DOCUMENT_ALTERED = "Error signature altered, document {}";
    public static final String OPERATION_GENERATE_CERTIFICATE = "GENERATE CERTIFICATE";
    public static final String LOG_ERROR_GENERATING_SIGNATURE_OF_DOCUMENT = "Error generating signature of document: {}";
    public static final String LOG_ERROR_GENERATING_DOCUMENT = "Error generating document: {}";
    public static final String LOG_ERROR_CREATING_AUTHORIZATION_FOR_CRUD_EMAIL = "Error creating authorization for crud email: {}";
    public static final String SERVICE_MANAGER_CRUD_EMAIL_ACTION_METHOD = "Query";
    public static final String ERROR_PARAMETER_BODY = "body is required";
    public static final String ERROR_PARAMETER_SEND_EMAIL = "sendEmail is required";
    public static final String ERROR_PARAMETER_AVAILABLE_EXTRACT = "availableExtract is required";
    public static final String ERROR_PARAMETER_YEAR = "year is required";
    public static final String ERROR_PARAMETER_MONTH = "month is required";

    // Util
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String EMPTY_STRING = "";
    public static final String REGEX_ALL = ".";
    public static final String ASTERISK = "*";
    public static final String TWO_DOTS = ":";
    public static final Integer AUTHORIZATION_LENGTH = 15;
    public static final Integer PHONE_NUMBER_LENGTH = 10;
    public static final Integer PIN_LENGTH = 4;
    public static final int ZERO_INT = 0;
    public static final int ONE_INT = 1;
    public static final String EMPTY_JSON = "{}";
    public static final String STRING_LINE = "-";
    public static final String MAHINDRA_DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SEPARATOR = "|";

    public static final String PREFIX_CERTIFICATES = "certificates";
    public static final String SIGNATURE_HELPER = "signatureHelper";

    public static final String SERVER_ERROR_CODE = "500";
    public static final String ALTERED_CODE = "403";
    public static final String NOT_FOUND_CODE = "404";
    public static final String AUTHORIZATION_HEADER_INVALID_CODE = "401";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String CHANNEL = "CHANNEL";
    public static final String TOKEN_TAG = "{token}";

    public static final String EMAIL = "email";
    public static final String LINK = "link";
    public static final String MESSAGE = "message";
    public static final String IMAGE = "image";
    public static final String IMAGE_OPTION = "extracto";
    public static final String MESSAGE_FOR_EMAIL = "Extracto";

    public static final String EMAIL_GENERATOR_API = "emailGeneratorApi";
    public static final String SERVICE_MANAGER_API = "serviceManagerApi";
    public static final String SUPPORT_USER_API = "supportUserApi";

    // Components
    public static final String MAHINDRA_COMPONENT = "Mahindra";
    public static final String EMAIL_SENDER_COMPONENT = "EmailSender";
    public static final String SERVICE_MANAGER_COMPONENT = "ServiceManager";

    // Logs
    public static final String LOG_ERROR_INVOKING_SEND_EMAIL = "Error invoking send email: {}";

    private Constants() {
        //Not is necessary this implementation
    }
}

