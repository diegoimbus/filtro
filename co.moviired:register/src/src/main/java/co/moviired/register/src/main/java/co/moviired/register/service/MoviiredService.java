package co.moviired.register.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Security;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.config.*;
import co.moviired.register.domain.dto.*;
import co.moviired.register.domain.enums.register.OperationType;
import co.moviired.register.domain.enums.register.ProviderType;
import co.moviired.register.domain.enums.register.ServiceStatusCode;
import co.moviired.register.domain.enums.register.Status;
import co.moviired.register.domain.factory.mahindra.MahindraDTOHelper;
import co.moviired.register.domain.model.entity.PendingUser;
import co.moviired.register.domain.model.entity.UserMoviired;
import co.moviired.register.domain.model.entity.UserPendingUpdate;
import co.moviired.register.domain.model.register.ResponseStatus;
import co.moviired.register.exceptions.ManagerException;
import co.moviired.register.exceptions.ParseException;
import co.moviired.register.exceptions.PendingRegisterException;
import co.moviired.register.exceptions.ServiceException;
import co.moviired.register.helper.OTPHelper;
import co.moviired.register.helper.UtilsHelper;
import co.moviired.register.properties.*;
import co.moviired.register.providers.*;
import co.moviired.register.providers.reegistraduria.RegistraduriaResponse;
import co.moviired.register.repository.IUserMoviiredRepository;
import co.moviired.register.repository.IUserPendingUpdateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static co.moviired.register.domain.enums.register.Status.*;
import static co.moviired.register.helper.ConstantsHelper.*;

@Slf4j
@Service
public final class MoviiredService extends BaseService {

    private static final String USERLOGIN_MANDATORY = "El campo <userLogin> es obligatorio.";
    private static final String USER_MANDATORY = "El campo <user> es obligatorio.";
    private static final String USER_NAME_MANDATORY = "El campo <user.firstName> es obligatorio.";
    private static final String USER_LASTNAME_MANDATORY = "El campo <user.lastName> es obligatorio.";
    private static final String USER_SHOPNAME_MANDATORY = "El campo <user.shopName> es obligatorio.";
    private static final String USER_SHOPNAME_INAVLID = "El campo <user.shopName> es invalido.";
    private static final String USER_IDNO_MANDATORY = "El campo <user.idno> es obligatorio.";
    private static final String USER_IDNO_INCORRECT = "El campo <user.idno> debe contener solo números.";
    private static final String USER_IDTYPE_MANDATORY = "El campo <user.idtype> es obligatorio.";
    private static final String USER_OTP_MANDATORY = "El campo otp es obligatorio.";
    private static final String USER_NOTIFY_CHANNEL_MANDATORY = "El campo notifyChannel es obligatorio.";
    private static final String USER_NOTIFY_CHANNEL_INCORRECT = "El campo notifyChannel debe ser SMS, EMAIL o CALL";
    private static final String USER_OTP_BAD_FORMAT_MANDATORY = "El campo otp no cumple con el formato de encriptación.";
    private static final String MSISDN_MANDATORY = "El campo <user.msisdn> es obligatorio.";
    private static final String GENDER_MANDATORY = "El campo <user.gender> es obligatorio.";
    private static final String EMAIL_MANDATORY = "El campo <user.email> es obligatorio.";
    private static final String DOB_MANDATORY = "El campo <user.dob> es obligatorio.";
    private static final String ADDRESS_MANDATORY = "El campo <user.address> es obligatorio.";
    private static final String DISTRICT_MANDATORY = "El campo <user.district> es obligatorio.";
    private static final String CITY_MANDATORY = "El campo <user.city> es obligatorio.";
    private static final String EXPEDITION_DATE_MANDATORY = "El campo <user.expeditionDate> es obligatorio.";
    private static final String BAD_FORMAT_USERLOGIN = "El campo <userLogin> no cumple con el formato de encriptación.";
    private static final String SOURCE_MANDATORY = "El campo <source> es obligatorio.";
    private static final String TYPE_SOURCE = "El campo <source> debe ser CHANNEL o SUBSCRIBER";
    private static final String PENDING_REGISTER = "PENDING_REGISTER";
    private static final String SUBSCRIBER_USER = "SUBSCRIBER_USER";
    private static final String CHANNEL_USER = "CHANNEL_USER";
    private static final String LOG_FORMATTED_4 = "{} {} {} {}";
    private static final String SEPARATOR = "***";
    private static final String PETICION = "petición recibida ";
    private static final String MOVIIRED = "MOVIIRED";
    private static final String ERROR = "ERROR";
    private static final String OK = "OK";
    private static final String LBL_INVALID_OPERATION = "Invalid operation.";

    // UserPendingUpdate
    private static final String USER_PENDING_UPDATE_MANDATORY = "El campo <userPendingUpdate> es obligatorio.";
    private static final String USER_PENDING_UPDATE_IDNO_MANDATORY = "El campo <userPendingUpdate.idno> es obligatorio.";
    private static final String USER_PENDING_UPDATE_IDNO_INCORRECT = "El campo <userPendingUpdate.idno> debe contener solo números.";
    private static final String USER_PENDING_UPDATE_SHOPNAME_MANDATORY = "El campo <userPendingUpdate.shopName> es obligatorio.";
    private static final String USER_PENDING_UPDATE_SHOPNAME_INVALID = "El campo <userPendingUpdate.shopName> es invalido.";
    private static final String USER_PENDING_UPDATE_GENDER_MANDATORY = "El campo <userPendingUpdate.gender> es obligatorio.";
    private static final String USER_PENDING_UPDATE_ADDRESS_MANDATORY = "El campo <userPendingUpdate.address> es obligatorio.";
    private static final String USER_PENDING_UPDATE_DISTRICT_MANDATORY = "El campo <userPendingUpdate.district> es obligatorio.";
    private static final String USER_PENDING_UPDATE_CITY_MANDATORY = "El campo <userPendingUpdate.city> es obligatorio.";
    private static final String USER_PENDING_UPDATE_RUT_MANDATORY = "El campo <userPendingUpdate.rut> es obligatorio.";
    private static final String USER_PENDING_UPDATE_DIGIT_VERIFICATION_MANDATORY = "El campo <userPendingUpdate.digitVerification> es obligatorio.";
    private static final String USER_PENDING_UPDATE_DIGIT_VERIFICATION_INCORRECT = "El campo <userPendingUpdate.digitVerification> debe contener solo números.";
    private static final String USER_PENDING_UPDATE_ACTIVITY_ECONOMIC_MANDATORY = "El campo <userPendingUpdate.activityEconomic> es obligatorio.";
    private static final String USER_PENDING_UPDATE_ACTIVITY_ECONOMIC_INCORRECT = "El campo <userPendingUpdate.activityEconomic> debe contener solo números.";

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;

    private final MahindraProperties mahindraProperties;
    private final BlackListProperties blackListProperties;
    private final RegistraduriaProperties registraduriaProperties;
    private final CleanAddressProperties cleanAddressProperties;
    private final CmlProperties cmlProperties;

    private final CryptoHelper cryptoHelper;
    private final OTPHelper otpHelper;

    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;

    private final ReactiveConnector mahindraClient;
    private final ReactiveConnector cmlClient;

    private final IUserMoviiredRepository userMoviiredRepository;
    private final IUserPendingUpdateRepository userPendingUpdateRepository;

    private final TermsAndConditionsService termsAndConditionsService;

    @Value("${spring.application.dummyData}")
    private String dummyData;

    public MoviiredService(
            @NotNull HelperHandler helperHandler,
            @NotNull PropertiesHandler propertiesHandler,
            @NotNull ClientHandler clientHandler,
            @NotNull RepositoryHandler repositoryHandler,
            @NotNull FactoryHandler factoryHandler,
            @NotNull ConfigHandler configHandler,
            @NotNull TermsAndConditionsService pTermsAndConditionsService

    ) throws ManagerException {
        super(propertiesHandler.getGlobalProperties(), configHandler.getStatusCodeConfig(), helperHandler.getSignatureHelper(), repositoryHandler.getPendingUserRepository());
        this.termsAndConditionsService = pTermsAndConditionsService;

        this.cryptoHelper = helperHandler.getCryptoHelper();
        this.otpHelper = helperHandler.getOtpHelper();

        this.clientFactory = factoryHandler.getClientFactory();
        this.parserFactory = factoryHandler.getParserFactory();

        this.userMoviiredRepository = repositoryHandler.getUserMoviiredRepository();
        this.userPendingUpdateRepository = repositoryHandler.getUserPendingUpdateRepository();

        this.mahindraClient = clientHandler.getMahindraClient();
        this.cmlClient = clientHandler.getCmlClient();

        this.cmlProperties = propertiesHandler.getCmlProperties();
        this.blackListProperties = propertiesHandler.getBlackListProperties();
        this.cleanAddressProperties = propertiesHandler.getCleanAddressProperties();
        this.registraduriaProperties = propertiesHandler.getRegistraduriaProperties();
        SmsProperties smsProperties = propertiesHandler.getSmsProperties();
        this.mahindraProperties = propertiesHandler.getMahindraProperties();

        // XmlMapper
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        // Object Mapper Jackson
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        // Expresiones de búsqueda del SMS de OTP
        if ((smsProperties.getQueryOtpRegExpr() == null) || (smsProperties.getQueryOtpRegExpr().isEmpty())) {
            throw new ManagerException(0, "-005", "\nERROR en la CONFIGURACIÓN DEL SERVICIO");
        }
        StringBuilder sb = new StringBuilder();
        for (String pattern : smsProperties.getQueryOtpRegExpr().values()) {
            sb.append(pattern);
            sb.append("|");
        }
        sb.deleteCharAt(sb.lastIndexOf("|"));
        sb.trimToSize();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // OTP Management
    public Mono<RegisterResponse> otpGenerate(@NotNull Mono<RegisterRequest> mrequest) {
        AtomicReference<RegisterRequest> atomicRequest = new AtomicReference<>();
        return mrequest
                .flatMap(request -> {
                    atomicRequest.set(request);
                    asignarCorrelativo(atomicRequest.get().getCorrelationId());

                    log.info("*********** GENERATE OTP ***********");

                    try {
                        log.debug(this.objectMapper.writeValueAsString(request));
                        // Validate datos de entrada
                        RegisterResponse responseValidate = validateInput(OperationType.GENERATE_OTP, request);
                        if (!responseValidate.getCode().equals(SUCCESS_CODE_00)) {
                            log.error(PETICION + responseValidate.getMessage());
                            return Mono.just(responseValidate);
                        }

                        // Establevcer el usuario
                        UserMoviired user = UserMoviired.builder()
                                .phoneNumber(cryptoHelper.decoder(request.getUserLogin()))
                                .build();

                        // Generar el OTP
                        return this.otpHelper.generateOTP(request.getSource(), user)
                                .flatMap(resp -> {
                                    asignarCorrelativo(atomicRequest.get().getCorrelationId());

                                    return Mono.just(RegisterResponse.builder()
                                            .type(OK)
                                            .code(resp.getResponseCode())
                                            .message(resp.getResponseMessage())
                                            .build()
                                    );
                                }).onErrorResume(Mono::error);

                    } catch (ParsingException | JsonProcessingException e) {
                        return Mono.error(e);
                    }

                }).flatMap(serviceResponse -> {
                    try {
                        log.info(LOG_RESPONSE_ON_API_CLEVERTAP, this.objectMapper.writer().writeValueAsString(serviceResponse));
                    } catch (JsonProcessingException e) {
                        log.error(LOG_ERROR_MAPPING_RESPONSE, e.getMessage());
                    }
                    return Mono.just(serviceResponse);
                }).onErrorResume(e -> {
                    log.error(e.getMessage(), e);

                    RegisterResponse response = new RegisterResponse();
                    response.setType(ERROR);
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                });
    }

    public Mono<RegisterResponse> otpResend(@NotNull Mono<RegisterRequest> mrequest) {
        AtomicReference<RegisterRequest> atomicRequest = new AtomicReference<>();
        return mrequest
                .flatMap(request -> {
                    atomicRequest.set(request);
                    asignarCorrelativo(atomicRequest.get().getCorrelationId());
                    log.info("*********** RESEND OTP ***********");

                    try {
                        log.debug(this.objectMapper.writeValueAsString(request));
                        // Validate datos de entrada
                        RegisterResponse responseValidate = validateInput(OperationType.RESEND_OTP, request);
                        if (!responseValidate.getCode().equals(SUCCESS_CODE_00)) {
                            log.error(PETICION + responseValidate.getMessage());
                            return Mono.just(responseValidate);
                        }

                        // Reenviar el OTP
                        return this.otpHelper.resendOTP(request.getSource(), cryptoHelper.decoder(request.getUserLogin()), request.getNotifyChannel())
                                .flatMap(resp -> {
                                    asignarCorrelativo(atomicRequest.get().getCorrelationId());

                                    return Mono.just(RegisterResponse.builder()
                                            .type(OK)
                                            .code(resp.getResponseCode())
                                            .message(resp.getResponseMessage())
                                            .build()
                                    );
                                }).onErrorResume(Mono::error);

                    } catch (ParsingException | JsonProcessingException e) {
                        return Mono.error(e);
                    }

                }).onErrorResume(e -> {
                    log.error(e.getMessage(), e);

                    RegisterResponse response = new RegisterResponse();
                    response.setType(ERROR);
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                });
    }

    public Mono<RegisterResponse> otpValidate(@NotNull Mono<RegisterRequest> mrequest) {
        AtomicReference<RegisterRequest> atomicRequest = new AtomicReference<>();
        return mrequest
                .flatMap(request -> {
                    atomicRequest.set(request);
                    asignarCorrelativo(atomicRequest.get().getCorrelationId());
                    log.info("*********** VALIDATE OTP ***********");

                    try {
                        log.debug(this.objectMapper.writeValueAsString(request));
                        // Validate datos de entrada
                        RegisterResponse responseValidate = validateInput(OperationType.VALIDATE_OTP, request);
                        if (!responseValidate.getCode().equals(SUCCESS_CODE_00)) {
                            log.error(PETICION + responseValidate.getMessage());
                            return Mono.just(responseValidate);
                        }

                        // Validar el OTP
                        return this.otpHelper.isValid(request.getSource(), cryptoHelper.decoder(request.getUserLogin()), cryptoHelper.decoder(request.getOtp()))
                                .flatMap(resp -> {
                                    asignarCorrelativo(atomicRequest.get().getCorrelationId());

                                    return Mono.just(RegisterResponse.builder()
                                            .type(OK)
                                            .code(resp.getResponseCode())
                                            .message(resp.getResponseMessage())
                                            .build()
                                    );
                                }).onErrorResume(Mono::error);

                    } catch (ParsingException | JsonProcessingException e) {
                        return Mono.error(e);
                    }

                }).onErrorResume(e -> {
                    asignarCorrelativo(atomicRequest.get().getCorrelationId());
                    log.error(e.getMessage(), e);

                    RegisterResponse response = new RegisterResponse();
                    response.setType(ERROR);
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                });
    }

    // User info validation
    public Mono<RegisterResponse> validateMHUser(@NotNull RegisterRequest request, OperationType opType) throws ParseException, JsonProcessingException {
        log.info(SEPARATOR);
        AtomicReference<RegisterResponse> responseUserQueryInfoChannelAtomicReference = new AtomicReference<>();

        //Validate PENDING STATUS REGISTER
        RegisterResponse registerResponse = isPendingRegister(request.getUserLogin());
        if (null != registerResponse) {
            log.info("*********** USER STATUS IS: {} ***********", Status.PENDING.toString());
            log.info(LOG_VERIFY_USER_STATUS_FINISHED);
            return Mono.just(registerResponse);
        }

        ProviderType providerType = ProviderType.MAHINDRA;
        ReactiveConnector client = this.clientFactory.getClient(providerType);
        IParser parser = this.parserFactory.getParser(opType);
        IRequest mhRequestChannel = parser.parseRequest(request, CHANNEL); //set UserType CHANNEL for query user in MAHINDRA

        log.info("*********** QUERYING IN MAHINDRA. USERTYPE = {} ***********", CHANNEL);
        log.info(this.xmlMapper.writeValueAsString(mhRequestChannel).toUpperCase());

        // Query USERQUERYINFO to Mahindra with USERTYPE = CHANNEL
        return client.exchange(
                this.clientFactory.getHttpMethod(providerType, opType),
                this.clientFactory.getUrl(providerType),
                this.clientFactory.getRequestString(providerType, mhRequestChannel),
                String.class,
                this.clientFactory.getMediaType(providerType, opType),
                null
        ).flatMap(respChannel -> {

            //Obtain response Mahindra USERQUERYINFO with USERTYPE = CHANNEL
            RegisterResponse resp;
            Mono<RegisterResponse> finalResp;

            try {
                IResponse mhResponse = this.clientFactory.readValue(OperationType.USER_QUERY_INFO, (String) respChannel);

                log.info("*********** RESPONSE MAHINDRA. USERTYPE = {} ***********", CHANNEL);
                log.info(this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());

                // Transformar response especifica a Response
                resp = parser.parseResponse(mhResponse);
                responseUserQueryInfoChannelAtomicReference.set(resp);

                finalResp = Mono.just(resp);
                log.info("*********** QUERYING IN MAHINDRA. USERTYPE = {} - FINISHED ***********", CHANNEL);
                log.info(SEPARATOR);
            } catch (ParseException | IOException e) {
                return Mono.error(e);
            }

            return finalResp;
        }).flatMap(respChannel -> { //Query USERQUERYINFO to Mahindra with USERTYPE = SUBSCRIBER
            try {
                request.setSource(SUBSCRIBER); //set UserType SUBSCRIBER for query user in MAHINDRA
                IRequest mhRequestSubscriber = parser.parseRequest(request, SUBSCRIBER);

                log.info("*********** QUERYING IN MAHINDRA. USERTYPE = {} ***********", SUBSCRIBER);
                log.info(this.xmlMapper.writeValueAsString(mhRequestSubscriber).toUpperCase());

                return client.exchange(this.clientFactory.getHttpMethod(providerType, opType),
                        this.clientFactory.getUrl(providerType),
                        this.clientFactory.getRequestString(providerType, mhRequestSubscriber),
                        String.class,
                        this.clientFactory.getMediaType(providerType, opType),
                        null);

            } catch (Exception e) {
                return Mono.error(e);
            }

        }).flatMap(respSubscriber -> {
            //Obtain response Mahindra USERQUERYINFO with USERTYPE = SUBSCRIBER
            RegisterResponse responseUserQueryInfoChannel = responseUserQueryInfoChannelAtomicReference.get();
            RegisterResponse responseUserQueryInfoSubscriber;

            try {
                IResponse mhResponse = this.clientFactory.readValue(OperationType.USER_QUERY_INFO, (String) respSubscriber);
                responseUserQueryInfoSubscriber = parser.parseResponse(mhResponse);

                log.info("*********** RESPONSE MAHINDRA. USERTYPE = {} ***********", SUBSCRIBER);
                log.info(this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());
                log.info("*********** QUERYING IN MAHINDRA. USERTYPE = {} - FINISHED ***********", SUBSCRIBER);

                //Validar USERQUERYINFO
                if (responseUserQueryInfoChannel.getCode().equals(StatusCode.Level.SUCCESS.value())) {
                    RegisterResponse response = new RegisterResponse();
                    StatusCode sc = this.getStatusCodeConfig().of(CHANNEL_USER);
                    response.setCode(sc.getCode());
                    response.setType(OK);
                    response.setMessage(sc.getMessage());
                    log.info(SEPARATOR);
                    log.info(LOG_VERIFY_USER_STATUS_FINISHED);
                    return Mono.just(response);
                }

                if (responseUserQueryInfoSubscriber.getCode().equals(StatusCode.Level.SUCCESS.value())) {
                    RegisterResponse response = new RegisterResponse();
                    StatusCode sc = this.getStatusCodeConfig().of(SUBSCRIBER_USER);
                    response.setCode(sc.getCode());
                    response.setType(ErrorType.DATA.name());
                    response.setMessage(sc.getMessage());
                    log.info(SEPARATOR);
                    log.info(LOG_VERIFY_USER_STATUS_FINISHED);
                    return Mono.just(response);
                }

            } catch (ParseException | IOException e) {
                return Mono.error(e);
            }

            RegisterResponse response = new RegisterResponse();
            response.setCode(StatusCode.Level.SUCCESS.value());
            response.setMessage("El Usuario no se encuentra en Moviired ni en Movii.");
            response.setType(OK);
            log.info(SEPARATOR);
            log.info(LOG_VERIFY_USER_STATUS_FINISHED);
            return Mono.just(response);

        }).flatMap(serviceResponse -> {
            try {
                log.info(LOG_RESPONSE_ON_API_CLEVERTAP, this.objectMapper.writer().writeValueAsString(serviceResponse));
            } catch (JsonProcessingException e) {
                log.error(LOG_ERROR_MAPPING_RESPONSE, e.getMessage());
            }
            return Mono.just(serviceResponse);
        }).onErrorResume(e -> {
            RegisterResponse response = new RegisterResponse();
            response.setCode("99");
            response.setMessage(e.getMessage());
            response.setType(ERROR);
            log.info("ERROR VERYFING USER IN MH. ERROR= {}", e.getMessage());
            return Mono.just(response);
        });
    }

    private RegisterResponse isPendingRegister(String userLogin) {
        //Validate Status User Register in BD
        log.info("*********** QUERYING REGISTER STATUS IN BD. USER: {} ***********", userLogin);
        Optional<UserMoviired> userMoviired = this.userMoviiredRepository.findByPhoneNumberAndStatus(
                userLogin, Status.PENDING);

        if (userMoviired.isPresent()) {
            if (validateSignature(userMoviired.get())) {
                //Validar si el registro se encuentra y está en estado PENDIENTE
                try {

                    if (userMoviired.get().getStatus().equals(PENDING)) {
                        throw new PendingRegisterException("Registro en estado PENDIENTE");
                    }
                } catch (PendingRegisterException p) {
                    RegisterResponse response = new RegisterResponse();
                    StatusCode sc = this.getStatusCodeConfig().of(PENDING_REGISTER);
                    response.setCode(sc.getCode());
                    response.setType(ErrorType.DATA.name());
                    response.setMessage(sc.getMessage());
                    return response;
                }
            } else {
                RegisterResponse response = new RegisterResponse();
                response.setCode(StatusCode.Level.FAIL.value());
                response.setMessage(ERROR);
                return response;
            }
        }
        log.info("*********** USER REGISTER IN BD IS NULL. USER: {} ***********", userLogin);
        log.info(SEPARATOR);
        return null;
    }

    public Mono<RegisterResponse> getUserInfo(@NotNull Mono<RegisterRequest> mrequest, boolean callBlackLists, boolean generateErrorOnFailBlackLists) {

        AtomicReference<RegisterRequest> registerRequestAtomicReference = new AtomicReference<>();
        AtomicReference<RegisterResponse> serviceResponse = new AtomicReference<>();
        return mrequest
                .flatMap(request -> {
                    request.setCorrelationId(asignarCorrelativo(request.getCorrelationId()));
                    try {
                        registerRequestAtomicReference.set(request);
                        log.info("*********** GET STARTED USER INFO ***********");

                        // Input Mask
                        log.info(Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), "pin", "mpin"));

                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                    return Mono.just(request);
                })
                .flatMap(req -> {
                    // INVOKE REGISTRADURIA
                    RegisterRequest request = registerRequestAtomicReference.get();
                    asignarCorrelativo(request.getCorrelationId());
                    return Mono.just(searchRegistraduria(request));
                })
                .flatMap(req -> {
                    serviceResponse.set(req);
                    RegisterRequest request = registerRequestAtomicReference.get();
                    asignarCorrelativo(request.getCorrelationId());

                    return searchInBlackList(callBlackLists, request, req);
                }).flatMap(respBlackList -> {
                    // Validate black lists response
                    if (BLACK_LISTS_ERROR_CODE.equalsIgnoreCase(respBlackList.getCode())
                            || (!SUCCESS_CODE_00.equalsIgnoreCase(respBlackList.getCode())
                            && generateErrorOnFailBlackLists)) {
                        return Mono.error(new DataException(respBlackList.getCode(), respBlackList.getMessage()));
                    }

                    // Map registraduria response
                    try {
                        log.info(LOG_RESPONSE_ON_API_CLEVERTAP, this.objectMapper.writer().writeValueAsString(serviceResponse.get()));
                    } catch (JsonProcessingException e) {
                        log.error(LOG_ERROR_MAPPING_RESPONSE, e.getMessage());
                    }
                    return Mono.just(serviceResponse.get());
                }).onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    RegisterResponse registerResponse = new RegisterResponse();
                    if (e instanceof DataException) {
                        registerResponse.setType(ERROR);
                        registerResponse.setCode(((DataException) e).getCode());
                        registerResponse.setMessage(e.getMessage());
                        return Mono.just(registerResponse);
                    }
                    registerResponse.setType(ERROR);
                    registerResponse.setCode("99");
                    registerResponse.setMessage(e.getMessage());
                    return Mono.just(registerResponse);
                });
    }

    private Mono<RegisterResponse> searchInBlackList(boolean callBlackLists, RegisterRequest request, RegisterResponse req) {
        if (callBlackLists && this.blackListProperties.isEnable()) {
            // Set firstName
            setFirstName(request, req);
            request.getUser().setFirstName(UtilsHelper.cleanNameForBlackList(request.getUser().getFirstName()).trim().replaceAll(this.cleanAddressProperties.getCleanMultipleSpacesRegex(), " "));
            log.info(LOG_SEND_DOCUMENT_TO_BLACKLIST, request.getUser().getIdno(), request.getUser().getFirstName());

            // Search in Blacklist
            if (!request.getUser().getFirstName().trim().isEmpty()) {
                return Mono.just(searchBlackList(request));
            } else {
                log.error(LOG_REQUEST_BLACKLIST_NOT_MADE);
                return Mono.just(RegisterResponse.builder().code("99").message(LOG_REQUEST_BLACKLIST_NOT_MADE).build());
            }
        } else {
            return Mono.just(RegisterResponse.builder().code(SUCCESS_CODE_00).message(LOG_CALL_BLACKLIST_DISABLE).build());
        }
    }

    private void setFirstName(RegisterRequest request, RegisterResponse req) {
        if (request.getUser().getFirstName() == null) {
            String name = "";
            if (req.getData().getFirstName() != null) {
                name += req.getData().getFirstName() + " ";
            }
            if (req.getData().getSecondName() != null) {
                name += req.getData().getSecondName() + " ";
            }
            if (req.getData().getFirstSurname() != null) {
                name += req.getData().getFirstSurname() + " ";
            }
            if (req.getData().getSecondSurname() != null) {
                name += req.getData().getSecondSurname();
            }
            request.getUser().setFirstName(name);
        }
    }

    public Mono<RegisterResponse> registry(@NotNull Mono<RegisterRequest> mrequest) {
        AtomicReference<RegisterRequest> registerRequestAtomicReference = new AtomicReference<>();
        return mrequest
                // Decode request
                .flatMap(request -> {
                    request.setCorrelationId(asignarCorrelativo(request.getCorrelationId()));
                    try {
                        // Deseencriptar registerRequest
                        decodeRegisterRequestMethod(request);

                        registerRequestAtomicReference.set(request);
                        log.info("*********** REGISTRY USER MERCHANT: STARTED ***********");

                        // Input Mask
                        log.info(Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), "pin", "mpin"));
                    } catch (JsonProcessingException | ParsingException e) {
                        return Mono.error(e);
                    }
                    return Mono.just(request);
                })
                // Validate registraduria
                .flatMap(this::validateRegistraduriaRegistry)
                // Validate data
                .flatMap(request -> {
                    // Put DummyData to request (shopName, gender, address, district, city)
                    putDummyData(request);

                    // Validate data
                    return validateInputRegistry(request);
                })
                // Terms and conditions
                .flatMap(request -> {
                    asignarCorrelativo(registerRequestAtomicReference.get().getCorrelationId());
                    return termsAndConditionsService.registrarTermsAndConditions(registerRequestAtomicReference.get().getUser().getIdno(), true, false, false);
                })
                // Send data to mahindra (sign up merchant)
                .flatMap(respTermCond -> {
                    try {
                        if (!respTermCond.getCode().equalsIgnoreCase(SUCCESS_CODE_00)) {
                            throw new DataException("99", "No se pudo registrar términos y condiciones");
                        }

                        RegisterRequest request = registerRequestAtomicReference.get();
                        asignarCorrelativo(request.getCorrelationId());

                        return sendDataMahindra(request);
                    } catch (DataException e) {
                        return Mono.error(e);
                    }
                })
                // Handle Sign up response
                .flatMap(resp -> {
                    asignarCorrelativo(registerRequestAtomicReference.get().getCorrelationId());

                    // Transform client response
                    return transformClientResponse(resp);
                })
                // Handle response
                .flatMap(respMh -> {
                    asignarCorrelativo(registerRequestAtomicReference.get().getCorrelationId());

                    if (!respMh.getCode().equalsIgnoreCase(SUCCESS_CODE_00)) {
                        return Mono.error(new DataException(respMh.getCode(), respMh.getMessage()));
                    }

                    return this.saveUser(Mono.just(registerRequestAtomicReference.get()));
                }).flatMap(serviceResponse -> {
                    try {
                        log.info(LOG_RESPONSE_ON_API_CLEVERTAP, this.objectMapper.writer().writeValueAsString(serviceResponse));
                    } catch (JsonProcessingException e) {
                        log.error(LOG_ERROR_MAPPING_RESPONSE, e.getMessage());
                    }
                    return Mono.just(serviceResponse);
                })
                // On Error
                .onErrorResume(error -> {
                    asignarCorrelativo(registerRequestAtomicReference.get().getCorrelationId());

                    RegisterResponse registerResponse = new RegisterResponse();
                    if (error instanceof DataException) {
                        registerResponse.setType(ERROR);
                        registerResponse.setCode(((DataException) error).getCode());
                        registerResponse.setMessage(error.getMessage());
                        return Mono.just(registerResponse);
                    }

                    if (error instanceof ManagerException) {
                        registerResponse.setType(ERROR);
                        registerResponse.setCode(((ManagerException) error).getCodigo());
                        registerResponse.setMessage(error.getMessage());
                        return Mono.just(registerResponse);
                    }

                    registerResponse.setType(ERROR);
                    registerResponse.setCode("99");
                    registerResponse.setMessage(error.getMessage());
                    return Mono.just(registerResponse);
                }).doAfterTerminate(() -> log.info("***********  REGISTRY USER MERCHANT:ENDED ***********"));
    }

    private Mono<RegisterRequest> validateRegistraduriaRegistry(RegisterRequest request) {
        RegisterResponse registrarResponse = validateRegistraduria(request);
        if (!registrarResponse.getCode().equals(SUCCESS_CODE_00)) {
            log.error(LOG_FORMATTED_4, ERROR, MOVIIRED, registrarResponse.getCode(), registrarResponse.getMessage());
            return Mono.error(new DataException(registrarResponse.getCode(), registrarResponse.getMessage()));
        }

        return Mono.just(request);
    }

    private Mono<RegisterRequest> validateInputRegistry(RegisterRequest request) {
        RegisterResponse responseValidate = validateInput(OperationType.REGISTRY_MERCHANT, request);
        if (!responseValidate.getCode().equals(SUCCESS_CODE_00)) {
            log.error(PETICION + responseValidate.getMessage());
            return Mono.error(new DataException(responseValidate.getCode(), responseValidate.getMessage()));
        }

        return Mono.just(request);
    }

    private Mono<Object> sendDataMahindra(RegisterRequest request) {
        try {
            ProviderType providerType = ProviderType.MAHINDRA;

            ReactiveConnector client = this.clientFactory.getClient(providerType);
            IParser parser = this.parserFactory.getParser(OperationType.REGISTRY_MERCHANT);
            IRequest mhRequestChannel = parser.parseRequest(request);
            log.info(LOG_FORMATTED_4, "==>", "Request ", ":  ", this.xmlMapper.writeValueAsString(mhRequestChannel));

            return client.exchange(this.clientFactory.getHttpMethod(providerType, OperationType.REGISTRY_MERCHANT),
                    this.clientFactory.getUrl(providerType),
                    this.clientFactory.getRequestString(providerType, mhRequestChannel),
                    String.class,
                    this.clientFactory.getMediaType(providerType, OperationType.REGISTRY_MERCHANT),
                    null);
        } catch (ParseException | JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private Mono<RegisterResponse> transformClientResponse(Object resp) {
        try {
            IParser parser = parserFactory.getParser(OperationType.REGISTRY_MERCHANT);

            // Transformar XML response a  response
            String mhr = (String) resp;
            IResponse mhResponse = this.clientFactory.readValue(OperationType.REGISTRY_MERCHANT, mhr);
            log.info(LOG_FORMATTED_4, "<==", "Response ", ":  ", this.xmlMapper.writeValueAsString(mhResponse));

            RegisterResponse response = parser.parseResponse(mhResponse);
            log.info(this.objectMapper.writer().writeValueAsString(response));

            return Mono.just(response);

        } catch (IOException | ParseException e) {
            return Mono.error(e);
        }
    }

    private void decodeRegisterRequestMethod(RegisterRequest request) throws ParsingException {
        // Decode registerRequest
        decodeRegisterRequest(request);

        // Validate if exist user object
        if (!validateNullOrEmpty(request.getUser(), true, "", false)) {
            decodeRegisterRequestUser(request);
        }

        // Validate if exist userMoviiredDTO object
        if (!validateNullOrEmpty(request.getUserMoviiredDTO(), true, "", false)) {
            decodeRegisterRequestUserMoviiredDTO(request);
        }
    }

    private void decodeRegisterRequest(RegisterRequest request) throws ParsingException {
        // userLogin
        if (!validateNullOrEmpty(null, false, request.getUserLogin(), true)) {
            request.setUserLogin(cryptoHelper.decoder(request.getUserLogin()));
        }

        // email
        if (!validateNullOrEmpty(null, false, request.getEmail(), true)) {
            request.setEmail(cryptoHelper.decoder(request.getEmail()));
        }

        // otp
        if (!validateNullOrEmpty(null, false, request.getOtp(), true)) {
            request.setOtp(cryptoHelper.decoder(request.getOtp()));
        }

        // imei
        if (!validateNullOrEmpty(null, false, request.getImei(), true)) {
            request.setImei(cryptoHelper.decoder(request.getImei()));
        }

        // ip
        if (!validateNullOrEmpty(null, false, request.getIp(), true)) {
            request.setIp(cryptoHelper.decoder(request.getIp()));
        }

        // source
        if (!validateNullOrEmpty(null, false, request.getSource(), true)) {
            request.setSource(cryptoHelper.decoder(request.getSource()));
        }

        // operatingSystem
        if (!validateNullOrEmpty(null, false, request.getOperatingSystem(), true)) {
            request.setOperatingSystem(cryptoHelper.decoder(request.getOperatingSystem()));
        }

        // channel
        if (!validateNullOrEmpty(null, false, request.getChannel(), true)) {
            request.setChannel(cryptoHelper.decoder(request.getChannel()));
        }

        // browser
        if (!validateNullOrEmpty(null, false, request.getBrowser(), true)) {
            request.setBrowser(cryptoHelper.decoder(request.getBrowser()));
        }

        // version
        if (!validateNullOrEmpty(null, false, request.getVersion(), true)) {
            request.setVersion(cryptoHelper.decoder(request.getVersion()));
        }

        // issuerDate
        if (!validateNullOrEmpty(null, false, request.getIssuerDate(), true)) {
            request.setIssuerDate(cryptoHelper.decoder(request.getIssuerDate()));
        }

        // notifyChannel
        if (!validateNullOrEmpty(null, false, request.getNotifyChannel(), true)) {
            request.setNotifyChannel(cryptoHelper.decoder(request.getNotifyChannel()));
        }
    }

    private void decodeRegisterRequestUser(RegisterRequest request) throws ParsingException {
        // firstName
        if (!validateNullOrEmpty(null, false, request.getUser().getFirstName(), true)) {
            request.getUser().setFirstName(cryptoHelper.decoder(request.getUser().getFirstName()));
        }

        // lastName
        if (!validateNullOrEmpty(null, false, request.getUser().getLastName(), true)) {
            request.getUser().setLastName(cryptoHelper.decoder(request.getUser().getLastName()));
        }

        // idno
        if (!validateNullOrEmpty(null, false, request.getUser().getIdno(), true)) {
            request.getUser().setIdno(cryptoHelper.decoder(request.getUser().getIdno()));
        }

        // gender
        if (!validateNullOrEmpty(null, false, request.getUser().getGender(), true)) {
            request.getUser().setGender(cryptoHelper.decoder(request.getUser().getGender()));
        }

        // msisdn
        if (!validateNullOrEmpty(null, false, request.getUser().getMsisdn(), true)) {
            request.getUser().setMsisdn(cryptoHelper.decoder(request.getUser().getMsisdn()));
        }

        // userType
        if (!validateNullOrEmpty(null, false, request.getUser().getUserType(), true)) {
            request.getUser().setUserType(cryptoHelper.decoder(request.getUser().getUserType()));
        }

        // agentCode
        if (!validateNullOrEmpty(null, false, request.getUser().getAgentCode(), true)) {
            request.getUser().setAgentCode(cryptoHelper.decoder(request.getUser().getAgentCode()));
        }

        // idtype
        if (!validateNullOrEmpty(null, false, request.getUser().getIdtype(), true)) {
            request.getUser().setIdtype(cryptoHelper.decoder(request.getUser().getIdtype()));
        }

        // dob
        if (!validateNullOrEmpty(null, false, request.getUser().getDob(), true)) {
            request.getUser().setDob(cryptoHelper.decoder(request.getUser().getDob()));
        }

        // email
        if (!validateNullOrEmpty(null, false, request.getUser().getEmail(), true)) {
            request.getUser().setEmail(cryptoHelper.decoder(request.getUser().getEmail()));
        }

        // cellphone
        if (!validateNullOrEmpty(null, false, request.getUser().getCellphone(), true)) {
            request.getUser().setCellphone(cryptoHelper.decoder(request.getUser().getCellphone()));
        }

        // userId
        if (!validateNullOrEmpty(null, false, request.getUser().getUserId(), true)) {
            request.getUser().setUserId(cryptoHelper.decoder(request.getUser().getUserId()));
        }

        // rut
        if (!validateNullOrEmpty(null, false, request.getUser().getRut(), true)) {
            request.getUser().setRut(cryptoHelper.decoder(request.getUser().getRut()));
        }

        // digitVerification
        if (!validateNullOrEmpty(null, false, request.getUser().getDigitVerification(), true)) {
            request.getUser().setDigitVerification(cryptoHelper.decoder(request.getUser().getDigitVerification()));
        }

        // activityEconomic
        if (!validateNullOrEmpty(null, false, request.getUser().getActivityEconomic(), true)) {
            request.getUser().setActivityEconomic(cryptoHelper.decoder(request.getUser().getActivityEconomic()));
        }

        decodeRegisterRequestUserExtraInfo(request);
    }

    private void decodeRegisterRequestUserExtraInfo(RegisterRequest request) throws ParsingException {
        // shopName
        if (!validateNullOrEmpty(null, false, request.getUser().getShopName(), true)) {
            request.getUser().setShopName(cryptoHelper.decoder(request.getUser().getShopName()));
        }

        // address
        if (!validateNullOrEmpty(null, false, request.getUser().getAddress(), true)) {
            request.getUser().setAddress(cryptoHelper.decoder(request.getUser().getAddress()));
        }

        // district
        if (!validateNullOrEmpty(null, false, request.getUser().getDistrict(), true)) {
            request.getUser().setDistrict(cryptoHelper.decoder(request.getUser().getDistrict()));
        }

        // city
        if (!validateNullOrEmpty(null, false, request.getUser().getCity(), true)) {
            request.getUser().setCity(cryptoHelper.decoder(request.getUser().getCity()));
        }

        // mahindraUser
        if (!validateNullOrEmpty(null, false, request.getUser().getMahindraUser(), true)) {
            request.getUser().setMahindraUser(cryptoHelper.decoder(request.getUser().getMahindraUser()));
        }

        // mahindraPassword
        if (!validateNullOrEmpty(null, false, request.getUser().getMahindraPassword(), true)) {
            request.getUser().setMahindraPassword(cryptoHelper.decoder(request.getUser().getMahindraPassword()));
        }

        // status
        if (!validateNullOrEmpty(null, false, request.getUser().getStatus(), true)) {
            request.getUser().setStatus(cryptoHelper.decoder(request.getUser().getStatus()));
        }

        // sign
        if (!validateNullOrEmpty(null, false, request.getUser().getSign(), true)) {
            request.getUser().setSign(cryptoHelper.decoder(request.getUser().getSign()));
        }

        // changePasswordRequired
        if (!validateNullOrEmpty(null, false, request.getUser().getChangePasswordRequired(), true)) {
            request.getUser().setChangePasswordRequired(cryptoHelper.decoder(request.getUser().getChangePasswordRequired()));
        }

        // grade
        if (!validateNullOrEmpty(null, false, request.getUser().getGrade(), true)) {
            request.getUser().setGrade(cryptoHelper.decoder(request.getUser().getGrade()));
        }

        // tcp
        if (!validateNullOrEmpty(null, false, request.getUser().getTcp(), true)) {
            request.getUser().setTcp(cryptoHelper.decoder(request.getUser().getTcp()));
        }

        // walletNumber
        if (!validateNullOrEmpty(null, false, request.getUser().getWalletNumber(), true)) {
            request.getUser().setWalletNumber(cryptoHelper.decoder(request.getUser().getWalletNumber()));
        }

        // lastLogin
        if (!validateNullOrEmpty(null, false, request.getUser().getLastLogin(), true)) {
            request.getUser().setLastLogin(cryptoHelper.decoder(request.getUser().getLastLogin()));
        }

        // expeditionDate
        if (!validateNullOrEmpty(null, false, request.getUser().getExpeditionDate(), true)) {
            request.getUser().setExpeditionDate(cryptoHelper.decoder(request.getUser().getExpeditionDate()));
        }
    }

    private void decodeRegisterRequestUserMoviiredDTO(RegisterRequest request) throws ParsingException {
        // firstName
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getFirstName(), true)) {
            request.getUserMoviiredDTO().setFirstName(cryptoHelper.decoder(request.getUserMoviiredDTO().getFirstName()));
        }

        // lastName
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getLastName(), true)) {
            request.getUserMoviiredDTO().setLastName(cryptoHelper.decoder(request.getUserMoviiredDTO().getLastName()));
        }

        // msisdn
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getMsisdn(), true)) {
            request.getUserMoviiredDTO().setMsisdn(cryptoHelper.decoder(request.getUserMoviiredDTO().getMsisdn()));
        }

        // userType
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getUserType(), true)) {
            request.getUserMoviiredDTO().setUserType(cryptoHelper.decoder(request.getUserMoviiredDTO().getUserType()));
        }

        // idtype
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getIdtype(), true)) {
            request.getUserMoviiredDTO().setIdtype(cryptoHelper.decoder(request.getUserMoviiredDTO().getIdtype()));
        }

        // idno
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getIdno(), true)) {
            request.getUserMoviiredDTO().setIdno(cryptoHelper.decoder(request.getUserMoviiredDTO().getIdno()));
        }

        // gender
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getGender(), true)) {
            request.getUserMoviiredDTO().setGender(cryptoHelper.decoder(request.getUserMoviiredDTO().getGender()));
        }

        // dob
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getDob(), true)) {
            request.getUserMoviiredDTO().setDob(cryptoHelper.decoder(request.getUserMoviiredDTO().getDob()));
        }

        // email
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getEmail(), true)) {
            request.getUserMoviiredDTO().setEmail(cryptoHelper.decoder(request.getUserMoviiredDTO().getEmail()));
        }

        // cellphone
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getCellphone(), true)) {
            request.getUserMoviiredDTO().setCellphone(cryptoHelper.decoder(request.getUserMoviiredDTO().getCellphone()));
        }

        // userId
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getUserId(), true)) {
            request.getUserMoviiredDTO().setUserId(cryptoHelper.decoder(request.getUserMoviiredDTO().getUserId()));
        }

        decodeRegisterRequestMoviiredDTOExtraInfo(request);
    }

    private void decodeRegisterRequestMoviiredDTOExtraInfo(RegisterRequest request) throws ParsingException {
        // agentCode
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getAgentCode(), true)) {
            request.getUserMoviiredDTO().setAgentCode(cryptoHelper.decoder(request.getUserMoviiredDTO().getAgentCode()));
        }

        // mahindraUser
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getMahindraUser(), true)) {
            request.getUserMoviiredDTO().setMahindraUser(cryptoHelper.decoder(request.getUserMoviiredDTO().getMahindraUser()));
        }

        // mahindraPassword
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getMahindraPassword(), true)) {
            request.getUserMoviiredDTO().setMahindraPassword(cryptoHelper.decoder(request.getUserMoviiredDTO().getMahindraPassword()));
        }

        // status
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getStatus(), true)) {
            request.getUserMoviiredDTO().setStatus(cryptoHelper.decoder(request.getUserMoviiredDTO().getStatus()));
        }

        // sign
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getSign(), true)) {
            request.getUserMoviiredDTO().setSign(cryptoHelper.decoder(request.getUserMoviiredDTO().getSign()));
        }

        // changePasswordRequired
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getChangePasswordRequired(), true)) {
            request.getUserMoviiredDTO().setChangePasswordRequired(cryptoHelper.decoder(request.getUserMoviiredDTO().getChangePasswordRequired()));
        }

        // grade
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getGrade(), true)) {
            request.getUserMoviiredDTO().setGrade(cryptoHelper.decoder(request.getUserMoviiredDTO().getGrade()));
        }

        // tcp
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getTcp(), true)) {
            request.getUserMoviiredDTO().setTcp(cryptoHelper.decoder(request.getUserMoviiredDTO().getTcp()));
        }

        // walletNumber
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getWalletNumber(), true)) {
            request.getUserMoviiredDTO().setWalletNumber(cryptoHelper.decoder(request.getUserMoviiredDTO().getWalletNumber()));
        }

        // lastLogin
        if (!validateNullOrEmpty(null, false, request.getUserMoviiredDTO().getLastLogin(), true)) {
            request.getUserMoviiredDTO().setLastLogin(cryptoHelper.decoder(request.getUserMoviiredDTO().getLastLogin()));
        }
    }

    private RegisterResponse validateRegistraduria(RegisterRequest request) {
        // Vars
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        try {
            // Validate expeditionDate
            if (!validateNullOrEmpty(null, false, request.getUser().getExpeditionDate(), true)) {
                // Search in Registrar
                RegisterResponse registrarResponse = searchRegistraduria(request);

                // Validate value of Registrar
                if (registrarResponse.getCode().equals(SUCCESS_CODE_00)) {
                    // setFirstAndLastName
                    setFirstAndLastName(registrarResponse, request);

                    // ExpeditionDate
                    if (registrarResponse.getData().getExpeditionDate() != null) {
                        // Format
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");

                        // Compare expDateReg -> requestExpDate
                        Date expDateReg = simpleFormat.parse(registrarResponse.getData().getExpeditionDate());
                        Date requestExpDate = simpleFormat.parse(request.getUser().getExpeditionDate());

                        if (!expDateReg.equals(requestExpDate)) {
                            log.error(LOG_FORMATTED_4, ERROR, MOVIIRED, "101", "La fecha de expedición del documento de identidad no concuerda con la enviada en la petición.");
                            response.setCode("101");
                            response.setMessage("La fecha de expedición del documento de identidad no concuerda con la enviada en la petición.");
                        }
                    } else {
                        log.error(LOG_FORMATTED_4, ERROR, MOVIIRED, "100", "La Registraduría no tiene fecha de expedición del documento de identidad.");
                        response.setCode("100");
                        response.setMessage("La Registraduría no tiene fecha de expedición del documento de identidad.");
                    }
                }
            } else {
                log.error(LOG_FORMATTED_4, ERROR, MOVIIRED, REGISTER_ALTERED_CODE, EXPEDITION_DATE_MANDATORY);
                response.setCode(REGISTER_ALTERED_CODE);
                response.setMessage(EXPEDITION_DATE_MANDATORY);
            }
        } catch (java.text.ParseException e) {
            log.info("ERROR - " + e.getMessage());
            response.setCode("102");
            response.setMessage("Ha ocurrido un error en el proceso de validación de registraduría.");
        }

        return response;
    }

    private void setFirstAndLastName(RegisterResponse registrarResponse, RegisterRequest request) {
        String firstName = "";
        String lastName = "";

        // FirstName
        if (registrarResponse.getData().getFirstName() != null) {
            firstName = registrarResponse.getData().getFirstName() + " ";
        }
        if (registrarResponse.getData().getSecondName() != null) {
            firstName += registrarResponse.getData().getSecondName();
        }
        request.getUser().setFirstName(firstName.trim());

        // LastName
        if (registrarResponse.getData().getFirstSurname() != null) {
            lastName = registrarResponse.getData().getFirstSurname() + " ";
        }
        if (registrarResponse.getData().getSecondSurname() != null) {
            lastName += registrarResponse.getData().getSecondSurname();
        }
        request.getUser().setLastName(lastName.trim());
    }

    private void putDummyData(RegisterRequest request) {
        request.getUser().setShopName(dummyData);
        request.getUser().setGender("GEN_MAL");
        request.getUser().setAddress(dummyData);
        request.getUser().setDistrict(dummyData);
        request.getUser().setCity(dummyData);
        request.getUser().setRut(dummyData);
        request.getUser().setDigitVerification("0");
        request.getUser().setActivityEconomic("0");
    }

    @SuppressWarnings("unchecked")
    private RegisterResponse searchBlackList(RegisterRequest params) {
        JsonObject response;
        RegisterResponse registerResponse = new RegisterResponse();

        JsonObject data = new JsonObject();

        // Elimina caracteeres especiales
        String text = changeSpecialCaracteres(params.getUser().getFirstName());
        data.addProperty("documentNumber", params.getUser().getIdno());
        data.addProperty("userName", text);
        try {
            log.info("Invocando listas negras :" + this.blackListProperties.getUrl());

            ResponseEntity<String> entity = (ResponseEntity<String>) UtilsHelper.consume(this.registraduriaProperties.getUrl(), data, String.class, null, null);

            response = new Gson().fromJson(entity.getBody(), JsonObject.class);
            if ((response == null) || (!response.get("code").getAsString().equalsIgnoreCase(SUCCESS_CODE_00))) {
                throw new DataException(SERVER_ERROR_CODE, "Listas negras no disponible");
            }

            if (response.getAsJsonObject("item").get("block").getAsBoolean()) {
                log.info("{} {}", "El usuario esta reportado en listas negras", params.getUser().getIdno());
                throw new DataException(BLACK_LISTS_ERROR_CODE, "El usuario esta reportado en listas negras, no se puede registrar.");
            }

            log.info("{} {}", "El usuario no esta reportado en listas negras", params.getUser().getIdno());
            registerResponse.setCode(SUCCESS_CODE_00);
            registerResponse.setMessage("No se encontro usuario en listas negras");

        } catch (Exception dex) {
            log.error("Listas negras no disponibles");
            log.error(dex.getMessage());
            registerResponse.setCode("99");
            registerResponse.setMessage("Listas negras no disponible");
        } catch (DataException e) {
            registerResponse.setCode(e.getCode());
            registerResponse.setMessage(e.getMessage());
            log.error(e.getMessage());
        }

        return registerResponse;
    }

    private RegisterResponse searchRegistraduria(RegisterRequest params) {
        RegisterResponse registerResponse = new RegisterResponse();

        JsonObject data = new JsonObject();
        data.addProperty("documentNumber", params.getUser().getIdno());
        data.addProperty("forced", this.registraduriaProperties.getForced());
        try {
            log.info("* Invocando registraduria :" + this.registraduriaProperties.getUrl());
            ResponseEntity<String> entity = (ResponseEntity<String>) UtilsHelper.consume(this.registraduriaProperties.getUrl(), data, String.class, null, null);
            log.info(entity.toString());
            RegistraduriaResponse registraduriaResponse = new ObjectMapper().readValue(entity.getBody(), RegistraduriaResponse.class);
            if ((registraduriaResponse.getStatusDTO() != null) &&
                    (registraduriaResponse.getStatusDTO().getCode().equalsIgnoreCase("0"))) {
                registerResponse.setCode(SUCCESS_CODE_00);
                registerResponse.setMessage("Transacción exitosa");
                registerResponse.setData(registraduriaResponse);
                return registerResponse;
            }

            if ((registraduriaResponse.getStatusDTO() != null) &&
                    (registraduriaResponse.getStatusDTO().getCode().equalsIgnoreCase("002"))) {
                registerResponse.setCode("RG_002");
                registerResponse.setMessage("Acceso no autorizado a registraduria");
                return registerResponse;
            }

            registerResponse.setCode("01");
            registerResponse.setMessage("Persona no encontrada en registraduria.");
            registerResponse.setData(registraduriaResponse);
            return registerResponse;
        } catch (Exception exception) {
            log.info("ERROR - " + exception.getMessage());
            registerResponse.setCode("RG_99");
            registerResponse.setMessage("Registraduria caida, Intente mas tarde.");
        }

        return registerResponse;
    }

    private Mono<RegisterResponse> saveUser(Mono<RegisterRequest> mrequest) {
        try {
            return mrequest.flatMap(request -> {
                try {
                    UserMoviired userMoviired = UserMoviired.builder()
                            .phoneNumber(request.getUser().getMsisdn())
                            .firstName(request.getUser().getFirstName().split(" ")[0])
                            .secondName((request.getUser().getFirstName().split(" ").length > 1) ? request.getUser().getFirstName().split(" ")[1] : "")
                            .firstSurname(request.getUser().getLastName().split(" ")[0])
                            .secondSurname((request.getUser().getLastName().split(" ").length > 1) ? request.getUser().getLastName().split(" ")[1] : "")
                            .rut(request.getUser().getRut())
                            .digitVerification(request.getUser().getDigitVerification())
                            .activityEconomic(request.getUser().getActivityEconomic())
                            .registrationDate(new Date())
                            .status(APPROVED)
                            .build();
                    userMoviired.setSignature(this.getSignatureHelper().signMoviiredRegister(userMoviired));
                    this.userMoviiredRepository.save(userMoviired);
                    createPendingUser(request.getUser().getMsisdn(), CHANNEL, null);
                    createUserPendingUpdate(request);
                } catch (ParsingException e) {
                    Mono.error(e);
                }
                RegisterResponse response = new RegisterResponse();
                StatusCode statusCode = this.getStatusCodeConfig().of(REGISTRY_OK);
                response.setCode(statusCode.getCode());
                response.setMessage(statusCode.getMessage());
                return Mono.just(response);
            }).onErrorResume(e -> {
                RegisterResponse resp = new RegisterResponse();
                if (e instanceof DataException) {
                    resp.setType(ERROR);
                    resp.setCode(((DataException) e).getCode());
                    resp.setMessage(e.getMessage());
                    return Mono.just(resp);
                }
                resp.setType(ERROR);
                resp.setCode("99");
                resp.setMessage(e.getMessage());
                return Mono.just(resp);
            });

        } catch (Exception e) {
            // Devolver el error
            log.error(LOG_USER_FAIL_CHANGE_ERROR + e.getMessage());
            return Mono.error(new ManagerException(-2, SERVER_ERROR_CODE, LOG_USER_FAIL_CHANGE_ERROR + e.getMessage()));
        }
    }

    private void savePendingUser(String msisdn, Date date, String userType, String referralCode) {
        PendingUser pendingUser = PendingUser.builder()
                .registrationDate(date)
                .phoneNumber(msisdn)
                .status(true)
                .type(userType)
                .altered(false)
                .referralCode(referralCode)
                .processType(PendingUser.ProcessType.NORMAL_REGISTRATION)
                .build();
        log.info(LOG_SAVING_PENDING_USER, new Gson().toJson(pendingUser));
        this.getPendingUserRepository().save(signPendingUser(pendingUser));
    }

    // Validar los datos obligatorios en la entrda
    public RegisterResponse validateInput(@NotNull OperationType opType, RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        try {
            //userLogin
            if ((opType.equals(OperationType.USER_QUERY_INFO))
                    || (opType.equals(OperationType.GENERATE_OTP))
                    || (opType.equals(OperationType.VALIDATE_OTP))
                    || (opType.equals(OperationType.RESEND_OTP))) {

                if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
                    response.setCode(REGISTER_ALTERED_CODE);
                    response.setMessage(USERLOGIN_MANDATORY);
                    return response;
                } else if (cryptoHelper.decoder(request.getUserLogin()) == null) {
                    response.setCode(REGISTER_ALTERED_CODE);
                    response.setMessage(BAD_FORMAT_USERLOGIN);
                    return response;
                }
            }

            //source
            if ((opType.equals(OperationType.USER_QUERY_INFO))
                    || (opType.equals(OperationType.GENERATE_OTP))
                    || (opType.equals(OperationType.RESEND_OTP))) {

                if (request.getSource() == null || request.getSource().isEmpty()) {
                    response.setCode(REGISTER_ALTERED_CODE);
                    response.setMessage(SOURCE_MANDATORY);
                    return response;
                } else if (!request.getSource().equals(CHANNEL) && !request.getSource().equals(SUBSCRIBER)) {
                    response.setCode(REGISTER_ALTERED_CODE);
                    response.setMessage(TYPE_SOURCE);
                    return response;
                }
            }

            switch (opType) {
                case VALIDATE_OTP:
                    response = validateOTP(request);
                    break;

                case RESEND_OTP:
                    response = validateResendOTP(request);
                    break;

                case REGISTRY_MERCHANT:
                    response = validateRegistryMerchant(request);
                    break;
                default:
                    response.setCode("99");
                    response.setMessage(LBL_INVALID_OPERATION);
            }

        } catch (ParsingException e) {
            response.setCode("99");
            response.setMessage(LBL_INVALID_OPERATION);
        }

        return response;
    }

    private RegisterResponse validateOTP(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        try {
            // otp
            if (validateNullOrEmpty(null, false, request.getOtp(), true)) {
                response.setCode(REGISTER_ALTERED_CODE);
                response.setMessage(USER_OTP_MANDATORY);
                return response;
            } else if (cryptoHelper.decoder(request.getOtp()) == null) {
                response.setCode(REGISTER_ALTERED_CODE);
                response.setMessage(USER_OTP_BAD_FORMAT_MANDATORY);
                return response;
            }

            // source
            if (validateNullOrEmpty(null, false, request.getSource(), true)) {
                response.setCode(REGISTER_ALTERED_CODE);
                response.setMessage(SOURCE_MANDATORY);
                return response;
            } else if (!request.getSource().equals(CHANNEL) && !request.getSource().equals(SUBSCRIBER)) {
                response.setCode("99");
                response.setMessage(TYPE_SOURCE);
                return response;
            }
        } catch (ParsingException e) {
            response.setCode("99");
            response.setMessage(LBL_INVALID_OPERATION);
            return response;
        }
        return response;
    }

    private RegisterResponse validateResendOTP(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        // notifyChannel
        if (validateNullOrEmpty(null, false, request.getNotifyChannel(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_NOTIFY_CHANNEL_MANDATORY);
        } else if (!request.getNotifyChannel().equals("SMS")
                && !request.getNotifyChannel().equals("EMAIL")
                && !request.getNotifyChannel().equals("CALL")) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_NOTIFY_CHANNEL_INCORRECT);
        }
        return response;
    }

    private RegisterResponse validateRegistryMerchant(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        // user
        if (validateNullOrEmpty(request.getUser(), true, "", false)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_MANDATORY);
            return response;
        }

        // firstName
        if (validateNullOrEmpty(null, false, request.getUser().getFirstName(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_NAME_MANDATORY);
            return response;
        }

        // lastName
        if (validateNullOrEmpty(null, false, request.getUser().getLastName(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_LASTNAME_MANDATORY);
            return response;
        }

        // msisdn
        if (validateNullOrEmpty(null, false, request.getUser().getMsisdn(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(MSISDN_MANDATORY);
            return response;
        }
        request.getUser().setCellphone(request.getUser().getMsisdn());

        // idno
        if (validateNullOrEmpty(null, false, request.getUser().getIdno(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_IDNO_MANDATORY);
            return response;
        }

        // isNotNumber idno
        if (UtilsHelper.isNotNumber(request.getUser().getIdno())) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_IDNO_INCORRECT);
            return response;
        }

        // idType
        if (validateNullOrEmpty(null, false, request.getUser().getIdtype(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_IDTYPE_MANDATORY);
            return response;
        }

        // gender
        if (validateNullOrEmpty(null, false, request.getUser().getGender(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(GENDER_MANDATORY);
            return response;
        }

        // email
        if (validateNullOrEmpty(null, false, request.getUser().getEmail(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(EMAIL_MANDATORY);
            return response;
        }

        // dob
        if (validateNullOrEmpty(null, false, request.getUser().getDob(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(DOB_MANDATORY);
            return response;
        }

        return validateRegistryMerchantExtraData(request);
    }

    private RegisterResponse validateRegistryMerchantExtraData(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        // shopName
        if (validateNullOrEmpty(null, false, request.getUser().getShopName(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_SHOPNAME_MANDATORY);
            return response;
        }

        // isInvalidInput shopName
        if (isInvalidInput(request.getUser().getShopName())) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(USER_SHOPNAME_INAVLID);
            return response;
        }

        // address
        if (validateNullOrEmpty(null, false, request.getUser().getAddress().trim(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(ADDRESS_MANDATORY);
            return response;
        }
        request.getUser().setAddress(UtilsHelper.cleanAddress(this.cleanAddressProperties, request.getUser().getAddress()));

        // district
        if (validateNullOrEmpty(null, false, request.getUser().getDistrict().trim(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(DISTRICT_MANDATORY);
            return response;
        }

        // city
        if (validateNullOrEmpty(null, false, request.getUser().getCity().trim(), true)) {
            response.setCode(REGISTER_ALTERED_CODE);
            response.setMessage(CITY_MANDATORY);
            return response;
        }

        return response;
    }

    private boolean validateNullOrEmpty(Object obj, boolean vNullable, String value, boolean vEmpty) {
        // Validate Object Nullable
        if (vNullable && obj == null) {
            return true;
        }

        // Validate Value
        if (vEmpty) {
            // is Null
            if (value == null) {
                return true;
            }

            // is Empty
            return value.isEmpty();
        }

        return false;
    }

    private boolean isInvalidInput(String name) {
        return Pattern.compile(this.mahindraProperties.getSpecialCharacters()).matcher(name).find();
    }

    // Crear el CorrelationID
    private String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilsHelper.getRandomUUID();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "register");
        return cId;
    }

    // Verificar la firma del registro d eusuarios
    private boolean validateSignature(UserMoviired userMoviired) {
        boolean isValid = Boolean.FALSE;
        try {
            this.getSignatureHelper().validateMoviiredRegisterSignature(userMoviired);
            isValid = Boolean.TRUE;

        } catch (ParsingException e) {
            log.error(LOG_ERROR_VALIDATING_SIGNATURE, userMoviired.getPhoneNumber(), " ", e.getMessage());

        } catch (DataException e) {
            if (userMoviired.getSignature().equalsIgnoreCase(STRING_LINE)) {
                log.error(LOG_USER_NOT_HAS_SIGNATURE, userMoviired.getPhoneNumber(), " ", e.getMessage());
            } else {
                log.error(LOG_USER_ALTERED, userMoviired.getPhoneNumber(), " ", e.getMessage());
                userMoviired.setStatus(ALTERED);
                this.userMoviiredRepository.save(userMoviired);
            }
        }

        return isValid;
    }

    private String changeSpecialCaracteres(String cadena) {
        String text = cadena;

        text = text.replaceAll("[ñ]", "n");
        text = text.replaceAll("[Ñ]", "N");
        text = text.replaceAll("[Á]", "A");
        text = text.replaceAll("[á]", "a");
        text = text.replaceAll("[é]", "e");
        text = text.replaceAll("[É]", "E");
        text = text.replaceAll("[í]", "i");
        text = text.replaceAll("[Í]", "I");
        text = text.replaceAll("[ó]", "o");
        text = text.replaceAll("[Ó]", "O");
        text = text.replaceAll("[ú]", "u");
        text = text.replaceAll("[Ú]", "U");
        text = text.replaceAll("[^a-zA-Z0-9 ]", "");

        return text;
    }

    public Mono<RegisterDTO> createPendingUser(String phoneNumber, String origin, String referralCode) {
        logsStart(OPERATION_CREATE_PENDING_USER_FOR_REGISTRATION, origin + TWO_DOTS_SPACE + phoneNumber);
        RegisterDTO response = new RegisterDTO();
        try {
            validatingMethodsPendingUser(phoneNumber, origin);
            List<PendingUser> pendingUsers = this.getPendingUserRepository()
                    .getRegistrationPendingUsers(phoneNumber, origin.toUpperCase(), true, PendingUser.ProcessType.NORMAL_REGISTRATION);
            if (pendingUsers.isEmpty()) {
                savePendingUser(phoneNumber, new Date(), origin, referralCode);
            }
            StatusCode statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            logsEnd(response);
            return Mono.just(response);
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_CREATING_PENDING_USER_FOR_REGISTRATION, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_CREATING_PENDING_USER_FOR_REGISTRATION, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    private void createUserPendingUpdate(RegisterRequest request) {
        UserPendingUpdate uPendingUpdate = UserPendingUpdate.builder()
                .idno(request.getUser().getIdno())
                .phoneNumber(request.getUser().getMsisdn())
                .shopName(request.getUser().getShopName())
                .gender(request.getUser().getGender())
                .address(request.getUser().getAddress())
                .district(request.getUser().getDistrict())
                .city(request.getUser().getCity())
                .rut(request.getUser().getRut())
                .digitVerification(request.getUser().getDigitVerification())
                .activityEconomic(request.getUser().getActivityEconomic())
                .build();
        log.info(LOG_SAVING_USER_PENDING_UPDATE, new Gson().toJson(uPendingUpdate));
        this.userPendingUpdateRepository.save(uPendingUpdate);
    }

    public Mono<RegisterDTO> inactivePendingUser(MoviiService moviiService, String authorizationHeader, String origin) {
        String correlative = logsStart(OPERATION_INACTIVE_PENDING_USER_FOR_REGISTRATION, origin);
        RegisterDTO response = new RegisterDTO();
        try {
            String[] authentication = UtilsHelper.getAuthorizationParts(authorizationHeader, this.getStatusCodeConfig(), this.getGlobalProperties());
            validatingMethodsPendingUser(authentication[ZERO_INT], origin);

            return callMahindraLogin(authentication[ZERO_INT], authentication[ONE_INT], correlative)
                    .flatMap(onMahindraResponse -> {
                        log.info(LOG_FINDING_USERS_FOR_NUMBER_AND_TYPE, authentication[ZERO_INT], origin);

                        StatusCode statusCode;
                        if (!origin.equalsIgnoreCase(onMahindraResponse.getUserType())) {
                            statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.BAD_REQUEST.getStatusCode());
                        } else {
                            List<PendingUser> pendingUsers = this.getPendingUserRepository()
                                    .getRegistrationPendingUsers(authentication[ZERO_INT], origin.toUpperCase(), true,
                                            PendingUser.ProcessType.NORMAL_REGISTRATION);
                            statusCode = inactivePendingUsers(pendingUsers);
                            if (statusCode.getCode().equalsIgnoreCase(ServiceStatusCode.SUCCESS.getStatusCode())) {
                                response.setReferralCode(pendingUsers.get(0).getReferralCode());
                                // Verify and apply subsidy
                                if (onMahindraResponse.getUserType().equals(SUBSCRIBER)) {
                                    moviiService.verifyAndApplySubsidy(correlative, authorizationHeader, authentication[ZERO_INT], onMahindraResponse.getIdType(), onMahindraResponse.getIdNo());
                                }
                            }
                        }
                        response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
                        return Mono.just(response);
                    })
                    .flatMap(resp -> invokeCml(response.getReferralCode(), authentication[ZERO_INT], correlative, resp))
                    .flatMap(resp -> {
                        logsEnd(response);
                        return Mono.just(response);
                    })
                    .onErrorResume(e -> {
                        handleThrowableError(LOG_ERROR_EXECUTING_INACTIVE_USER_FOR_REGISTRATION_STEPS, e, response);
                        logsEnd(response);
                        return Mono.just(response);
                    });
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_INACTIVATING_PENDING_USER_FOR_REGISTRATION, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_INACTIVATING_PENDING_USER_FOR_REGISTRATION, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    public Mono<RegisterDTO> validatePendingUser(String origin, String phoneNumber) {
        logsStart(OPERATION_VALIDATE_PENDING_USER_FOR_REGISTRATION, origin + TWO_DOTS_SPACE + phoneNumber);
        RegisterDTO response = new RegisterDTO();
        try {
            validatingMethodsPendingUser(phoneNumber, origin);

            List<PendingUser> pendingUsers = this.getPendingUserRepository()
                    .getRegistrationPendingUsers(phoneNumber, origin.toUpperCase(), true, PendingUser.ProcessType.NORMAL_REGISTRATION);

            StatusCode statusCode;
            if (pendingUsers.isEmpty()) {
                statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
            } else {
                statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
            }

            response.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage()));
            logsEnd(response);
            return Mono.just(response);
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_REGISTRATION, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_VALIDATING_PENDING_USER_FOR_REGISTRATION, e, response);
        }
        logsEnd(response);
        return Mono.just(response);
    }

    // REQUEST PARAMS VALIDATIONS **************************************************************************************

    private void validatingMethodsPendingUser(String phone, String origin) throws ServiceException {
        String messageForParameterError = null;
        if (phone == null || phone.trim().isEmpty()) {
            messageForParameterError = ERROR_PARAMETER_PHONE;
        }
        if (messageForParameterError == null && !Pattern.matches(REGEX_VALIDATE_PHONE_NUMBER, phone)) {
            messageForParameterError = ERROR_PARAMETER_PHONE_INVALID;
        }
        if (messageForParameterError == null && !CHANNEL.equalsIgnoreCase(origin) && !SUBSCRIBER.equalsIgnoreCase(origin)) {
            messageForParameterError = ERROR_PARAMETER_ORIGIN_INVALID;
        }
        generateErrorValidation(messageForParameterError);
    }

    private void generateErrorValidation(String messageForParameterError) throws ServiceException {
        if (messageForParameterError != null) {
            throw new ServiceException(
                    this.getStatusCodeConfig().of(ServiceStatusCode.BAD_REQUEST.getStatusCode()).getMessage() + TWO_DOTS + messageForParameterError,
                    ServiceStatusCode.BAD_REQUEST.getStatusCode(),
                    this.getGlobalProperties().getName());
        }
    }

    // CALL MAHINDRA ***************************************************************************************************

    private Mono<MahindraDTO> callMahindraLogin(String msisdn, String pin, String correlative) {
        try {
            return invokeMahindra(mahindraClient, this.mahindraProperties, xmlMapper, MahindraDTOHelper.getLoginRequest(this.mahindraProperties, msisdn, pin), correlative)
                    // Handle login response
                    .flatMap(loginResponse -> onMahindraResponse(loginResponse, correlative));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    // INVOKERS ********************************************************************************************************

    private Mono<RegisterDTO> invokeCml(String code, String msisdn, String correlative, RegisterDTO responseRegister) {
        CmlDTO cmlDTO = CmlDTO.builder().code(code).action(ACTION_REGISTER).build();
        if (code == null || code.isEmpty()) {
            return Mono.just(responseRegister);
        }
        log.info(LOG_THIRD_REQUEST, CML_COMPONENT, this.cmlProperties.getUrl() + this.cmlProperties.getPathValidateReferral(), new Gson().toJson(cmlDTO));
        return cmlClient.exchange(HttpMethod.POST, this.cmlProperties.getPathValidateReferral(), cmlDTO, String.class, MediaType.APPLICATION_JSON,
                new ImmutableMap.Builder<String, String>().put(AUTHORIZATION, UtilsHelper.getAuthorizationHeader(cryptoHelper, msisdn, DEFAULT_PIN)).build())
                .flatMap(response -> {
                    try {
                        UtilsHelper.asignarCorrelativo(correlative);
                        log.info(LBL_RESPONSE_SERVICE, CML_COMPONENT.toUpperCase(), response);
                        CmlDTO cmlResponse = this.objectMapper.readValue((String) response, CmlDTO.class);
                        responseRegister.setCmlResponse(cmlResponse);
                        return Mono.just(responseRegister);
                    } catch (Exception e) {
                        log.error(LBL_ERROR, CML_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.just(responseRegister);
                    }
                })
                .onErrorResume(error -> {
                    log.error(LBL_ERROR, CML_COMPONENT.toUpperCase(), error.getMessage());
                    return Mono.just(responseRegister);
                });
    }

    // UserPendingUpdate
    public Mono<UserPendingUpdateResponse> updateUserPendingUpdate(@NotNull Mono<UserPendingUpdateRequest> mrequest) {
        AtomicReference<UserPendingUpdateRequest> userPendingUpdateRequestAtomicReference = new AtomicReference<>();
        return mrequest
                // Validations
                .flatMap(request -> {
                    request.setCorrelationId(asignarCorrelativo(request.getCorrelationId()));
                    try {
                        // Deseencriptar Request
                        if (!validateNullOrEmpty(request.getUserPendingUpdate(), true, "", false)) {
                            decodeUserPendingUpdateRequest(request);
                        }

                        userPendingUpdateRequestAtomicReference.set(request);
                        log.info("*********** USER PENDING UPDATE: STARTED ***********");

                        // Validate datos de entrada
                        UserPendingUpdateResponse responseValidate = validateUserPendingUpdateInput(request);
                        if (!responseValidate.getCode().equals(SUCCESS_CODE_00)) {
                            log.error(PETICION + responseValidate.getMessage());
                            return Mono.error(new DataException(responseValidate.getCode(), responseValidate.getMessage()));
                        }
                    } catch (ParsingException e) {
                        return Mono.error(e);
                    }

                    return Mono.just(request);
                })
                // Save UserPendingUpdate
                .flatMap(respMh -> {
                    asignarCorrelativo(userPendingUpdateRequestAtomicReference.get().getCorrelationId());
                    return this.saveUserPendingUpdate(Mono.just(userPendingUpdateRequestAtomicReference.get()));
                }).flatMap(serviceResponse -> {
                    try {
                        log.info(LOG_RESPONSE_ON_API_CLEVERTAP, this.objectMapper.writer().writeValueAsString(serviceResponse));
                    } catch (JsonProcessingException e) {
                        log.error(LOG_ERROR_MAPPING_RESPONSE, e.getMessage());
                    }
                    return Mono.just(serviceResponse);
                })
                // On Error
                .onErrorResume(e -> {
                    asignarCorrelativo(userPendingUpdateRequestAtomicReference.get().getCorrelationId());

                    UserPendingUpdateResponse response = new UserPendingUpdateResponse();
                    if (e instanceof DataException) {
                        response.setType(ERROR);
                        response.setCode(((DataException) e).getCode());
                        response.setMessage(e.getMessage());
                        return Mono.just(response);
                    }

                    if (e instanceof ManagerException) {
                        response.setType(ERROR);
                        response.setCode(((ManagerException) e).getCodigo());
                        response.setMessage(e.getMessage());
                        return Mono.just(response);
                    }

                    response.setType(ERROR);
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                }).doAfterTerminate(() -> log.info("***********  USER PENDING UPDATE: ENDED ***********"));
    }

    private void decodeUserPendingUpdateRequest(UserPendingUpdateRequest request) throws ParsingException {
        // idno
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getIdno(), true)) {
            request.getUserPendingUpdate().setIdno(cryptoHelper.decoder(request.getUserPendingUpdate().getIdno()));
        }

        // shopName
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getShopName(), true)) {
            request.getUserPendingUpdate().setShopName(cryptoHelper.decoder(request.getUserPendingUpdate().getShopName()));
        }

        // gender
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getGender(), true)) {
            request.getUserPendingUpdate().setGender(cryptoHelper.decoder(request.getUserPendingUpdate().getGender()));
        }

        // address
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getAddress(), true)) {
            request.getUserPendingUpdate().setAddress(cryptoHelper.decoder(request.getUserPendingUpdate().getAddress()));
        }

        // district
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getDistrict(), true)) {
            request.getUserPendingUpdate().setDistrict(cryptoHelper.decoder(request.getUserPendingUpdate().getDistrict()));
        }

        // city
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getCity(), true)) {
            request.getUserPendingUpdate().setCity(cryptoHelper.decoder(request.getUserPendingUpdate().getCity()));
        }

        // rut
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getRut(), true)) {
            request.getUserPendingUpdate().setRut(cryptoHelper.decoder(request.getUserPendingUpdate().getRut()));
        }

        // digitVerification
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getDigitVerification(), true)) {
            request.getUserPendingUpdate().setDigitVerification(cryptoHelper.decoder(request.getUserPendingUpdate().getDigitVerification()));
        }

        // activityEconomic
        if (!validateNullOrEmpty(null, false, request.getUserPendingUpdate().getActivityEconomic(), true)) {
            request.getUserPendingUpdate().setActivityEconomic(cryptoHelper.decoder(request.getUserPendingUpdate().getActivityEconomic()));
        }
    }

    private UserPendingUpdateResponse validateUserPendingUpdateInput(UserPendingUpdateRequest request) {
        UserPendingUpdateResponse response = new UserPendingUpdateResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        try {
            // Validate userPendingUpdate
            if (validateNullOrEmpty(request.getUserPendingUpdate(), true, "", false)) {
                response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
                response.setMessage(USER_PENDING_UPDATE_MANDATORY);
                return response;
            } else {
                response = validateUserPendingUpdateInputData(request);
            }
        } catch (Exception e) {
            response.setCode("99");
            response.setMessage(LBL_INVALID_OPERATION);
        }

        return response;
    }

    private UserPendingUpdateResponse validateUserPendingUpdateInputData(UserPendingUpdateRequest request) {
        UserPendingUpdateResponse response = new UserPendingUpdateResponse();
        response.setCode(SUCCESS_CODE_00);
        response.setMessage(PARAMETER_OK);

        // idno
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getIdno(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_IDNO_MANDATORY);
            return response;
        }
        if (UtilsHelper.isNotNumber(request.getUserPendingUpdate().getIdno())) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_IDNO_INCORRECT);
            return response;
        }

        // shopName
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getShopName(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_SHOPNAME_MANDATORY);
            return response;
        }
        if (isInvalidInput(request.getUserPendingUpdate().getShopName())) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_SHOPNAME_INVALID);
            return response;
        }

        // gender
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getGender(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_GENDER_MANDATORY);
            return response;
        }

        // address
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getAddress(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_ADDRESS_MANDATORY);
            return response;
        } else {
            request.getUserPendingUpdate().setAddress(UtilsHelper.cleanAddress(this.cleanAddressProperties, request.getUserPendingUpdate().getAddress()));
        }

        // district
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getDistrict(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_DISTRICT_MANDATORY);
            return response;
        }

        // city
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getCity(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_CITY_MANDATORY);
            return response;
        }

        // rut
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getRut(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_RUT_MANDATORY);
            return response;
        }

        // digitVerification
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getDigitVerification(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_DIGIT_VERIFICATION_MANDATORY);
            return response;
        }
        if (UtilsHelper.isNotNumber(request.getUserPendingUpdate().getDigitVerification())) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_DIGIT_VERIFICATION_INCORRECT);
            return response;
        }

        // activityEconomic
        if (validateNullOrEmpty(null, false, request.getUserPendingUpdate().getActivityEconomic(), true)) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_ACTIVITY_ECONOMIC_MANDATORY);
            return response;
        }
        if (UtilsHelper.isNotNumber(request.getUserPendingUpdate().getActivityEconomic())) {
            response.setCode(USER_PENDING_UPDATE_ALTERED_CODE);
            response.setMessage(USER_PENDING_UPDATE_ACTIVITY_ECONOMIC_INCORRECT);
            return response;
        }
        return response;
    }

    private Mono<UserPendingUpdateResponse> saveUserPendingUpdate(Mono<UserPendingUpdateRequest> mrequest) {
        try {
            return mrequest.flatMap(request -> {
                UserPendingUpdateResponse response = new UserPendingUpdateResponse();
                try {
                    // FindUserPendingByIdno
                    UserPendingUpdate upu = this.userPendingUpdateRepository.findByIdno(request.getUserPendingUpdate().getIdno());

                    // Validate status
                    if (upu.getStatus().equals(PENDING)) {
                        // Update
                        UserPendingUpdate uPendingUpdate = UserPendingUpdate.builder()
                                .id(upu.getId())
                                .idno(request.getUserPendingUpdate().getIdno())
                                .phoneNumber(upu.getPhoneNumber())
                                .shopName(request.getUserPendingUpdate().getShopName())
                                .gender(request.getUserPendingUpdate().getGender())
                                .address(request.getUserPendingUpdate().getAddress())
                                .district(request.getUserPendingUpdate().getDistrict())
                                .city(request.getUserPendingUpdate().getCity())
                                .rut(request.getUserPendingUpdate().getRut())
                                .digitVerification(request.getUserPendingUpdate().getDigitVerification())
                                .activityEconomic(request.getUserPendingUpdate().getActivityEconomic())
                                .dateUpdate(new Date())
                                .status(APPROVED)
                                .build();

                        // Input Mask
                        log.info(Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), "idno", "rut"));
                        this.userPendingUpdateRepository.save(uPendingUpdate);
                    } else {
                        response.setType(ERROR);
                        response.setCode(USER_PENDING_UPDATE_STATUS_CODE);
                        response.setMessage(LOG_ERROR_STATUS_USER_PENDING_UPDATED);
                        return Mono.just(response);
                    }
                } catch (Exception e) {
                    Mono.error(e);
                }

                StatusCode statusCode = this.getStatusCodeConfig().of(REGISTRY_OK);
                response.setCode(statusCode.getCode());
                response.setMessage(statusCode.getMessage());
                return Mono.just(response);
            }).onErrorResume(err -> {
                UserPendingUpdateResponse userPendingUpdateResponse = new UserPendingUpdateResponse();
                if (err instanceof DataException) {
                    userPendingUpdateResponse.setType(ERROR);
                    userPendingUpdateResponse.setCode(((DataException) err).getCode());
                    userPendingUpdateResponse.setMessage(err.getMessage());
                    return Mono.just(userPendingUpdateResponse);
                }
                userPendingUpdateResponse.setType(ERROR);
                userPendingUpdateResponse.setCode("99");
                userPendingUpdateResponse.setMessage(err.getMessage());
                return Mono.just(userPendingUpdateResponse);
            });

        } catch (Exception e) {
            // Devolver el error
            log.error(LOG_USER_FAIL_PENDING_DATA_UPDATE + e.getMessage());
            return Mono.error(new ManagerException(-2, SERVER_ERROR_CODE, LOG_USER_FAIL_PENDING_DATA_UPDATE + e.getMessage()));
        }
    }

    public Mono<UserPendingUpdateResponse> validateUserPendingUpdate(String pphoneNumber) {
        String phoneNumber = pphoneNumber;
        logsStart(OPERATION_VALIDATE_USER_PENDING_FOR_UPDATE, phoneNumber);
        UserPendingUpdateResponse response = new UserPendingUpdateResponse();
        try {
            // Decode phoneNumber
            if (!phoneNumber.isEmpty()) {
                phoneNumber = cryptoHelper.decoder(phoneNumber);
            }

            // Validate phoneNumber
            validatingUserPendingUpdate(phoneNumber);

            // Valido si el usuario ya está registrado en la bd
            UserPendingUpdate upu = this.userPendingUpdateRepository.findByPhoneNumber(phoneNumber);
            if (upu == null) {
                // El usuario no existe en la bd
                response.setType(ERROR);
                response.setCode(USER_PENDING_UPDATED_STATUS_CODE);
                response.setMessage(LOG_ERROR_NOT_FOUND_USER_PENDING_UPDATED);
                return Mono.just(response);
            }

            // Valido el estado del registro
            if (upu.getStatus().equals(APPROVED)) {
                // El usuario ya esta actualizado
                response.setType(OK);
                response.setCode(USER_PENDING_UPDATED_STATUS_CODE);
                response.setMessage(LOG_ERROR_STATUS_USER_PENDING_UPDATED);
                return Mono.just(response);
            }

            response.setType(ERROR);
            response.setCode(USER_PENDING_UPDATED_STATUS_CODE);
            response.setMessage(LOG_ERROR_STATUS_USER_PENDING_FOR_UPDATE);

            logsEnd(response);
            return Mono.just(response);
        } catch (ParsingException | ServiceException e) {
            log.error(LOG_ERROR_USER_PENDING_UPDATED + e.getMessage());
            return Mono.error(new ManagerException(-2, SERVER_ERROR_CODE, LOG_ERROR_USER_PENDING_UPDATED + e.getMessage()));
        } catch (Exception e) {
            log.error(LOG_ERROR_USER_PENDING_UPDATE + e.getMessage());
            return Mono.error(new ManagerException(-2, SERVER_ERROR_CODE, LOG_ERROR_USER_PENDING_UPDATE + e.getMessage()));
        }
    }

    private void validatingUserPendingUpdate(String phoneNumber) throws ServiceException {
        String errorMessage = null;
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errorMessage = ERROR_PARAMETER_PHONE;
        }
        if (errorMessage == null && !Pattern.matches(REGEX_VALIDATE_PHONE_NUMBER, phoneNumber)) {
            errorMessage = ERROR_PARAMETER_PHONE_INVALID;
        }
        generateErrorValidation(errorMessage);
    }
}


