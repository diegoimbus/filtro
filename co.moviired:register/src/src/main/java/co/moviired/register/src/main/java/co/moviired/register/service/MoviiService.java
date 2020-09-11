package co.moviired.register.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.config.ClientHandler;
import co.moviired.register.config.PropertiesHandler;
import co.moviired.register.config.RepositoryHandler;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.dto.*;
import co.moviired.register.domain.enums.ado.AdoCaseStatus;
import co.moviired.register.domain.enums.ado.AdoProcess;
import co.moviired.register.domain.enums.clevertap.Event;
import co.moviired.register.domain.enums.register.*;
import co.moviired.register.domain.factory.clevertap.ClevertapDTOHelper;
import co.moviired.register.domain.factory.clevertap.ClevertapEventHelper;
import co.moviired.register.domain.factory.mahindra.MahindraDTOHelper;
import co.moviired.register.domain.model.entity.PendingUser;
import co.moviired.register.domain.model.entity.User;
import co.moviired.register.domain.model.register.ResponseStatus;
import co.moviired.register.exceptions.ServiceException;
import co.moviired.register.helper.SignatureHelper;
import co.moviired.register.helper.UtilsHelper;
import co.moviired.register.properties.*;
import co.moviired.register.repository.IUserRepository;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static co.moviired.register.domain.enums.ado.AdoProcess.ORDINARY_DEPOSIT;
import static co.moviired.register.domain.enums.ado.AdoProcess.REGISTRATION;
import static co.moviired.register.domain.enums.register.ActiveStatus.DEFEAT;
import static co.moviired.register.domain.enums.register.RegistrationService.CREATE_PENDING_USER;
import static co.moviired.register.domain.enums.register.RegistrationService.VALIDATE_USER_STATUS;
import static co.moviired.register.domain.enums.register.ServiceStatusCode.*;
import static co.moviired.register.domain.enums.register.Status.*;
import static co.moviired.register.helper.ConstantsHelper.*;

@Slf4j
@Service
public final class MoviiService extends BaseService {

    private final IUserRepository userRepository;
    private final ReactiveConnector cleverTapClient;
    private final ReactiveConnector adoClient;
    private final SchedulersConfigurationProperties schedulersConfigurationProperties;
    private final Map<Long, Integer> uploadFilesController = new HashMap<>();
    private final ReactiveConnector mahindraClient;

    private final PropertiesHandler propertiesHandler;
    private final AdoProperties adoProperties;
    private final SubsidyProperties subsidyProperties;
    private final ClevertapProperties clevertapProperties;
    private final MahindraProperties mahindraProperties;

    private final XmlMapper xmlMapper;
    private long lastTimeDetectingJobAlive = 0;

    // SERVICE METHODS *************************************************************************************************

    public MoviiService(
            @NotNull RepositoryHandler repositoryHandler,
            @NotNull ClientHandler clientHandler,
            @NotNull PropertiesHandler pPropertiesHandler,
            @NotNull StatusCodeConfig statusCodeConfig,
            @NotNull SignatureHelper signatureHelper,
            @NotNull SchedulersConfigurationProperties pSchedulersConfigurationProperties) {

        super(pPropertiesHandler.getGlobalProperties(), statusCodeConfig, signatureHelper, repositoryHandler.getPendingUserRepository());
        this.userRepository = repositoryHandler.getUserRepository();
        this.cleverTapClient = clientHandler.getCleverTapClient();
        this.adoClient = clientHandler.getAdoClient();
        this.mahindraClient = clientHandler.getMahindraClient();
        this.schedulersConfigurationProperties = pSchedulersConfigurationProperties;
        this.propertiesHandler = pPropertiesHandler;

        this.adoProperties = propertiesHandler.getAdoProperties();
        this.subsidyProperties = propertiesHandler.getSubsidyProperties();
        this.clevertapProperties = propertiesHandler.getClevertapProperties();
        this.mahindraProperties = propertiesHandler.getMahindraProperties();

        // XmlMapper
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Test the correct upload of component
     *
     * @return response of test
     */
    public Mono<String> ping() {
        UtilsHelper.asignarCorrelativo(null);

        // Do basic consult to BD, for test connection
        String response;
        try {
            response = OK;
            this.userRepository.findById(1);
        } catch (Exception e) {
            response = ERROR + e.getMessage();
        }

        log.info(EMPTY_STRING);
        log.info(LBL_START);
        log.info(LBL_REQUEST_TYPE, OPERATION_PING);
        log.info(LBL_REQUEST, STRING_LINE);
        log.info(LBL_RESPONSE, response);
        log.info(LBL_END);
        log.info(EMPTY_STRING);

        return Mono.just(response);
    }

    /**
     * Validate status of one user on ADO
     *
     * @param phoneNumber       this is the phone number of the account of user
     * @param phoneSerialNumber this is the serial number of the device of user
     * @return response of service
     */
    public Mono<RegisterDTO> validateUserStatus(String phoneNumber, String phoneSerialNumber, AdoProcess process) {
        logsStart(OPERATION_VALIDATE_USER_STATUS, EMPTY_STRING);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUser(new User());
        registerDTO.getUser().setPhoneNumber(phoneNumber);
        registerDTO.getUser().setPhoneSerialNumber(phoneSerialNumber);
        registerDTO.setProcessNumber(process.getId());

        boolean isValidRequest = validateFields(registerDTO, VALIDATE_USER_STATUS, process, false);

        log.info(LBL_REQUEST, registerDTO);

        if (isValidRequest) {
            validateUserStatus(registerDTO, process);
        }

        logsEnd(registerDTO);
        return Mono.just(registerDTO);
    }

    private User validateUserStatus(RegisterDTO registerDTO, AdoProcess process) {
        User returnUser = null;
        try {
            Optional<User> user = this.userRepository.findFirstByPhoneNumberAndPhoneSerialNumberAndIsActiveAndStatusInAndProcessOrderByRegistrationDate(
                    registerDTO.getUser().getPhoneNumber(),
                    registerDTO.getUser().getPhoneSerialNumber(),
                    ActiveStatus.ACTIVE.getId(),
                    Arrays.asList(PENDING.getId(), APPROVED.getId()),
                    process.getId());

            StatusCode statusCode;
            if (user.isPresent()) {
                returnUser = user.get();
                if (validateSignature(user.get())) {
                    registerDTO.setUser(user.get());
                    if (registerDTO.getUser().getStatusEnum().equals(PENDING)) {
                        statusCode = this.getStatusCodeConfig().of(USER_HAS_A_PREVIOUS_PENDING_REGISTRY_ON_ADO.getStatusCode());
                    } else if
                    ((user.get().getProcess().equals(REGISTRATION_PROCESS)) || (user.get().isOrdinaryDepositFormCompleted())) {
                        statusCode = this.getStatusCodeConfig().of(SUCCESS.getStatusCode());
                    } else {
                        statusCode = this.getStatusCodeConfig().of(USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM.getStatusCode());
                    }
                } else {
                    statusCode = this.getStatusCodeConfig().of(NOT_FOUND.getStatusCode());
                }
            } else {
                statusCode = this.getStatusCodeConfig().of(NOT_FOUND.getStatusCode());
            }

            if (statusCode != null) {
                registerDTO.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            }
        } catch (Exception e) {
            registerDTO.setStatus(new ResponseStatus(this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode()).getCode(), this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode()).getMessage()));
            log.error(LOG_FAIL_USER_VALIDATE_STATUS, e.getMessage(), e);
        }
        return returnUser;
    }

    // VALIDATE SUBSIDY ************************************************************************************************

    /**
     * Create pending user on DB for validate status on ADO with the JOB
     *
     * @param registerDTO data of user to create
     * @return response of service
     */
    public Mono<RegisterDTO> createPendingUser(RegisterDTO registerDTO) {
        logsStart(OPERATION_CREATE_PENDING_USER, EMPTY_STRING);

        AdoProcess process;
        if (registerDTO.getProcessNumber() == null) {
            registerDTO.setProcessNumber(-1);
        }
        process = AdoProcess.getById(registerDTO.getProcessNumber());
        if (process == AdoProcess.UNKNOWN) {
            process = REGISTRATION;
        }

        boolean isValidRequest = validateFields(registerDTO, CREATE_PENDING_USER, process, false);

        log.info(LBL_REQUEST, registerDTO);

        if (isValidRequest) {
            // Execute the operation
            try {
                Optional<User> user = this.userRepository.findByPhoneNumberAndPhoneSerialNumberAndStatusInAndIsActiveAndProcess(
                        registerDTO.getUser().getPhoneNumber(),
                        registerDTO.getUser().getPhoneSerialNumber(),
                        Arrays.asList(PENDING.getId(), APPROVED.getId()),
                        ActiveStatus.ACTIVE.getId(),
                        process.getId());

                StatusCode statusCode;
                if (user.isPresent()) {
                    statusCode = this.getStatusCodeConfig().of(USER_HAS_A_PREVIOUS_PENDING_REGISTRY_ON_ADO.getStatusCode());
                } else {
                    saveUser(registerDTO.getUser(), process);
                    statusCode = this.getStatusCodeConfig().of(SUCCESS.getStatusCode());
                }

                if (statusCode != null) {
                    registerDTO.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
                }
            } catch (Exception | ParsingException e) {
                registerDTO.setStatus(new ResponseStatus(this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode()).getCode(), this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode()).getMessage()));
                log.error(LOG_FAIL_CREATE_PENDING_USER, e.getMessage(), e);
            }
        }

        logsEnd(registerDTO);
        return Mono.just(registerDTO);
    }

    /**
     * validate if document has a subsidy
     *
     * @param documentNumber document of person
     */
    public Mono<RegisterDTO> validateSubsidized(String authorizationHeader, DocumentType documentType, String documentNumber, String correlative) {
        logsStart(VALIDATE_SUBSIDY_PERSON, documentNumber, correlative);
        RegisterDTO registerDTO = new RegisterDTO();
        try {
            String[] authentication = UtilsHelper.getAuthorizationParts(authorizationHeader, this.getStatusCodeConfig(), this.getGlobalProperties());

            List<PendingUser> pendingUsers = this.getPendingUserRepository().getSubsidyPendingRegistration(documentType.getDocumentToUse(), Long.parseLong(documentNumber),
                    true, PendingUser.ProcessType.SUBSIDY_REGISTRATION, false);

            StatusCode sCode;
            if (!pendingUsers.isEmpty() && validateSignaturePendingUser(pendingUsers.get(0))) {
                if (validateHashSubsidy(documentType.getDocumentToUse(), documentNumber, authentication[0], pendingUsers.get(0))) {
                    registerDTO.setSubsidyUser(pendingUsers.get(0));
                    sCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
                } else {
                    sCode = this.getStatusCodeConfig().of(ServiceStatusCode.HASH_NOT_MATCH.getStatusCode());
                }
            } else {
                sCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
            }

            registerDTO.setStatus(new ResponseStatus(sCode.getCode(), sCode.getMessage()));
        } catch (ServiceException servExc) {
            handleThrowableError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_SUBSIDY, servExc, registerDTO);
        } catch (Exception exc) {
            handleExceptionError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_SUBSIDY, exc, registerDTO);
        }
        logsEnd(registerDTO);
        return Mono.just(registerDTO);
    }

    private boolean validateHashSubsidy(String documentType, String documentNumber, String phoneNumber, PendingUser pendingUser) {
        if (this.subsidyProperties.isValidateHash() && documentType.equalsIgnoreCase(DocumentType.CC.getDocumentToUse())) {
            return getSubsidyHash(documentType, documentNumber, phoneNumber).equals(pendingUser.getPhoneNumberHash());
        }
        return true;
    }

    // CHANGE HASH SUBSIDIZED ******************************************************************************************

    private String getSubsidyHash(String documentType, String pdocumentNumber, String phoneNumber) {
        if (documentType.equalsIgnoreCase(DocumentType.CC.getDocumentToUse())) {
            String documentNumber = pdocumentNumber.trim().replaceAll(NOT_NUMBER_REGEX, EMPTY_STRING);
            String finalDocument = DOCUMENT_HASH_PLACE_HOLDER.substring(0, DOCUMENT_HASH_PLACE_HOLDER.length() - pdocumentNumber.length()) + documentNumber;
            return DigestUtils.sha256Hex(finalDocument + phoneNumber.substring(phoneNumber.length() - 4));
        }
        return STRING_LINE;
    }

    public Mono<RegisterDTO> changeSubsidizedHash(RegisterDTO registerDTO) {
        logsStart(CHANGE_HASH_SUBSIDY_PERSON, new Gson().toJson(registerDTO));
        RegisterDTO response = new RegisterDTO();
        try {
            if (isInvalidRequestChangeHash(registerDTO)) {
                throw new ServiceException(this.getStatusCodeConfig().of(ServiceStatusCode.BAD_REQUEST.getStatusCode()).getMessage(),
                        ServiceStatusCode.BAD_REQUEST.getStatusCode(), this.getGlobalProperties().getName());
            }

            List<PendingUser> pendingUsers = this.getPendingUserRepository().getSubsidyPendingRegistration(DocumentType.CC.getDocumentToUse(), Long.parseLong(registerDTO.getDocumentNumber()),
                    true, PendingUser.ProcessType.SUBSIDY_REGISTRATION, false);

            StatusCode statusCode;
            if (!pendingUsers.isEmpty() && validateSignaturePendingUser(pendingUsers.get(0))) {
                if (validateHashSubsidy(DocumentType.CC.getDocumentToUse(), registerDTO.getDocumentNumber(),
                        registerDTO.getPhoneNumberOfSubsidy(), pendingUsers.get(0))) {
                    generateNewHashAndSave(DocumentType.CC.getDocumentToUse(), pendingUsers.get(0), registerDTO.getNewPhoneNumber());
                    statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
                } else {
                    statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.HASH_NOT_MATCH.getStatusCode());
                }
            } else {
                statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
            }

            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_SUBSIDY, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_SUBSIDY, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    // GET DATA SUBSIDY ************************************************************************************************

    private boolean isInvalidRequestChangeHash(RegisterDTO registerDTO) {
        return registerDTO.getDocumentNumber() == null
                || registerDTO.getDocumentNumber().trim().isEmpty()
                || registerDTO.getPhoneNumberOfSubsidy() == null
                || registerDTO.getPhoneNumberOfSubsidy().isEmpty()
                || registerDTO.getNewPhoneNumber() == null
                || registerDTO.getNewPhoneNumber().trim().isEmpty();
    }

    /**
     * get data of person with subsidy
     *
     * @param documentNumber document of person (Only CC)
     * @param subsidizedCode code for validate person
     */
    public Mono<RegisterDTO> getSubsidizedData(String authorizationHeader, String documentNumber, String subsidizedCode, MoviiredService moviiredService) {
        String correlationId = logsStart(GET_INFO_PERSON_WITH_SUBSIDY, documentNumber);
        RegisterDTO response = new RegisterDTO();
        try {
            String[] authentication = UtilsHelper.getAuthorizationParts(authorizationHeader, this.getStatusCodeConfig(), this.getGlobalProperties());

            List<PendingUser> pendingUsers = this.getPendingUserRepository().getSubsidyPendingRegistrationWithOutStatus(DocumentType.CC.getDocumentToUse(), Long.parseLong(documentNumber),
                    PendingUser.ProcessType.SUBSIDY_REGISTRATION, false);

            StatusCode statusCode;
            if (isInvalidRequestOfSubsidyInformation(pendingUsers, subsidizedCode, authentication[0], documentNumber)) {
                statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
                response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            } else if (pendingUsers.get(0).getValidationBlackList() != null && !pendingUsers.get(0).getValidationBlackList()) {
                statusCode = this.getStatusCodeConfig().of(USER_IN_BLACK_LIST.getStatusCode());
                response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            } else {
                // Request to registrar and black lists
                boolean validateBlackLists = pendingUsers.get(0).getValidationBlackList() == null;
                return callRegistrarAndBlackLists(correlationId, documentNumber, moviiredService, validateBlackLists)
                        .flatMap(registerResponse -> validateSubsidy(subsidizedCode, response, registerResponse, pendingUsers.get(0)))
                        .onErrorResume(e -> {
                            handleThrowableError(LOG_ERROR_EXECUTING_GET_INFO_PERSON_WITH_SUBSIDY_STEPS, e, response);
                            logsEnd(response);
                            return Mono.just(response);
                        });
            }
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_EXECUTING_GET_INFO_PERSON_WITH_SUBSIDY, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_EXECUTING_GET_INFO_PERSON_WITH_SUBSIDY, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    private boolean isInvalidRequestOfSubsidyInformation(List<PendingUser> pendingUsers, String subsidizedCode, String phoneNumber, String documentNumber) {
        return subsidizedCode == null
                || subsidizedCode.trim().isEmpty()
                // User not found
                || pendingUsers.isEmpty()
                // user is altered
                || !validateSignaturePendingUser(pendingUsers.get(0))
                // is inactive but not in black list
                || ((pendingUsers.get(0).getValidationBlackList() == null || pendingUsers.get(0).getValidationBlackList()) && !pendingUsers.get(0).isStatus())
                // we have all information of user but expedition date not match
                || (pendingUsers.get(0).isInfoPersonIsComplete() && !pendingUsers.get(0).getSubsidyCode().equalsIgnoreCase(subsidizedCode))
                // user phone number hash is invalid
                || !validateHashSubsidy(DocumentType.CC.getDocumentToUse(), documentNumber, phoneNumber, pendingUsers.get(0));
    }

    private Mono<RegisterResponse> callRegistrarAndBlackLists(String correlationId, String documentNumber, MoviiredService moviiredService, boolean callBlackLists) {
        return moviiredService.getUserInfo(
                Mono.just(RegisterRequest.builder().user(co.moviired.register.domain.dto.User.builder()
                        .idno(documentNumber)
                        .build())
                        .correlationId(correlationId)
                        .build()), callBlackLists, true);
    }

    // INACTIVE SUBSIDY ************************************************************************************************

    private Mono<RegisterDTO> validateSubsidy(String subsidizedCode, RegisterDTO response, RegisterResponse registerResponse, PendingUser pendingUser) {
        if (registerResponse.getCode().equals(BLACK_LISTS_ERROR_CODE)) {
            saveIsComplete(pendingUser, registerResponse, false);
            StatusCode statusCode = this.getStatusCodeConfig().of(USER_IN_BLACK_LIST.getStatusCode());
            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        } else if (!registerResponse.getCode().equals(SUCCESS_CODE_00)) {
            // Error has occurred
            response.setRegisterResponse(registerResponse.getData());
            response.setStatus(new ResponseStatus(registerResponse.getCode(), registerResponse.getMessage()));
        } else if (!registerResponse.getData().getExpeditionDate().equalsIgnoreCase(subsidizedCode)) {
            // Expedition date not match
            log.info(LOG_NOT_MATCH_EXPEDITION_DATE, subsidizedCode);
            StatusCode statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            saveIsComplete(pendingUser, registerResponse, true);
        } else {
            StatusCode status = this.getStatusCodeConfig().of(SUCCESS.getStatusCode());

            response.setRegisterResponse(registerResponse.getData());
            response.getRegisterResponse().setGender(GENDER_MALE);
            response.getRegisterResponse().setBirthPlace(BOGOTA_PLACE);

            String dateBirthDay;
            try {
                // Format
                SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");

                long expeditionTime = simpleFormat.parse(response.getRegisterResponse().getExpeditionDate()).getTime();
                Date birthDay = new Date(expeditionTime - (31557600000L * 18));
                dateBirthDay = simpleFormat.format(birthDay);
            } catch (ParseException e) {
                dateBirthDay = DEFAULT_BIRTH_DAY;
            }
            response.getRegisterResponse().setDob(dateBirthDay);
            response.setStatus(new ResponseStatus(status.getCode(), status.getMessage()));
            saveIsComplete(pendingUser, registerResponse, true);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    // APPLY SUBSIDY ***************************************************************************************************

    public Mono<RegisterDTO> inactivateSubsidizedCase(DocumentType documentType, String documentNumber) {
        logsStart(INACTIVATE_SUBSIDY_PERSON, documentNumber);
        RegisterDTO response = new RegisterDTO();
        try {
            List<PendingUser> pendingUsers = this.getPendingUserRepository().getSubsidyPendingRegistration(documentType.getDocumentToUse(),
                    Long.parseLong(documentNumber), true, PendingUser.ProcessType.SUBSIDY_REGISTRATION, false);

            if (!pendingUsers.isEmpty()) {
                inactivePendingUsers(pendingUsers);
            }

            StatusCode statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_REGISTRATION, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    public void verifyAndApplySubsidy(String correlative, String authorizationHeader, String phoneNumber, String documentType, String documentNumber) {
        try {
            logsStart(VERIFY_AND_APPLY_SUBSIDY, documentType + TWO_DOTS + documentNumber);
            validateSubsidized(authorizationHeader, DocumentType.getByDocumentToUse(documentType), documentNumber, correlative)
                    .flatMap(registerDTO -> {
                        if (registerDTO.getStatus().getCode().equals(ServiceStatusCode.SUCCESS.getStatusCode()) && registerDTO.getSubsidyUser() != null) {
                            if (registerDTO.getSubsidyUser().isSubsidyApplied() || registerDTO.getSubsidyUser().getSubsidyValue() == null) {
                                log.info("user not is valid for apply subsidy for this method");
                                return Mono.just(new MahindraDTO());
                            }
                            if (registerDTO.getSubsidyUser().isSubsidyApplied() || registerDTO.getSubsidyUser().getStatusTransaction() != null) {
                                log.info("Subsidy has applied previously");
                                return Mono.just(new MahindraDTO());
                            }

                            registerDTO.getSubsidyUser().setStatusTransaction("-1");
                            this.getPendingUserRepository().save(signPendingUser(registerDTO.getSubsidyUser()));
                            log.info("Applying transaction to user {} {} with phone number {}", documentType, documentNumber, phoneNumber);
                            return callMahindraCashIn(phoneNumber, documentType, documentNumber, registerDTO.getSubsidyUser().getSubsidyValue(), correlative)
                                    .flatMap(cashInResponse -> validateMahindraResponse(registerDTO, cashInResponse, correlative, documentType, documentNumber, phoneNumber))
                                    .onErrorResume(error -> {
                                        log.error("Error applying subsidy to user with document {} {} and phone {}: {}", documentType, documentNumber, phoneNumber, error.getMessage());
                                        return Mono.just(new MahindraDTO());
                                    });
                        } else {
                            log.info("User {} {} with phone number {} not has subsidy", documentType, documentNumber, phoneNumber);
                        }
                        return Mono.just(new MahindraDTO());
                    }).subscribe();
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_VERIFYING_APPLYING_SUBSIDY, e, new RegisterDTO());
        }
    }

    private Mono<MahindraDTO> validateMahindraResponse(RegisterDTO registerDTO, MahindraDTO cashInResponse, String correlative, String documentType, String documentNumber, String phoneNumber) {
        UtilsHelper.asignarCorrelativo(correlative);

        log.info("Saving transaction of user {} {} with phone number {}", documentType, documentNumber, phoneNumber);
        if (cashInResponse.getTxnStatus().matches(SUCCESS_CODE)) {
            registerDTO.getSubsidyUser().setSubsidyApplied(true);
        }

        registerDTO.getSubsidyUser().setPhoneNumber(phoneNumber);
        registerDTO.getSubsidyUser().setTransactionId(cashInResponse.getTxnId());
        registerDTO.getSubsidyUser().setPhoneNumberHash(getSubsidyHash(
                registerDTO.getSubsidyUser().getDocumentType(),
                String.valueOf(registerDTO.getSubsidyUser().getDocumentNumber()),
                phoneNumber
        ));
        registerDTO.getSubsidyUser().setStatusTransaction(cashInResponse.getTxnStatus());
        this.getPendingUserRepository().save(signPendingUser(registerDTO.getSubsidyUser()));
        log.info("Subsidy applying to user {} {} with phone number {} was {}", documentType, documentNumber, phoneNumber,
                registerDTO.getSubsidyUser().isSubsidyApplied() ? "Successful" : "Failed");
        return Mono.just(cashInResponse);
    }

    // UPLOAD DATA *****************************************************************************************************

    private Mono<MahindraDTO> callMahindraCashIn(String phoneNumber, String documentType, String documentNumber, BigDecimal value, String correlative) {
        try {
            return invokeMahindra(mahindraClient, this.mahindraProperties, xmlMapper,
                    MahindraDTOHelper.getCashInRequest(this.mahindraProperties, phoneNumber + new Date().getTime(), phoneNumber, documentNumber, value, null,
                            "SUBSIDY|" + documentType + SEPARATOR + documentNumber), correlative);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<RegisterDTO> uploadSubsidedCases(DocumentType documentType, FilePart filePart) {
        logsStart(UPLOAD_SUBSIDY_CASES, filePart.filename());
        RegisterDTO response = new RegisterDTO();
        try {
            return filePart.content().collectList()
                    .flatMap(this::readFile)
                    .flatMap(cases -> uploadCases(documentType.getDocumentToUse(), cases))
                    .flatMap(resp -> {
                        StatusCode statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
                        response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
                        super.logsEnd(response);
                        return Mono.just(response);
                    })
                    .onErrorResume(e -> {
                        handleThrowableError(LOG_ERROR_EXECUTING_UPLOAD_PEOPLE_WITH_SUBSIDY_STEPS, e, response);
                        logsEnd(response);
                        return Mono.just(response);
                    });
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_UPLOADING_PEOPLE_WITH_SUBSIDY, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    private Mono<String[]> readFile(List<DataBuffer> dataBuffers) {
        log.info(READING_DOCUMENT);
        StringBuilder stringBuilder = new StringBuilder();
        for (DataBuffer dataBuffer : dataBuffers) {
            try {
                stringBuilder.append(IOUtils.inputStreamAsString(dataBuffer.asInputStream(), UTF_8));
            } catch (IOException e) {
                return Mono.error(e);
            }
        }
        return Mono.just(stringBuilder.toString().split(JUMP_LINE));
    }

    private Mono<Boolean> uploadCases(String documentType, String[] cases) {
        log.info(UPLOADING_SUBSIDIZED_PEOPLE);
        long mark = new Date().getTime();
        uploadFilesController.put(mark, 0);
        List<String> casesList = new ArrayList<>(Arrays.asList(cases));

        int nextStart = 0;
        int nextTotal = this.subsidyProperties.getUploadFilesSubsidyMaxRecordsSimultaneous();
        if ((nextStart + nextTotal) >= cases.length) {
            nextTotal = cases.length - nextStart;
        }

        while (uploadFilesController.get(mark) < cases.length) {
            boolean take = uploadFileSegment(documentType, casesList, nextStart, nextTotal, mark);
            if (take) {
                nextStart = nextStart + this.subsidyProperties.getUploadFilesSubsidyMaxRecordsSimultaneous();
            }
            if (take && (nextStart + nextTotal) >= cases.length) {
                nextTotal = cases.length - nextStart;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(LOG_ERROR_THREAD_SLEEP, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        return Mono.just(true);
    }

    // UPDATE DATA *****************************************************************************************************

    private synchronized boolean uploadFileSegment(String documentType, List<String> cases, int start, int totalToTake, long mark) {
        if (start == uploadFilesController.get(mark)) {
            List<Mono<PendingUser>> requests = new ArrayList<>();
            for (int i = start; i < start + totalToTake; i++) {
                requests.add(saveCase(documentType, cases.get(i)).subscribeOn(Schedulers.elastic()));
            }

            Mono.zip(requests, Arrays::asList)
                    .flatMapIterable(objects -> objects)
                    .doOnComplete(() -> uploadFilesController.put(mark, start + totalToTake))
                    .subscribe();
            return true;
        } else {
            return false;
        }
    }

    public boolean isJobUpdateSubsidized() {
        return (System.currentTimeMillis() - lastTimeDetectingJobAlive) <= 10000;
    }

    private void markJobAliveNow() {
        lastTimeDetectingJobAlive = System.currentTimeMillis();
    }

    private void markJobDie() {
        lastTimeDetectingJobAlive = 0;
    }

    public synchronized void updateSubsidizedInformation(MoviiredService moviiredService, String correlative) {
        try {
            UtilsHelper.asignarCorrelativo(correlative);
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(LOGS_DATE_FORMAT);

            log.info(EMPTY_STRING);
            log.info(LBL_START);
            log.info(LBL_REQUEST_TYPE, LOG_JOB_UPDATE_SUBSIDIZED_INFORMATION);
            log.info(LBL_REQUEST, sdf.format(currentDate));
            markJobAliveNow();

            List<PendingUser> pendingUsers = this.getPendingUserRepository().getSubsidyPendingForFillData(true,
                    PendingUser.ProcessType.SUBSIDY_REGISTRATION, false, false,
                    String.valueOf(new Date().getTime() - schedulersConfigurationProperties.getUpdateInfoPersonTakenTimeOut()),
                    PageRequest.of(0, schedulersConfigurationProperties.getUpdateInfoPersonMaxRecordsSimultaneous()));

            if (!pendingUsers.isEmpty()) {
                markJobAliveNow();
                String takenMark = String.valueOf(new Date().getTime());
                log.info(LOG_FOUND_SUBSIDY_PENDING_USERS, takenMark, pendingUsers.size());
                updateInfoPersons(pendingUsers, moviiredService, takenMark);
            } else {
                markJobDie();
                log.info(LOG_NOT_FOUND_SUBSIDY_PENDING_USERS);
            }
        } catch (Exception e) {
            markJobDie();
            log.error(LOG_FAIL_JOB_SUBSIDY, e.getMessage(), e);
        } finally {
            log.info(LBL_END);
            log.info(EMPTY_STRING);
        }
    }

    private void updateInfoPersons(List<PendingUser> pendingUsers, MoviiredService moviiredService, String takenMark) {
        List<Mono<PendingUser>> requests = new ArrayList<>();
        for (PendingUser pendingUser : pendingUsers) {
            markJobAliveNow();
            requests.add(updateInfoInPersonTable(moviiredService, pendingUser, takenMark).subscribeOn(Schedulers.elastic()));
        }

        Mono.zip(requests, Arrays::asList)
                .flatMapIterable(objects -> objects)
                .doOnComplete(this::markJobDie)
                .subscribe();
    }

    private Mono<PendingUser> updateInfoInPersonTable(MoviiredService moviiredService, PendingUser pendingUser, String takenMark) {
        try {
            markJobAliveNow();
            log.info(LOG_FINDING_USER_IN_LISTS, pendingUser.getDocumentNumber());
            return Mono.just(pendingUser)
                    .flatMap(pendingU -> {
                        markJobAliveNow();
                        if (!validateSignaturePendingUser(pendingUser)) {
                            return Mono.just(pendingUser);
                        }
                        markAsTaken(pendingUser, takenMark);
                        return callRegistrarAndBlackLists(null, String.valueOf(pendingUser.getDocumentNumber()), moviiredService, true)
                                .flatMap(response -> {
                                    if (SUCCESS_CODE_00.equals(response.getCode())) {
                                        saveIsComplete(pendingUser, response, true);
                                    } else if (BLACK_LISTS_ERROR_CODE.equalsIgnoreCase(response.getCode())) {
                                        saveIsComplete(pendingUser, response, false);
                                    } else if (PERSON_NOT_FOUND_ON_REGISTRAR_CODE.equals(response.getCode())) {
                                        List<PendingUser> pendingUsers = new ArrayList<>();
                                        pendingUsers.add(pendingUser);
                                        inactivePendingUsers(pendingUsers);
                                    } else {
                                        log.error(LOG_ERROR_ASKING_INFORMATION_OF_PERSON, pendingUser.getDocumentNumber(), response.getCode(), response.getMessage());
                                    }
                                    return Mono.just(pendingUser);
                                });
                    })
                    .onErrorResume(e -> {
                        markJobAliveNow();
                        log.error(LOG_ERROR_GENERATING_ASKING_INFORMATION_OF_PERSON, pendingUser.getDocumentNumber(), e.getMessage());
                        return Mono.just(pendingUser);
                    });
        } catch (Exception e) {
            markJobAliveNow();
            log.error(LOG_ERROR_GENERATING_ASKING_INFORMATION_OF_PERSON, pendingUser.getDocumentNumber(), e.getMessage());
            return Mono.just(pendingUser);
        }
    }

    // DATA BASE SAVE **************************************************************************************************

    private Mono<PendingUser> saveCase(String documentType, String subside) {
        return Mono.just(subside)
                .flatMap(subsideCase -> {
                    try {
                        log.debug(UPLOADING_SUBSIDIZED_PERSON, subsideCase);

                        // Get case parts and validate parts
                        Map<String, Object> values = getValidateParts(subsideCase, documentType);

                        // Guardar el usuario
                        PendingUser pu = PendingUser.builder()
                                .documentType((String) values.get("documentType"))
                                .documentNumber((Long) values.get("documentNumber"))
                                .phoneNumber((String) values.get("phoneNumber"))
                                .phoneNumberHash((String) values.get("phoneNumberHash"))
                                .subsidyValue(BigDecimal.valueOf((double) values.get("subsidyValue")))
                                .subsidyApplied((boolean) values.get("subsidyIsApplied"))
                                .build();
                        return saveCase((boolean) values.get("isValidCase"), subsideCase, pu);

                    } catch (Exception e) {
                        log.error(LOG_ERROR_SAVING_CASE, subsideCase, e.getMessage());
                        return Mono.just(new PendingUser());
                    }
                });
    }

    private Map<String, Object> getValidateParts(String subsideCase, String documentType) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> otherValues;
        long documentNumber = 0;
        String phoneNumberHash = null;
        boolean isValidCase = true;
        String phoneNumber = null;
        double subsidyValue = 0.0;
        boolean subsidyIsApplied = false;

        if (!subsideCase.contains(SEMICOLON)) {
            isValidCase = false;
        } else {
            String[] caseParts = subsideCase.split(SEMICOLON);
            if (caseParts.length != 4) {
                isValidCase = false;
            } else {
                String docString = caseParts[0].trim().replaceAll(NOT_NUMBER_REGEX, EMPTY_STRING);
                if (UtilsHelper.isNotNumber(docString)) {
                    isValidCase = false;
                } else {
                    documentNumber = Long.parseLong(docString);
                    phoneNumberHash = caseParts[1].trim();
                }

                if (documentNumber == 0) {
                    isValidCase = false;
                }

                // getOtherValues
                otherValues = getOtherValues(caseParts, documentNumber, phoneNumberHash, isValidCase, documentType);

                // Assign other values
                phoneNumber = (String) otherValues.get("pn");
                phoneNumberHash = (String) otherValues.get("pnh");
                subsidyValue = (double) otherValues.get("sv");
                subsidyIsApplied = (boolean) otherValues.get("sia");
                isValidCase = (boolean) otherValues.get("ivc");
            }
        }

        // Generate response
        response.put("documentType", documentType);
        response.put("documentNumber", documentNumber);
        response.put("phoneNumber", phoneNumber);
        response.put("phoneNumberHash", phoneNumberHash);
        response.put("subsidyValue", subsidyValue);
        response.put("subsidyIsApplied", subsidyIsApplied);
        response.put("isValidCase", isValidCase);

        return response;
    }

    private Mono<PendingUser> saveCase(boolean isValidCase, String subsideCase, PendingUser pu) {
        if (!isValidCase) {
            log.error(THE_CASE_IS_INVALID, subsideCase);
            return Mono.just(new PendingUser());
        } else {
            return saveCase(pu.getDocumentType(), pu.getDocumentNumber(), pu.getPhoneNumberHash(), pu.getPhoneNumber(), pu.getSubsidyValue().doubleValue(), pu.isSubsidyApplied());
        }
    }

    private Map<String, Object> getOtherValues(String[] caseParts, long documentNumber, String pPhoneNumberHash, boolean pIsValidCase, String documentType) {
        String phoneNumber = null;
        double subsidyValue = 0.0;
        boolean subsidyIsApplied = false;
        String phoneNumberHash = pPhoneNumberHash;
        boolean isValidCase = pIsValidCase;
        Map<String, Object> otherValues = new HashMap<>();

        String docString = String.valueOf(documentNumber);
        if (isValidCase && documentType.equalsIgnoreCase(DocumentType.CC.getDocumentToUse()) &&
                (docString.length() < this.subsidyProperties.getMinLengthCC() || docString.length() > this.subsidyProperties.getMaxLengthCC())) {
            isValidCase = false;
        }

        if (isValidCase && !phoneNumberHash.matches(SHA_256_VALIDATE_REGX) && !phoneNumberHash.matches(PHONE_VALIDATE_REGX)) {
            isValidCase = false;
        }

        if (isValidCase && phoneNumberHash.length() == 10) {
            phoneNumber = phoneNumberHash;
            phoneNumberHash = getSubsidyHash(documentType, docString, phoneNumberHash);
        }

        if (UtilsHelper.isNotNumber(caseParts[2])) {
            isValidCase = false;
        } else {
            subsidyValue = Double.parseDouble(caseParts[2]);
            if (subsidyValue <= 0) {
                isValidCase = false;
            }
        }

        if (!caseParts[3].equals("0") && !caseParts[3].equals("1")) {
            isValidCase = false;
        } else {
            subsidyIsApplied = caseParts[3].equals("1");
        }

        // Generate response
        otherValues.put("pn", phoneNumber);
        otherValues.put("pnh", phoneNumberHash);
        otherValues.put("sv", subsidyValue);
        otherValues.put("sia", subsidyIsApplied);
        otherValues.put("ivc", isValidCase);

        return otherValues;
    }

    private Mono<PendingUser> saveCase(String documentType, long documentNumber, String phoneNumberHash, String phoneNumber, double subsidyValue, boolean subsidyIsApplied) {
        Optional<PendingUser> optionalPendingUser = this.getPendingUserRepository().findFirstByDocumentTypeAndDocumentNumberAndProcessType(documentType, documentNumber, PendingUser.ProcessType.SUBSIDY_REGISTRATION);

        boolean notIsCC = !documentType.equalsIgnoreCase(DocumentType.CC.getDocumentToUse());
        PendingUser pendingUser = PendingUser.builder()
                .registrationDate(new Date())
                .documentType(documentType)
                .documentNumber(documentNumber)
                .status(true)
                .phoneNumber(phoneNumber)
                .processType(PendingUser.ProcessType.SUBSIDY_REGISTRATION)
                .type(SUBSCRIBER)
                .altered(false)
                .infoPersonIsComplete(notIsCC)
                .phoneNumberHash(phoneNumberHash)
                .subsidyValue(BigDecimal.valueOf(subsidyValue))
                .subsidyApplied(subsidyIsApplied)
                .build();

        boolean save = false;
        if (optionalPendingUser.isPresent()) {
            PendingUser currentPendingUser = optionalPendingUser.get();
            if (!currentPendingUser.isInfoPersonIsComplete() || currentPendingUser.isAltered() || !currentPendingUser.isStatus()) {
                pendingUser.setId(currentPendingUser.getId());
                save = true;
            }
        } else {
            save = true;
        }

        if (save) {
            log.debug(LOG_SAVING_PENDING_USER, new Gson().toJson(pendingUser));
            this.getPendingUserRepository().save(signPendingUser(pendingUser));
            log.debug(SAVING_SUCCESS_SUBSIDY_PERSON, documentNumber, phoneNumberHash);
        }
        return Mono.just(pendingUser);
    }

    private void markAsTaken(PendingUser pendingUser, String takenMark) {
        pendingUser.setTaken(takenMark);
        this.getPendingUserRepository().save(pendingUser);
    }

    private void saveIsComplete(PendingUser pendingUser, RegisterResponse response, boolean isValidBlackLists) {
        pendingUser.setDateUpdate(new Date());
        pendingUser.setValidationBlackList(isValidBlackLists);
        if (!isValidBlackLists) {
            pendingUser.setStatus(false);
        } else {
            pendingUser.setInfoPersonIsComplete(true);
            pendingUser.setSubsidyCode(response.getData().getExpeditionDate().trim());
        }
        this.getPendingUserRepository().save(signPendingUser(pendingUser));
    }

    private void generateNewHashAndSave(String documentType, PendingUser pendingUser, String newPhoneNumber) {
        pendingUser.setDateUpdate(new Date());
        pendingUser.setPhoneNumberHash(getSubsidyHash(documentType, String.valueOf(pendingUser.getDocumentNumber()), newPhoneNumber));
        this.getPendingUserRepository().save(signPendingUser(pendingUser));
    }

    // VALIDATIONS *****************************************************************************************************

    private boolean validateSignature(User user) {
        try {
            this.getSignatureHelper().validateSignature(user);
            return true;
        } catch (ParsingException e) {
            log.error(LOG_ERROR_VALIDATING_SIGNATURE, user.getPhoneNumber(), user.getPhoneSerialNumber(), e.getMessage());
        } catch (DataException e) {
            if (user.getSignature().equalsIgnoreCase(STRING_LINE)) {
                log.error(LOG_USER_NOT_HAS_SIGNATURE, user.getPhoneNumber(), user.getPhoneSerialNumber(), e.getMessage());
            } else {
                log.error(LOG_USER_ALTERED, user.getPhoneNumber(), user.getPhoneSerialNumber(), e.getMessage());
                user.setStatusEnum(ALTERED);
                user.setIsActiveEnum(DEFEAT);
                this.userRepository.save(user);
            }
        }
        return false;
    }

    /**
     * Validate inputs of services
     *
     * @param registerDTO object of parameters of services
     * @param service     type of service (validate or create pending user)
     * @return say if the validation is success or not
     */
    private boolean validateFields(@NotNull RegisterDTO registerDTO, RegistrationService service, AdoProcess process, boolean isFillForm) {
        StatusCode statusCode = null;
        if (registerDTO.getUser() != null) {
            if (!isFillForm && service == CREATE_PENDING_USER && adoTransactionIdIsInvalid(registerDTO.getUser().getAdoTransactionId())) {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_ADO_TRANSACTION_ID.getStatusCode());
            }
            if (phoneSerialNumberIsInvalid(registerDTO.getUser().getPhoneSerialNumber())) {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_PHONE_SERIAL_NUMBER.getStatusCode());
            }
            if (phoneNumberIsInvalid(registerDTO.getUser().getPhoneNumber())) {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_PHONE_NUMBER.getStatusCode());
            }
            if (!isFillForm && service == CREATE_PENDING_USER && process == ORDINARY_DEPOSIT && statusIsInvalid(registerDTO.getUser().getStatus())) {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_STATUS_IS_REQUIRED.getStatusCode());
            }
        } else {
            statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_USER.getStatusCode());
        }
        if (process == AdoProcess.UNKNOWN) {
            statusCode = this.getStatusCodeConfig().of(PROCESS_NOT_FOUND.getStatusCode());
        } else {
            log.info(LOG_PROCESS_OF_TRANSACTION, process.name());
        }
        if (statusCode != null) {
            registerDTO.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        }
        return registerDTO.getStatus() == null;
    }

    private boolean adoTransactionIdIsInvalid(Integer adoTransactionId) {
        return adoTransactionId == null || adoTransactionId <= 0 || String.valueOf(adoTransactionId).length() > 11;
    }

    private boolean phoneSerialNumberIsInvalid(String phoneSerialNumber) {
        return phoneSerialNumber == null || phoneSerialNumber.trim().isEmpty() || phoneSerialNumber.length() > 50;
    }

    private boolean phoneNumberIsInvalid(String phoneNumber) {
        return phoneNumber == null || phoneNumber.trim().isEmpty() || phoneNumber.length() > 15;
    }

    private boolean statusIsInvalid(Integer status) {
        if (status == null) {
            return true;
        }
        Status statusEnum = Status.getById(status);
        return String.valueOf(status).length() > 2 || (statusEnum != PENDING && statusEnum != APPROVED);
    }

    /**
     * Validate is user is in pending state on ado DB
     *
     * @param adoDTO response of ado validations API
     * @return if user is pending return true else return false
     */
    private boolean isPendingUser(AdoDTO adoDTO) {
        return AdoCaseStatus.getById(adoDTO.getScores().get(0).getId()).isPending();
    }

    /**
     * validate if user is approval or reject for ADO process
     *
     * @param adoDTO response of ado validations API
     * @return if is true is Approval else is reject
     */
    private boolean isRejectOrApprovalUser(AdoDTO adoDTO) {
        return AdoCaseStatus.getById(adoDTO.getScores().get(0).getId()).isApproved();
    }

    /**
     * Get status number of status of ADO DB
     *
     * @param adoDTO response of ado validations API
     * @return number of status of ADO DB for user
     */
    private Integer getIdStatusAdo(AdoDTO adoDTO) {
        return AdoCaseStatus.getById(adoDTO.getScores().get(0).getId()).getId();
    }

    /**
     * Get id of this component on DB based on ado response
     *
     * @param adoDTO response of ado validations API
     * @return number of status of this component on DB for Pending, approval or Reject
     */
    private Integer getIdStatusOfService(AdoDTO adoDTO) {
        if (isPendingUser(adoDTO)) {
            return PENDING.getId();
        } else if (isRejectOrApprovalUser(adoDTO)) {
            return APPROVED.getId();
        } else {
            return DECLINED.getId();
        }
    }

    // JOB *************************************************************************************************************

    /**
     * update the state of registers pending
     */
    public synchronized void validateStatusADO() {
        try {
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(LOGS_DATE_FORMAT);

            log.info(EMPTY_STRING);
            log.info(LBL_START);
            log.info(LBL_REQUEST_TYPE, LOG_JOB_ADO);
            log.info(LBL_REQUEST, sdf.format(currentDate));

            // Execute the operation
            defeatOldUsers();

            // Validate status on ADO
            Optional<List<User>> pendingUsers = this.userRepository.findByStatusAndIsActiveAndProcessIn(
                    PENDING.getId(),
                    ActiveStatus.ACTIVE.getId(),
                    Arrays.asList(REGISTRATION.getId(), ORDINARY_DEPOSIT.getId()));

            if (pendingUsers.isPresent() && !pendingUsers.get().isEmpty()) {
                log.info(LOG_FOUND_PENDING_USERS, pendingUsers.get().size());
                validateStatusOfUserInAdo(pendingUsers.get());
            } else {
                log.info(LOG_NOT_FOUND_PENDING_USERS);
            }
        } catch (Exception e) {
            log.error(LOG_FAIL_JOB_ADO, e.getMessage(), e);
        } finally {
            log.info(LBL_END);
            log.info(EMPTY_STRING);
        }
    }

    private void defeatOldUsers() {
        // Defeat old users
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -1 * this.adoProperties.getRegistersLifeTimeMillis());
        Date minActiveDate = cal.getTime();
        List<User> usersToDefeat = this.userRepository.getCountDefeatOldRegisters(ActiveStatus.ACTIVE.getId(), minActiveDate, REGISTRATION_PROCESS);

        for (User user : usersToDefeat) {
            try {
                log.info(LOG_DEFEAT_OLD_REGISTER, user.getId(), minActiveDate);
                user.setIsActiveEnum(DEFEAT);
                user.setSignature(this.getSignatureHelper().signTransaction(user));
                this.userRepository.save(user);
            } catch (Exception | ParsingException e) {
                log.error(LOG_DEFEATING_USER, user.getPhoneNumber(), user.getPhoneSerialNumber(), e.getMessage());
            }
        }
    }

    /**
     * this method has a loop for validate status of pending user on ADO DB
     * is user is not pending on ADO send a clevertap event with number phone of user
     *
     * @param pendingUsers list of pending users
     */
    private void validateStatusOfUserInAdo(List<User> pendingUsers) {
        for (User pendingUser : pendingUsers) {
            UtilsHelper.asignarCorrelativo(null);
            if (validateSignature(pendingUser)) {
                try {
                    Optional<User> optionalUser = this.userRepository.findById(pendingUser.getId());
                    if (optionalUser.isPresent() && optionalUser.get().getStatusEnum() == PENDING) {
                        User user = optionalUser.get();
                        invokeAdoValidation(user)
                                .flatMap(adoResponse -> {
                                    onAdoResponse(adoResponse, user, AdoProcess.getById(user.getProcess()));
                                    return Mono.just(true);
                                })
                                .onErrorResume(adoError -> {
                                    log.error(LOG_FAIL_CALL_ADO_VALIDATE_SERVICE, adoError.getMessage());
                                    return Mono.just(false);
                                }).block();
                    } else {
                        log.info(LOG_USER_IS_ALREADY_PREVIOUSLY, pendingUser.getPhoneNumber(), pendingUser.getPhoneSerialNumber());
                    }
                } catch (Exception e) {
                    log.error(LOG_FAIL_CALL_ADO_VALIDATE_SERVICE_EXCEPTION, e.getMessage());
                }
            }
        }
    }

    private void onAdoResponse(AdoDTO adoResponse, User user, AdoProcess process) {
        log.info(LOG_SUCCESS_RESPONSE_ADO, adoResponse);
        if (!isPendingUser(adoResponse)) {
            boolean isApproval = isRejectOrApprovalUser(adoResponse);
            try {
                updateUser(adoResponse, user, isApproval);
            } catch (ParsingException e) {
                log.error(LOG_ERROR_UPDATING_USER, user.getPhoneNumber(), user.getPhoneSerialNumber(), e.getMessage());
            }

            Optional<User> optionalUser = this.userRepository.findById(user.getId());
            if (optionalUser.isPresent() && optionalUser.get().getStatusEnum() != PENDING) {
                invokeClevertapEvent(optionalUser.get(), isApproval, getIdStatusAdo(adoResponse), getIdStatusOfService(adoResponse), process);
            }
        }
    }

    // SAVE IN DB ******************************************************************************************************

    /**
     * Save user on request sending for client (Android, IOS) in create pending user
     *
     * @param user user to create
     */
    private void saveUser(User user, AdoProcess process) throws ParsingException {
        User saveUser = new User();
        saveUser.setPhoneNumber(user.getPhoneNumber());
        saveUser.setPhoneSerialNumber(user.getPhoneSerialNumber());
        saveUser.setAdoTransactionId(user.getAdoTransactionId());
        if (process == REGISTRATION) {
            saveUser.setStatusEnum(PENDING);
        } else {
            saveUser.setStatusEnum(Status.getById(user.getStatus()));
        }
        saveUser.setIsActiveEnum(ActiveStatus.ACTIVE);
        saveUser.setAdoStatus((Status.getById(user.getStatus()) == APPROVED && process == ORDINARY_DEPOSIT) ?
                AdoCaseStatus.SUCCESS_PROCESS.getId() : AdoCaseStatus.PENDING.getId());
        saveUser.setRegistrationDate(new Date());
        saveUser.setProcess(process.getId());
        saveUser.setOrdinaryDepositFormCompleted(false);
        saveUser.setSignature(this.getSignatureHelper().signTransaction(saveUser));
        this.userRepository.save(saveUser);
    }

    /**
     * Save update of user on DB, this is called for the JOB
     *
     * @param adoResponse response of ado validation API
     * @param pendingUser user of the consult
     * @param isApproval  say if user is approval or reject on ADO
     */
    private void updateUser(AdoDTO adoResponse, User pendingUser, boolean isApproval) throws ParsingException {
        pendingUser.setIsActiveEnum(isApproval ? ActiveStatus.ACTIVE : DEFEAT);
        pendingUser.setStatusEnum(isApproval ? APPROVED : DECLINED);
        pendingUser.setAdoStatus(getIdStatusAdo(adoResponse));
        pendingUser.setDateUpdate(new Date());

        if (isApproval) {
            pendingUser.setIdentificationTypeId(adoResponse.getCustomer().getIdentificationTypeId());
            pendingUser.setIdentificationName(adoResponse.getCustomer().getIdentificationName());
            pendingUser.setIdentificationNumber(String.valueOf(Long.parseLong(adoResponse.getCustomer().getIdentificationNumber())));
            if (adoResponse.getCustomer().getFirstName() != null) {
                pendingUser.setFirstName(adoResponse.getCustomer().getFirstName().toUpperCase());
            }
            if (adoResponse.getCustomer().getSecondName() != null) {
                pendingUser.setSecondName(adoResponse.getCustomer().getSecondName().toUpperCase());
            }
            if (adoResponse.getCustomer().getFirstSurname() != null) {
                pendingUser.setFirstSurname(adoResponse.getCustomer().getFirstSurname().toUpperCase());
            }
            if (adoResponse.getCustomer().getSecondSurname() != null) {
                pendingUser.setSecondSurname(adoResponse.getCustomer().getSecondSurname().toUpperCase());
            }
            if (adoResponse.getCustomer().getGenre() != null) {
                pendingUser.setGender(adoResponse.getCustomer().getGenre().toUpperCase());
            }
            pendingUser.setBirthDate(adoResponse.getCustomer().getBirthDate());
            pendingUser.setAdoCaseStatus(AdoCaseStatus.getById(adoResponse.getScores().get(0).getId()));
        }

        pendingUser.setSignature(this.getSignatureHelper().signTransaction(pendingUser));
        this.userRepository.save(pendingUser);
    }

    // INVOKE APIs *****************************************************************************************************

    /**
     * Send clevertap event to user
     *
     * @param user user to send event
     */
    private void invokeClevertapEvent(User user, boolean isApproval, Integer adoStatus, Integer statusId, AdoProcess process) {
        Event event;
        if (process == REGISTRATION) {
            event = isApproval ? Event.ADO_API_APPROVAL : Event.ADO_API_REJECT;
        } else {
            event = isApproval ? Event.ADO_DO_APPROVAL : Event.ADO_DO_REJECT;
        }

        ClevertapDTO clevertapDTO = ClevertapDTOHelper.getUploadEventRequest(
                ClevertapEventHelper.getClevertapEvent(
                        event,
                        user.getPhoneNumber(),
                        user.getAdoTransactionId(),
                        user.getId(),
                        adoStatus,
                        statusId,
                        this.propertiesHandler)
        );

        log.info(LOG_CLEVERTAP_START_REQUEST, this.clevertapProperties.getUploadEventUrl(), clevertapDTO);
        cleverTapClient.post(this.clevertapProperties.getUploadEventUrl(), clevertapDTO, String.class, MediaType.APPLICATION_JSON, this.clevertapProperties.getAuthenticationHeader())
                .flatMap(Mono::just)
                .onErrorResume(Mono::error)
                .doAfterTerminate(() -> log.info(LOG_FINALIZED))
                .subscribe(
                        cleverTapResponse -> log.info(LOG_RESPONSE_ON_API_CLEVERTAP, cleverTapResponse),
                        cleverTapError -> log.error(LOG_ERROR_ON_API_CLEVERTAP, cleverTapError.getMessage()));
    }

    /**
     * Request to ado the status of user on his DB
     *
     * @param pendingUser is the user to validate on ADO
     * @return response of ADO validation service
     */
    private Mono<AdoDTO> invokeAdoValidation(User pendingUser) {
        log.info(LOG_ADO_VALIDATE_START_REQUEST, this.adoProperties.getValidationUrl(pendingUser.getAdoTransactionId()), pendingUser);
        return adoClient.get(this.adoProperties.getValidationUrl(pendingUser.getAdoTransactionId()), String.class, MediaType.APPLICATION_JSON, this.adoProperties.getAuthenticationHeader())
                .flatMap(response -> {
                    try {
                        return Mono.just(new Gson().fromJson((String) response, AdoDTO.class));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(Mono::error)
                .doAfterTerminate(() -> log.info(LOG_FINALIZED));
    }

    // CHANGE STATUS FORM COMPLETED (ORDINARY DEPOSIT) *****************************************************************

    public Mono<RegisterDTO> changeStatusFormCompleted(String phoneNumber, String phoneSerialNumber) {
        RegisterDTO registerDTO = new RegisterDTO();
        boolean isValidRequest = validateRequest(OPERATION_CHANGE_FORM_STATUS, registerDTO, phoneNumber, phoneSerialNumber);

        StatusCode statusCode = null;
        if (isValidRequest) {
            User user = validateUserStatus(registerDTO, ORDINARY_DEPOSIT);
            if (registerDTO.getStatus().getCode().equals(SUCCESS.getStatusCode()) ||
                    registerDTO.getStatus().getCode().equals(USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM.getStatusCode())) {
                statusCode = saveChangeOfFormStatus(user);
            } else {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_USER_IS_NOT_IN_FORM_STATUS.getStatusCode());
            }
        }

        if (statusCode != null) {
            registerDTO.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        }

        logsEnd(registerDTO);
        return Mono.just(registerDTO);
    }

    private StatusCode saveChangeOfFormStatus(User user) {
        StatusCode statusCode;
        try {
            if (user != null) {
                user.setOrdinaryDepositFormCompleted(true);
                user.setSignature(this.getSignatureHelper().signTransaction(user));
                this.userRepository.save(user);
                statusCode = this.getStatusCodeConfig().of(SUCCESS.getStatusCode());
            } else {
                statusCode = this.getStatusCodeConfig().of(NOT_FOUND.getStatusCode());
            }
        } catch (ParsingException e) {
            log.error(LOG_FAIL_CHANGING_STATUS_FORM, e.getMessage(), e);
            statusCode = this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode());
        }
        return statusCode;
    }

    private StatusCode inactiveOrdinaryDepositCase(User user) {
        StatusCode statusCode;
        try {
            if (user != null) {
                user.setIsActiveEnum(DEFEAT);
                user.setSignature(this.getSignatureHelper().signTransaction(user));
                this.userRepository.save(user);
                statusCode = this.getStatusCodeConfig().of(SUCCESS.getStatusCode());
            } else {
                statusCode = this.getStatusCodeConfig().of(NOT_FOUND.getStatusCode());
            }
        } catch (ParsingException e) {
            log.error(LOG_FAIL_CHANGING_STATUS_FORM, e.getMessage(), e);
            statusCode = this.getStatusCodeConfig().of(SERVER_ERROR.getStatusCode());
        }
        return statusCode;
    }


    // INACTIVE CASE ***************************************************************************************************

    public Mono<RegisterDTO> inactiveCase(String phoneNumber, String phoneSerialNumber) {
        RegisterDTO registerDTO = new RegisterDTO();
        boolean isValidRequest = validateRequest(OPERATION_INACTIVE_CASE_ORDINARY_DEPOSIT, registerDTO, phoneNumber, phoneSerialNumber);

        StatusCode statusCode = null;
        if (isValidRequest) {
            User user = validateUserStatus(registerDTO, ORDINARY_DEPOSIT);
            if (registerDTO.getStatus().getCode().equals(SUCCESS.getStatusCode()) ||
                    registerDTO.getStatus().getCode().equals(USER_IS_APPROVAL_IN_ADO_BUT_PENDING_FOR_FILL_FORM.getStatusCode())) {
                statusCode = inactiveOrdinaryDepositCase(user);
            } else {
                statusCode = this.getStatusCodeConfig().of(BAD_REQUEST_USER_IS_NOT_IN_FORM_STATUS.getStatusCode());
            }
        }

        if (statusCode != null) {
            registerDTO.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
        }

        logsEnd(registerDTO);
        return Mono.just(registerDTO);
    }

    // VALIDATE CASE ***************************************************************************************************

    private boolean validateRequest(String requestType, RegisterDTO registerDTO, String phoneNumber, String phoneSerialNumber) {
        logsStart(requestType, EMPTY_STRING);

        registerDTO.setUser(new User());
        registerDTO.getUser().setPhoneNumber(phoneNumber);
        registerDTO.getUser().setPhoneSerialNumber(phoneSerialNumber);

        boolean isValidRequest = validateFields(registerDTO, VALIDATE_USER_STATUS, ORDINARY_DEPOSIT, true);

        log.info(LBL_REQUEST, registerDTO);
        return isValidRequest;
    }
}
