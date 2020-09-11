package co.moviired.acquisition.util;

public final class ConstantsHelper {

    // Yml
    public static final String PATH_INCOMM_REQUEST = "${spring.application.services.rest.incommRequest.path}";
    public static final String PATH_PRODUCT_CODE_VALIDATION = "${spring.application.services.rest.productValidation.path}";
    public static final String PATH_PRODUCT_CODE_REDEEM = "${spring.application.services.rest.productRedeem.path}";
    public static final String PATH_PRODUCT_CODES_CREATION = "${spring.application.services.rest.productCodesCreation.path}";
    public static final String PATH_GET_PRODUCT_CODES = "${spring.application.services.rest.getProductCodes.path}";
    public static final String PATH_GET_LOTS_IDENTIFIERS = "${spring.application.services.rest.getLotsIdentifiers.path}";

    public static final String TEST_JOB_RATE = "${spring.application.schedulers.testScheduler.rate}";

    // Database
    public static final String PRODUCT_CODE_ID = "product_code_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String CATEGORY_ID = "category_id";

    // Utils
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String HHMMSS = "HHmmss";
    public static final String PIN_CRYPTO_KEY = "nQwcVQ0nNTJEgGOx0OOP";
    public static final String UPC = "UPC";
    public static final String PAN = "PAN";
    public static final String PIN = "PIN";
    public static final String DEFAULT_CODE = "-1";

    // LOGS
    public static final String MAHINDRA_LOGIN_PROCESS = "MAHINDRA LOGIN";
    public static final String VALIDATING_REQUEST = "Validating request";
    public static final String REQUEST_VALIDATION_IS_SUCCESS = "Request validation is success";
    public static final String SELECTING_TRANSACTION_TYPE = "selecting transaction type";
    public static final String VALIDATING_CURRENCY_CODE = "Validating currency code";
    public static final String VALIDATING_AMOUNT = "Validating amount";
    public static final String VALIDATING_PRODUCT_CODE_WITH_ID = "Validating product code with id: {}";
    public static final String VALIDATING_ACTION_METHOD = "Validating action method: {}";
    public static final String ACTION_METHOD_NOT_FOUND = "Action {} method not found";
    public static final String DOING_ACTIVATION_AUTHORIZATION_CODE_WITH_ID = "DOING ACTIVATION AUTHORIZATION CODE WITH ID {}";
    public static final String DOING_ACTIVATION_PRE_AUTHORIZATION_CODE_WITH_ID = "DOING ACTIVATION PRE-AUTHORIZATION CODE WITH ID {}";
    public static final String DOING_ACTIVATION_REVERSAL_CODE_WITH_ID = "DOING ACTIVATION REVERSAL CODE WITH ID {}";
    public static final String EXECUTING_ACTIVATION_REVERSAL_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID = "Executing activation reversal extra validations for code with id: {}";
    public static final String CODE_ACTIVATION_IS_NOT_REVERSIBLE_FOR_TIME_OUT_FOR_CODE_WITH_ID = "Code activation is not reversible for time out for code with id: {}";
    public static final String DOING_DEACTIVATION_CODE_WITH_ID = "DOING DEACTIVATION CODE WITH ID {}";
    public static final String EXECUTING_DEACTIVATION_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID = "Executing deactivation extra validations for code with id: {}";
    public static final String CODE_IS_NOT_DEACTIVATING_FOR_TIME_OUT_FOR_CODE_WITH_ID = "Code is not deactivating for time out for code with id: {}";
    public static final String DOING_DEACTIVATION_REVERSAL_CODE_WITH_ID = "DOING DEACTIVATION REVERSAL CODE WITH ID {}";
    public static final String EXECUTING_DEACTIVATION_REVERSAL_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID = "Executing deactivation reversal extra validations for code with id: {}";
    public static final String CODE_DEACTIVATION_IS_NOT_REVERSIBLE_FOR_TIME_OUT_FOR_CODE_WITH_ID = "Code deactivation is not reversible for time out for code with id: {}";
    public static final String CREATING_BASE_RESPONSE = "Creating base response";
    public static final String BASE_RESPONSE_CREATED = "Base response created";
    public static final String MAPPING_END_OF_RESPONSE = "Mapping end of response";
    public static final String AN_ERROR_OCCURRED = "An error occurred: {}";
    public static final String THE_TRANSACTION_HAS_RESP_CODE_AND_MESSAGE = "The transaction has respCode {} and message {}";
    public static final String FIELD_PIN_IS_REQUIRED = ": field \"pin\" is required";
    public static final String NUMBER_OF_CODES_TO_CREATE_IS_INVALID = "Number of codes to create is invalid";
    public static final String FINDING_PRODUCT_FOR_IDENTIFIER = "Finding product for identifier: {}";
    public static final String PRODUCT_NOT_FOUND_FOR_IDENTIFIER = "Product not found for identifier: {}";
    public static final String STARTING_CREATION_OF_CODES_FOR_PRODUCT_WITH_IDENTIFIER = "Starting creation of codes for product with identifier {}";
    public static final String CREATING_CODES = "Creating codes {}/{}";
    public static final String ERROR_CREATING_ONE_CODE = "Error creating one code: {}";
    public static final String CREATING_CARD_CODE_PAN = "Creating card code PAN";
    public static final String CREATING_PIN = "Creating PIN";
    public static final String SAVING_CARD_CODES = "Saving card codes";
    public static final String SAVE_SUCCESSFUL = "Save successful";
    public static final String SAME_PARAMETER_VALUE = "SameParameterValue";
    public static final String CODE_ALREADY_EXIST_RETRYING = "Code already exist retrying";
    public static final String ERROR_GENERATING_CODE_RETRYING = "Error generating code retrying: {}";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred: {}";
    public static final String FINDING_LOTS = "Finding lots";
    public static final String ERROR_MAPPING_CODES = "Error mapping codes: {}";
    public static final String FINDING_PRODUCT_BY_PRODUCT_CODE = "Finding product by product code: {}";
    public static final String PRODUCT_FOUND_AND_HAS_ID = "Product found and has id {}";
    public static final String PRODUCT_NOT_FOUND_WITH_CODE = "Product not found with code: {}";
    public static final String FINDING_PRODUCT_BY_IDENTIFIER = "Finding product by identifier: {}";
    public static final String PRODUCT_NOT_FOUND_WITH_IDENTIFIER = "Product not found with identifier: {}";
    public static final String FINDING_PRODUCT_CODE_IN_PRODUCT_WITH_NAME = "Finding product code in product with name: {}";
    public static final String FINDING_FOR_PIN_AND_PRODUCT_ID = "Finding for pin and product id {}";
    public static final String DECODING_PIN_OF_TRANSACTION = "Decoding pin of transaction";
    public static final String AN_ERROR_OCCURRED_DECODING_PIN = "An error occurred decoding pin: {}";
    public static final String FINDING_FOR_CARD_CODE_AND_PRODUCT_ID = "Finding for card code and product id {}";
    public static final String PRODUCT_CODE_FOR_PRODUCT_WITH_NAME_NOT_FOUND = "Product code for product with name {} not found";
    public static final String PRODUCT_WITH_ID_OR_CATEGORY_WITH_ID_NOT_HAS_ACTIVE_STATUS = "Product with id {} or category with id {} not has active status";
    public static final String CHANGING_PRODUCT_CODE_WITH_ID_TO_STATE = "Changing product code with id {} to state {}";
    public static final String SAVING_PRODUCT_CODE_WITH_ID = "Saving product code with id: {}";
    public static final String VALIDATING_SIGNATURE_FOR_PRODUCT_CODE_WITH_ID = "Validating signature for product code with id: {}";
    public static final String SIGNATURE_IS_PREVIOUSLY_ALTERED_FOR_PRODUCT_CODE_WITH_ID = "Signature is previously altered for product code with id {}";
    public static final String SIGNATURE_ALTERED_FOR_PRODUCT_CODE_WITH_ID = "Signature altered for product code with id: {}";
    public static final String VALIDATING_TRANSACTIONS_FOR_PRODUCT_CODE_WITH_ID = "Validating transactions for product code with id {}";
    public static final String THE_PRODUCT_CODE_WITH_ID_HAS_A_TRANSACTION_PENDING_THIS_TRANSACTION_IS_REJECTED = "The product code with id {} has a transaction pending, this transaction is rejected";
    public static final String CREATING_TRANSACTION_WITH_TYPE = "Creating transaction with type: {}";
    public static final String ERROR_PARSING_INCOMM_DATE_TIME = "Error parsing incomm date time: {}";
    public static final String SAVING_TRANSACTION_FOR_PRODUCT_CODE_WITH_ID = "Saving transaction for product code with id: {}";
    public static final String CHANGING_TRANSACTION_STATE_TO = "Changing transaction state to {}";
    public static final String SAVING_TRANSACTION_WITH_ID = "Saving transaction with id: {}";
    public static final String DOING_LOGIN = "Doing login";
    public static final String USER_NOT_FOUND_OR_NOT_ALLOWED_FOR_METHOD = "User not found or not allowed for method";
    public static final String ERROR_DOING_LOGIN = "Error doing login: {}";
    public static final String METHOD_USE_LOCAL_LOGIN = "Method use local login";
    public static final String VALIDATING_AUTHORIZATION_PARTS = "Validating authorization parts";
    public static final String ERROR_AUTHORIZATION_NOT_IS_VALID = "Error authorization not is valid";
    public static final String LOGIN_SUCCESSFUL_WITH_USER = "Login successful with user \"{}\"";
    public static final String USER_IS_NOT_ALLOWED_FOR_METHOD = "User is not allowed for method \"{}\"";
    public static final String METHOD_USE_MAHINDRA_LOGIN = "Method use mahindra login";
    public static final String ERROR_VALIDATING_AUTHORIZATION_PARTS = "Error validating authorization parts: {}";
    public static final String VALIDATING_IF_USER_TYPE_IS_ALLOWED = "Validating if user type {} is allowed";
    public static final String USER_TYPE_IS_ALLOWED_FOR_THIS_METHOD = "User type {} is allowed for this method";
    public static final String USER_TYPE_IS_NOT_ALLOWED = "User type \"{}\" is not allowed";
    public static final String VALIDATING_LAST_TRANSACTION_FOR_PRODUCT = "Validating last transaction for product code with id {}";
    public static final String LAST_TRANSACTION_NOT_FOUND_FOR_PRODUCT = "Last transaction not found for product code with id {}";
    public static final String SIGNATURE_ALTERED_FOR_TRANSACTION = "Signature altered for transaction with id: {}";
    public static final String THE_LAST_TRANSACTION_NOT_IS_VALID_FOR_PROCESS = "The last transaction of product code with id {} not is valid for this process";

    private ConstantsHelper() {
        //Not is necessary this implementation
    }
}

