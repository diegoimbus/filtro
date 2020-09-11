package co.moviired.register.helper;

import java.io.Serializable;

public final class ConstantsHelper implements Serializable {

    // Yml properties
    public static final String PROJECT_PATH = "${spring.application.root}";
    public static final String PING_YML_ROUTE = "${spring.application.services.ping}";
    public static final String GET_STATUS_USER_YML_ROUTE = "${spring.application.services.validate-user-status}";
    public static final String GET_STATUS_USER_PROCESS_YML_ROUTE = "${spring.application.services.validate-user-status-for-process}";
    public static final String ADD_PENDING_USER_YML_ROUTE = "${spring.application.services.create-pending-user}";
    public static final String CHANGE_FORM_COMPLETED_STATUS_YML_ROUTE = "${spring.application.services.change-status-form-completed-ordinary-deposit}";
    public static final String INACTIVE_CASE_YML_ROUTE = "${spring.application.services.inactive-ado-ordinary-deposit-case}";
    public static final String VALIDATE_SUBSIDIZED_YML_ROUTE = "${spring.application.services.validateSubsidized}";
    public static final String CHANGE_SUBSIDIZED_HASH_YML_ROUTE = "${spring.application.services.changeHashSubsidy}";
    public static final String UPLOAD_SUBSIDIZED_DOCUMENTS_YML_ROUTE = "${spring.application.services.uploadSubsidizedDocuments}";

    public static final String CREATE_PENDING_USER_ROUTE = "${spring.application.services.create-pending-user-registration}";
    public static final String INACTIVE_PENDING_USER_ROUTE = "${spring.application.services.update-user-end-registration}";
    public static final String VALIDATE_PENDING_USER_ROUTE = "${spring.application.services.validate-pending-user}";
    public static final String UPDATE_USER_PENDING_UPDATE_ROUTE = "${spring.application.services.update-user-pending-update}";
    public static final String VALIDATE_USER_PENDING_UPDATE_ROUTE = "${spring.application.services.validate-user-pending-update}";

    public static final String DEPARTMENT_ALL_VALUES = "${spring.application.services.moviired.allDepartment}";
    public static final String DEPARTMENT_VALUES = "${spring.application.services.moviired.department}";

    public static final String SPRING_CONFIG_PREFIX = "spring.application";
    public static final String BLACK_LIST_PREFIX = "providers.black-list";
    public static final String REGISTRADURIA_PREFIX = "providers.registraduria";
    public static final String STATUS_CODES_PREFIX = "status-codes";
    public static final String CLEVERTAP_CONFIG_PREFIX = "providers.clevertap-api";
    public static final String CLEAN_ADDRESS_PROPERTIES = "clean-address-properties";
    public static final String ADO_CONFIG_PREFIX = "providers.ado";
    public static final String CML_CONFIG_PREFIX = "cml";

    // Path variables
    public static final String PHONE_NUMBER = "phone";
    public static final String PHONE_SERIAL_NUMBER = "phoneSerial";
    public static final String PROCESS_NUMBER = "process";
    public static final String DOCUMENT_NUMBER = "document";
    public static final String DEPARTMENT_NAME = "departmentName";

    // Tables of db
    public static final String REGISTER_TABLE_USER = "register_user_subscriber";
    public static final String REGISTER_TABLE_PENDING_USER = "register_pending_user";
    public static final String REGISTER_TABLE_USER_CHANNEL = "register_user_channel";
    public static final String REGISTER_TABLE_USER_PENDING_UPDATE = "register_user_pending_update";
    public static final String DEPARTMENT_TABLE = "department";
    public static final String MUNICIPALITY_TABLE = "municipality";

    // Register db parameters
    public static final String REGISTER_PARAMETER_ID = "id";
    public static final String REGISTER_PARAMETER_ADO_TRANSACTION_ID = "ado_transaction_id";
    public static final String REGISTER_PARAMETER_IDENTIFICATION_TYPE_ID = "identification_type_id";
    public static final String REGISTER_PARAMETER_IDENTIFICATION_NAME = "identification_name";
    public static final String REGISTER_PARAMETER_IDENTIFICATION_NUMBER = "identification_number";
    public static final String REGISTER_PARAMETER_FIRST_NAME = "first_name";
    public static final String REGISTER_PARAMETER_SECOND_NAME = "second_name";
    public static final String REGISTER_PARAMETER_FIRST_SURNAME = "first_surname";
    public static final String REGISTER_PARAMETER_SECOND_SURNAME = "second_surname";
    public static final String REGISTER_PARAMETER_GENDER = "gender";
    public static final String REGISTER_PARAMETER_BIRTH_DATE = "birth_date";
    public static final String REGISTER_PARAMETER_REGISTRATION_DATE = "registration_date";
    public static final String REGISTER_PARAMETER_DATE_UPDATE = "date_update";
    public static final String REGISTER_PARAMETER_STATUS = "status";
    public static final String REGISTER_PARAMETER_IS_ACTIVE = "is_active";
    public static final String REGISTER_PARAMETER_PHONE_NUMBER = "phone_number";
    public static final String REGISTER_PARAMETER_PHONE_SERIAL_NUMBER = "phone_serial_number";
    public static final String REGISTER_PARAMETER_ADO_STATUS = "ado_status";
    public static final String REGISTER_PARAMETER_PROCESS = "process";
    public static final String REGISTER_PARAMETER_IS_ORDINARY_DEPOSIT_FORM_COMPLETE = "is_ordinary_deposit_form_complete";
    public static final String REGISTER_PARAMETER_SIGNATURE = "signature";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_IDNO = "idno";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_PHONE_NUMBER = "phone_number";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_SHOP_NAME = "shop_name";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_GENDER = "gender";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_ADDRESS = "address";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_DISTRICT = "district";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_CITY = "city";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_RUT = "rut";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_DIGIT_VERIFICATION = "digit_verification";
    public static final String REGISTER_PARAMETER_PENDING_UPDATE_ACTIVITY_ECONOMIC = "activity_economic";

    public static final String DEPARTMENT_PARAMETER_ID = "id";
    public static final String DEPARTMENT_PARAMETER_NAME = "name";
    public static final String MUNICIPALITY_PARAMETER_ID = "id";
    public static final String MUNICIPALITY_PARAMETER_NAME = "name";
    public static final String MUNICIPALITY_DEPARTMENT = "department_id";
    public static final String MUNICIPALITY_PARAMETER_CODE = "dane_code";

    //Jackson Mapping properties
    public static final String ADO_CASE_STATUS = "adoCaseStatus";
    public static final String STATUS_ENUM = "statusEnum";

    // Logs constants
    public static final String LOG_VERIFY_USER_STATUS_FINISHED = "*********** VERIFY USER STATUS - FINISHED ***********";
    public static final String LOG_START_PROJECT = "{} Application started";
    public static final String LOG_PORT_OF_PROJECT = "Port: {}";
    public static final String LOG_PROJECT_VERSION = "Version: {}";
    public static final String LOG_RUN_OK = "Launched [OK]";
    public static final String LBL_START = "STARTING TRANSACTION";
    public static final String LBL_END = "END TRANSACTION";
    public static final String LBL_REQUEST = "REQUEST  - Value [{}]";
    public static final String LBL_REQUEST_TYPE = "REQUEST  - Type  [{}]";
    public static final String LBL_REQUEST_SERVICE_TYPE = "REQUEST  - Type  [{}] - [{}]";
    public static final String LBL_RESPONSE_SERVICE = "{} RESPONSE - Value [{}]";
    public static final String LBL_RESPONSE = "RESPONSE - Value [{}]";
    public static final String LOG_DEFEAT_OLD_REGISTER = "DEFEAT OLD REGISTER - id = [{}]- Min active date {}";
    public static final String LOG_THIRD_REQUEST = "INVOKE {}: {URL: '{}', body : '{}'}";
    public static final String LOG_ERROR_GENERATING_AUTHORIZATION = "Error generating authorization header: {}";

    public static final String LINE = "------------------------------------------------";

    public static final String OK = "OK";
    public static final String REGISTRY_OK = "REGISTRY_OK";
    public static final String PARAMETER_OK = "PARAMETER OK";
    public static final String ERROR = "ERROR: ";
    public static final String OPERATION_PING = "PING";
    public static final String OPERATION_VALIDATE_USER_STATUS = "VALIDATE USER STATUS";
    public static final String OPERATION_CREATE_PENDING_USER = "OPERATION CREATE PENDING USER";
    public static final String OPERATION_CHANGE_FORM_STATUS = "OPERATION CHANGE FORM STATUS OF ORDINARY DEPOSIT";
    public static final String OPERATION_INACTIVE_CASE_ORDINARY_DEPOSIT = "OPERATION INACTIVE CASE ORDINARY DEPOSIT";
    public static final String VALIDATE_SUBSIDY_PERSON = "OPERATION VALIDATE SUBSIDY PERSON";
    public static final String CHANGE_HASH_SUBSIDY_PERSON = "CHANGE HASH SUBSIDY PERSON";
    public static final String GET_INFO_PERSON_WITH_SUBSIDY = "OPERATION GET INFO PERSON WITH SUBSIDY";
    public static final String INACTIVATE_SUBSIDY_PERSON = "OPERATION INACTIVATE SUBSIDY PERSON";
    public static final String VERIFY_AND_APPLY_SUBSIDY = "VERIFY AND APPLY SUBSIDY";
    public static final String UPLOAD_SUBSIDY_CASES = "OPERATION UPLOAD SUBSIDY CASES";

    public static final String LOG_JOB_REGISTER_MOVIIRED = "JOB - Finalize MOVIIRED Pending registers";
    public static final String LOG_JOB_REGISTER_MOVIIRED_FAIL = "JOB - Finalize MOVIIRED Pending registers: Fail. Cause: {}";

    public static final String LOGS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String LOG_JOB_ADO = "JOB - Validate Status ADO";
    public static final String LOG_JOB_UPDATE_SUBSIDIZED_INFORMATION = "JOB - Update subsidized information";
    public static final String LOG_FAIL_JOB_ADO = "JOB - Validate Status ADO: Fail. cause: {}";
    public static final String LOG_FAIL_USER_VALIDATE_STATUS = "Service - Validate Status: Fail. cause: {}";
    public static final String LOG_FAIL_CREATE_PENDING_USER = "Service - Create pending user: Fail. cause: {}";
    public static final String LOG_FAIL_CHANGING_STATUS_FORM = "Service - Change status ordinary deposit form: Fail. cause: {}";
    public static final String LOG_FAIL_CALL_ADO_VALIDATE_SERVICE = "JOB - Call ADO Validate: Fail. cause: {}";
    public static final String LOG_FAIL_CALL_ADO_VALIDATE_SERVICE_EXCEPTION = "JOB - Call ADO Validate: Fail. exception: {}";
    public static final String LOG_SUCCESS_RESPONSE_ADO = "JOB - Success response of ADO, Response: {}";

    public static final String LOG_FAIL_JOB_SUBSIDY = "JOB - Update information of user with subsidy: Fail. cause: {}";

    public static final String LOG_FOUND_SUBSIDY_PENDING_USERS = "Subsidy pending update information users - taken mark {} count {}";
    public static final String LOG_NOT_FOUND_SUBSIDY_PENDING_USERS = "Subsidy pending update information users not found";

    public static final String LOG_FOUND_PENDING_USERS = "Pending users: {}";
    public static final String LOG_NOT_FOUND_PENDING_USERS = "Pending users not found";

    public static final String LOG_RESPONSE_ON_API_CLEVERTAP = "Response: {}";
    public static final String LOG_ERROR_MAPPING_RESPONSE = "Error mapping response: {}";
    public static final String LOG_ERROR_ON_API_CLEVERTAP = "Error invoke CleverTap Upload Event. cause: {}";
    public static final String LOG_CLEVERTAP_START_REQUEST = "Invoke CleverTap Upload Event: {URL: '{}', Request: '{}'}";
    public static final String LOG_ADO_VALIDATE_START_REQUEST = "Invoke ADO Validation Service: {URL: '{}', User : '{}'}";
    public static final String LOG_ERROR_VALIDATING_SIGNATURE = "Error validating signature of user {}|{}: {}";
    public static final String LOG_USER_NOT_HAS_SIGNATURE = "Error user not has signature {}|{}: {}";
    public static final String LOG_USER_FAIL_CHANGE_ERROR = "Fallo al cambiar la clave del usuario. Causa: ";
    public static final String LOG_USER_FAIL_PENDING_DATA_UPDATE = "Fallo al actualizar los datos del usuario. Causa: ";
    public static final String LOG_USER_ALTERED = "Error signature altered, user {}|{}: {}";
    public static final String LOG_DEFEATING_USER = "Error defeating user {}|{}: {}";
    public static final String LOG_ERROR_UPDATING_USER = "Error updating user {}|{}: {}";
    public static final String LOG_USER_IS_ALREADY_PREVIOUSLY = "User {}|{} already changed their status previously";
    public static final String LOG_FINALIZED = "finalized";
    public static final String LOG_PROCESS_OF_TRANSACTION = "Process of transaction: {}";
    public static final String LOG_ERROR_SIGN_PENDING_USER = "Error sign pending user: {}";
    public static final String LOG_SAVING_PENDING_USER = "Saving pending user: {}";
    public static final String LOG_ERROR_CREATING_PENDING_USER_FOR_REGISTRATION = "Error creating pending user for registration: {}";
    public static final String LOG_ERROR_INACTIVATING_PENDING_USER_FOR_REGISTRATION = "Error inactivating pending user for registration: {}";
    public static final String LOG_ERROR_VALIDATING_PENDING_USER_FOR_REGISTRATION = "Error validating pending user for registration: {}";
    public static final String LOG_VALIDATING_SIGNATURE_PENDING_USER = "validating signature of pending user with id: {}";
    public static final String LOG_ERROR_VALIDATING_SIGNATURE_OF_PENDING_USER = "Error validating signature of pending user: {}|{} -> {}";
    public static final String LOG_PENDING_USER_NOT_HAS_SIGNATURE = "Pending user not has signature: {}|{} -> {}";
    public static final String LOG_PENDING_USER_IS_ALTERED = "Pending user is altered: {}|{} -> {}";
    public static final String LBL_ERROR = "{} ERROR RESPONSE - VALUE  [{}]";
    public static final String LOG_ERROR_EXECUTING_INACTIVE_USER_FOR_REGISTRATION_STEPS = "Error executing inactive user for registration steps: {}";
    public static final String LOG_FINDING_USERS_FOR_NUMBER_AND_TYPE = "Finding pending users for number {} and type {}";
    public static final String LOG_ERROR_EXECUTING_GET_INFO_PERSON_WITH_SUBSIDY_STEPS = "Error executing get info of person with subsidy steps: {}";
    public static final String LOG_ERROR_EXECUTING_GET_INFO_PERSON_WITH_SUBSIDY = "Error getting info of person with subsidy: {}";
    public static final String LOG_ERROR_VALIDATING_PENDING_USER_FOR_SUBSIDY = "Error validating pending user for subsidy: {}";
    public static final String LOG_ERROR_VERIFYING_APPLYING_SUBSIDY = "Error verifying and applying subsidy: {}";
    public static final String LOG_ERROR_EXECUTING_UPLOAD_PEOPLE_WITH_SUBSIDY_STEPS = "Error executing upload people with subsidy steps: {}";
    public static final String LOG_ERROR_UPLOADING_PEOPLE_WITH_SUBSIDY = "Error uploading people with subsidy: {}";
    public static final String READING_DOCUMENT = "READING DOCUMENT...";
    public static final String UPLOADING_SUBSIDIZED_PEOPLE = "UPLOADING SUBSIDIZED PEOPLE...";
    public static final String UPLOADING_SUBSIDIZED_PERSON = "Upload subsidized person with document number and phone hash: {}";
    public static final String SAVING_SUCCESS_SUBSIDY_PERSON = "Saving success of person with subsidy, document number and phone hash: {} - {}";
    public static final String THE_CASE_IS_INVALID = "The case \"{}\" is invalid";
    public static final String LOG_ERROR_ASKING_INFORMATION_OF_PERSON = "Error asking information information of user with document number CC - {}: -> {} : {}";
    public static final String LOG_ERROR_GENERATING_ASKING_INFORMATION_OF_PERSON = "Error generating asking information information of user with document number CC - {}: -> {}";
    public static final String LOG_FINDING_USER_IN_LISTS = "Finding user CC - {} in lists";
    public static final String LOG_ERROR_THREAD_SLEEP = "Error in thread sleep for upload document: {}";
    public static final String LOG_NOT_MATCH_EXPEDITION_DATE = "Expedition date entry ({}) not do match with expedition date of person";
    public static final String LOG_ERROR_SAVING_CASE = "Error saving case {}: {}";
    public static final String LOG_SAVING_USER_PENDING_UPDATE = "Saving user pending update: {}";
    public static final String LOG_ERROR_STATUS_USER_PENDING_FOR_UPDATE = "El usuario debe actualizar sus datos.";
    public static final String LOG_ERROR_STATUS_USER_PENDING_UPDATED = "El usuario ya ha sido actualizado previamente.";
    public static final String LOG_ERROR_NOT_FOUND_USER_PENDING_UPDATED = "Los datos del usuario no han sido encontrados en el sistema.";
    public static final String LOG_ERROR_USER_PENDING_UPDATED = "Ha ocurrido un error en el servicio, validando el número de teléfono. Causa: ";
    public static final String LOG_ERROR_USER_PENDING_UPDATE = "Falló al validar si el usuario falta por ingresar su información. Causa: ";
    public static final String LOG_SEND_DOCUMENT_TO_BLACKLIST = "Send document {} name {} to black lists";
    public static final String LOG_REQUEST_BLACKLIST_NOT_MADE = "The request to blacklists could not be made: Username not received";
    public static final String LOG_CALL_BLACKLIST_DISABLE = "The call to black list is disable";

    // Status id
    public static final Integer PENDING_STATUS_ID = 0;
    public static final Integer APPROVED_STATUS_ID = 1;
    public static final Integer DECLINED_STATUS_ID = 2;
    public static final Integer ALTERED_STATUS_ID = 3;
    // Status of registry on db
    public static final Integer ACTIVE_REGISTRY_ID = 1;
    public static final Integer DEFEATED_REGISTRY_ID = 0;
    // Ado process
    public static final Integer REGISTRATION_PROCESS = 0;
    public static final Integer ORDINARY_DEPOSIT_PROCESS = 1;
    // Status codes mapping
    public static final String STATUS_TYPE_SUCCESS = "success";
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_CODE_00 = "00";
    public static final String STATUS_TYPE_FAIL = "fails";
    public static final String BAD_REQUEST_USER_IS_NOT_IN_FORM_STATUS_CODE = "87";
    public static final String BAD_REQUEST_STATUS_IS_REQUIRED_CODE = "88";
    public static final String PROCESS_NOT_FOUND_CODE = "89";
    public static final String BAD_REQUEST_PHONE_NUMBER_CODE = "90";
    public static final String BAD_REQUEST_PHONE_SERIAL_NUMBER_CODE = "91";
    public static final String BAD_REQUEST_ADO_TRANSACTION_ID_CODE = "92";
    public static final String BAD_REQUEST_USER_CODE = "93";
    public static final String USER_HAS_A_PREVIOUS_PENDING_REGISTRY_ON_ADO_CODE = "94";
    public static final String NOT_FOUND_CODE = "404";
    public static final String REGISTER_ALTERED_CODE = "95";
    public static final String USER_PENDING_UPDATE_ALTERED_CODE = "96";
    public static final String USER_PENDING_UPDATE_STATUS_CODE = "97";
    public static final String USER_PENDING_UPDATED_STATUS_CODE = "99";
    public static final String USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM_CODE = "98";
    public static final String SERVER_ERROR_CODE = "500";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String AUTHORIZATION_HEADER_INVALID_CODE = "401";
    public static final String USER_IN_BLACK_LIST_CODE = "403";
    public static final String HASH_NOT_MATCH_CODE = "406";
    // Beans names
    public static final String SIGNATURE_HELPER = "signatureHelper";
    // Operations
    public static final String OPERATION_CREATE_PENDING_USER_FOR_REGISTRATION = "OPERATION CREATE PENDING USER FOR REGISTRATION";
    public static final String OPERATION_INACTIVE_PENDING_USER_FOR_REGISTRATION = "OPERATION INACTIVE PENDING USER FOR REGISTRATION";
    public static final String OPERATION_VALIDATE_PENDING_USER_FOR_REGISTRATION = "OPERATION VALIDATE PENDING USER FOR REGISTRATION";
    public static final String OPERATION_VALIDATE_USER_PENDING_FOR_UPDATE = "OPERATION VALIDATE USER PENDING FOR UPDATE";
    public static final String ERROR_PARAMETER_PHONE = "phoneNumber is required";
    public static final String ERROR_PARAMETER_PHONE_INVALID = "phoneNumber is invalid";
    public static final String ERROR_PARAMETER_ORIGIN_INVALID = "origin is invalid";
    // UtilsHelper
    public static final Long MILLIS_IN_ONE_SECOND = 1000L;
    public static final Integer UNKNOWN_CODE_ENUM = -1;
    public static final String STRING_LINE = "-";
    public static final String EMPTY_STRING = "";
    public static final String SEPARATOR = "|";
    public static final String MAHINDRA_DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TWO_DOTS = ":";
    public static final String TWO_DOTS_SPACE = ": ";
    public static final String REGEX_VALIDATE_PHONE_NUMBER = "3[0-9]{9}";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final Integer AUTHORIZATION_LENGTH = 15;
    public static final Integer PHONE_NUMBER_LENGTH = 10;
    public static final Integer PIN_LENGTH = 4;
    public static final String MAHINDRA_COMPONENT = "Mahindra";
    public static final String CML_COMPONENT = "Cml";
    public static final int ZERO_INT = 0;
    public static final int ONE_INT = 1;
    public static final String CHANNEL = "CHANNEL";
    public static final String SUBSCRIBER = "SUBSCRIBER";
    public static final String CRYPTO_HELPER = "cryptoHelper";
    public static final String CML_API = "cmlClient";
    public static final String AUTHORIZATION = "Authorization";
    public static final String DEFAULT_PIN = "0000";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String CC = "CC";
    public static final String PA = "PA";
    public static final String CE = "CE";
    public static final String TI = "TI";
    public static final String PEP = "PEP";
    public static final String FILE = "file";
    public static final String UTF_8 = "UTF-8";
    public static final String JUMP_LINE = "\r\n|\n";
    public static final String GENDER_MALE = "Masculino";
    public static final String BOGOTA_PLACE = "Bogota DC";
    public static final String DEFAULT_BIRTH_DAY = "01/01/1990";
    public static final String PROCESS_UPDATE_DATA_SUBSIDIZED = "UPDATE SUBSIDIZED DATA";
    public static final String BLACK_LISTS_ERROR_CODE = "BL_01";
    public static final String PERSON_NOT_FOUND_ON_REGISTRAR_CODE = "01";
    public static final String REGEX_CLEAN_NAMES = "[^a-zA-Z ]";
    public static final String SEMICOLON = ";";
    public static final String NOT_NUMBER_REGEX = "\\D";
    public static final String SHA_256_VALIDATE_REGX = ".{64}";
    public static final String PHONE_VALIDATE_REGX = "3\\d{9}";
    public static final String DOCUMENT_HASH_PLACE_HOLDER = "0000000000";
    static final String LOG_NUMBER_OF_REQUEST = "USER OWN OF REQUEST [{}]";

    private ConstantsHelper() {
        super();
    }
}

