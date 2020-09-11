package co.moviired.acquisition.common.util;

public final class ConstantsHelper {

    // Yml properties
    public static final String PROJECT_PATH = "${spring.application.root}";
    public static final String PING_YML_ROUTE = "${spring.application.services.rest.ping.path}";
    public static final String SCHEDULER_HELPER_PATH = "${spring.application.services.rest.schedulerHelper.path}";
    public static final String APPLICATION_NAME = "${spring.application.name}";

    public static final String SPRING_CONFIG_PREFIX = "spring.application";
    public static final String STATUS_CODES_PREFIX = "status-codes";
    public static final String MAHINDRA_PROPERTIES_PREFIX = "providers.mahindra";
    public static final String SCHEDULER_PROPERTIES_PREFIX = "providers.scheduler-helper";
    public static final String COMPONENT_PROPERTIES_PREFIX = "properties";

    public static final String CRYPTO_KEY = "crypt.key";
    public static final String CRYPTO_INIT_VECTOR = "crypt.initializationVector";

    // Scheduled
    public static final long INITIAL_SCHEDULED_DELAY = 5000;

    // Beans names
    public static final String SIGNATURE_HELPER = "signatureHelper";
    public static final String CRYPTO_HELPER = "cryptoHelper";
    public static final String MAHINDRA_API = "mahindraApi";
    public static final String SCHEDULER_HELPER_API = "schedulerHelperApi";

    // Components
    public static final String MAHINDRA = "Mahindra";

    // Changelog
    public static final String INDEX = "index";
    public static final String NAME = "name";
    public static final String ROOT_PATH = "/";

    // Operations
    public static final String PING_SERVICE = "ping";
    public static final String SCHEDULER_HELPER = "schedulerHelper";

    // Logs on start application
    public static final String LOG_START_PROJECT = "{} Application started";
    public static final String LOG_PORT_OF_PROJECT = "Port: {}";
    public static final String LOG_PROJECT_VERSION = "Version: {}";
    public static final String LOG_RUN_OK = "Launched [OK]";

    // Primary logs
    public static final String LBL_START = "STARTING TRANSACTION";
    public static final String LBL_REQUEST_TYPE = "[{}] REQUEST: [TYPE: \"{}\", BODY: \"{}\"]"; // Service request
    public static final String LOG_NUMBER_OF_REQUEST = "USER OWN OF REQUEST [{}]"; // User of transaction
    public static final String LOG_THIRD_REQUEST = "[{}] REQUEST: [URL: \"{}\", BODY: \"{}\"]"; // Third request
    public static final String LBL_RESPONSE = "[{}] {} RESPONSE: [\"{}\"]"; // Third or service response
    public static final String LBL_ERROR = "[{}] ERROR RESPONSE: [\"{}\"]"; // Error response
    public static final String LBL_ERROR_THIRD = "[{}] {} ERROR RESPONSE: [\"{}\"]"; // Error response
    public static final String LBL_END = "END TRANSACTION";

    public static final String LOG_ERROR = "An error occurred executing operation {}: {}";

    // Scheduler helper
    public static final String LOG_NEXT_EXECUTION = "NEXT EXECUTION OF PROCESS {}: {} WAITING {} MILLISECONDS";
    public static final String ASSIGN_NEXT_EXECUTION = "ASSIGN NEXT EXECUTION FOR PROCESS {} MINIMUM TIME BETWEEN INSTANCES {}";
    public static final String SUCCESS_NEXT_EXECUTION = "NEXT TIME FOR EXECUTE PROCESS {} IS {}";
    public static final String ERROR_ASSIGN_TIME = "ERROR ASSIGN TIME FOR PROCESS: {}";

    public static final String START_EXECUTION_SCHEDULER = "START EXECUTION OF SCHEDULER PROCESS {}";
    public static final String DECISION_EXECUTE_SCHEDULER = "EXECUTION SCHEDULER PROCESS {} DECISION: {}";
    public static final String END_EXECUTION_SCHEDULER = "END EXECUTION OF SCHEDULER PROCESS {}";

    // Logs
    public static final String LOG_SERVICE_METHOD_NOT_FOUND = "Service method {} not found";
    public static final String LOG_SERVICE_METHOD_IS_NOT_ENABLED = "Service method {} is not enabled";

    public static final String LOG_SCHEDULER_NOT_FOUND = "Scheduler method {} not found";
    public static final String LOG_SCHEDULER_IS_NOT_ENABLED = "Scheduler method {} is not enabled";
    public static final String FIRST_PART_IDENTIFICATION_ASSIGN_SCHEDULER_TIME = "GET NEXT TIME FOR PROCESS: ";

    // Ping response
    public static final String OK = "OK";
    public static final String ERROR = "ERROR: ";

    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CORRELATIVE_HEADER = "Correlative";
    public static final String MERCHANT_ID_HEADER = "merchantId";
    public static final String POS_ID_HEADER = "posId";

    public static final String DEFAULT_POS_ID = "10001";

    // Correlative
    static final String CORRELATIVE_ID = "correlation-id";
    static final String COMPONENT_CORRELATIVE = "component";

    // User types
    public static final String CHANNEL = "CHANNEL";
    public static final String SUBSCRIBER = "SUBSCRIBER";

    // Regex
    public static final String REGEX_CLEAN_PROCESS = "[^a-z0-9]";
    public static final String REGEX_ALL = ".";
    public static final String REGEX_CLEAN_NAMES = "[^a-zA-Z ]";
    public static final String NOT_NUMBER_REGEX = "\\D";
    public static final String SHA_256_VALIDATE_REGX = ".{64}";
    public static final String PHONE_VALIDATE_REGX = "3\\d{9}";
    public static final String JUMP_LINE_REGEX = "\r\n|\n";
    public static final String REGEX_REPLACE_JSON_VALUE = "\".*?:.*?\".*?\"";
    public static final String REGEX_PIPE = "\\|";
    public static final String REGEX_REMOVE_LAMBDA_OF_METHOD_NAME = "lambda\\$";
    public static final String REGEX_REMOVE_FINAL_OF_LAMBDA_METHOD_NAME = "\\$.*";

    // Util
    public static final Long MILLIS_IN_ONE_SECOND = 1000L;
    public static final Integer UNKNOWN_CODE_ENUM = -1;
    public static final Integer AUTHORIZATION_LENGTH = 15;
    public static final Integer PHONE_NUMBER_LENGTH = 10;
    public static final Integer PIN_LENGTH = 4;
    public static final double ZERO_DOUBLE = 0.0;
    public static final int ZERO_INT = 0;
    public static final int ONE_INT = 1;
    public static final int TWO_INT = 2;
    public static final int FOUR_NUMBER = 4;
    public static final Long MONTH_MILLIS = 2592000000L;
    public static final Long HOUR_MILLIS = 3600000L;
    public static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String LONG_LINE = "------------------------------------------------";
    public static final String JUMP_LINE = "\r\n";
    public static final String STRING_LINE = "-";
    public static final String EMPTY_STRING = "";
    public static final String SEPARATOR_PIPE = "|";
    public static final String ASTERISK = "*";
    public static final String TWO_DOTS = ":";
    public static final String SEMICOLON = ";";
    public static final String TWO_DOTS_SPACE = ": ";
    public static final String ZERO_STRING = "0";
    public static final String ONE_STRING = "1";
    public static final String EMPTY_JSON = "{}";
    public static final String QUOTES = "\"";
    public static final String TRUE_STRING = "true";
    public static final String COLUMN_DEFINITION_TINYINT = "TINYINT";
    public static final String COLUMN_DEFINITION_ENUM = "ENUM";
    public static final String UTF_8 = "UTF-8";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String TIME_COMPLETE = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_PIN = "0000";
    public static final String PROCESS_PLACE_HOLDER = "{process}";
    public static final String BASIC = "Basic";
    public static final String COMMON_PACKAGE = "common.service";
    public static final String ATTACHMENT_FILE_NAME = "attachment; filename=";

    private ConstantsHelper() {
        //Not is necessary this implementation
    }
}

