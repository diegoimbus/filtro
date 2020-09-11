package co.movii.auth.server.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.domain.dto.*;
import co.movii.auth.server.domain.entity.AuthRegisterLogin;
import co.movii.auth.server.domain.entity.AuthRegisterPassword;
import co.movii.auth.server.domain.entity.MacBlackList;
import co.movii.auth.server.domain.enums.ClientType;
import co.movii.auth.server.domain.enums.OperationType;
import co.movii.auth.server.domain.enums.ProviderType;
import co.movii.auth.server.domain.repository.AuthRegisterLoginRepository;
import co.movii.auth.server.domain.repository.AuthRegisterPasswordRepository;
import co.movii.auth.server.domain.repository.MacBlackListRepository;
import co.movii.auth.server.domain.repository.RepositoryFactory;
import co.movii.auth.server.exception.*;
import co.movii.auth.server.helper.HelperFactory;
import co.movii.auth.server.helper.OTPHelper;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.properties.*;
import co.movii.auth.server.providers.*;
import co.movii.auth.server.providers.mahindra.request.CommandChangePasswordRequest;
import co.movii.auth.server.providers.mahindra.request.CommandLoginServiceRequest;
import co.movii.auth.server.providers.mahindra.request.CommandResetPasswordRequest;
import co.movii.auth.server.providers.mahindra.request.CommandUserQueryInfoRequest;
import co.movii.auth.server.providers.supportprofile.request.ProfileNameRequest;
import co.movii.auth.server.security.crypt.CryptoUtility;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.util.Security;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static co.movii.auth.server.domain.enums.OperationType.CHANGE_PASSWORD;
import static co.movii.auth.server.domain.enums.OperationType.RESET_PASSWORD;
import static co.movii.auth.server.helper.ConstantsHelper.*;

@Slf4j
@Service
public final class AuthenticationService {

    private static final String LOG_FORMATTED_4 = "{} {} {} {}";
    private static final String LBL_INVALID_OPERATION = "Operación inválida";
    private static final String BAD_VALIDATION_DATE = "Error en validación de fechas";
    private static final String RESPONSE = " Response";

    private static final int NUM_0 = 0;
    private static final int NUM_23 = 23;
    private static final int NUM_59 = 59;
    private static final int NUM_86400000 = 86400000;
    private static final String PMASK = "********";
    private static final String TWO_POINTS = ":  ";
    private static final String LEFT_ARROW = "<==";
    private static final String ENCRYPT_END = "************ encrypt end ************";
    private static final String CRYPT_ERROR = "Error encriptando, intente mas tarde";
    private static final String NO_PERMISSIONS = "No posee los permisos necesarios para consumir el servicio.";

    private static final String USERLOGIN_MANDATORY = "El campo userLogin es obligatorio.";
    private static final String BAD_FORMAT_USERLOGIN = "El campo userLogin no cumple con el formato de encriptación.";
    private static final String PIN_MANDATORY = "El campo pin es obligatorio.";
    private static final String BAD_FORMAT_PIN = "El campo pin no cumple con el formato de encriptación.";
    private static final String IMEI_MANDATORY = "El campo imei es obligatorio.";
    private static final String SOURCE_MANDATORY = "El campo source es obligatorio.";
    private static final String TYPE_SOURCE = "El campo source debe SUBSCRIBER";
    private static final String INVALID_CLV = "VALIDATE_PASSWORD";
    private static final String GENERATE_OTP = "GENERATE_OTP";
    private static final String VALIDATION_DEVICE = "VALIDATION_DEVICE";
    private static final String VALIDATION_DEVICE_MAC = "VALIDATION_MAC";
    private static final String ERROR_MAX_DEVICES = "ERROR_MAX_DEVICES";
    private static final String ERROR = "ERROR";

    private static final String MOVII = "MOVII";
    private static final String CHANNEL = "CHANNEL";
    private static final String SUBSCRIBER = "SUBSCRIBER";
    private static final String NOMBRE = "NOMBRE";

    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final OTPHelper otpHelper;
    private final ReactiveConnector smsLoginConnector;
    private final CryptoUtility cryptoUtility;

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;

    private final SupportSmsProperties supportSmsProperties;
    private final GlobalProperties globalProperties;
    private final ExtraValidationsProperties extraValidations;
    private final StatusCodeConfig statusCodeConfig;

    private final QueryUserInfoService queryUserInfoService;
    private final ValidationPasswordService validationPasswordService;

    private final AuthRegisterLoginRepository authRegisterLoginRepository;
    private final AuthRegisterPasswordRepository authRegisterPasswordRepository;
    private final MacBlackListRepository macBlackListRepository;
    private final RegisterProperties registerProperties;
    private final ReactiveConnector registerConnector;

    @Value("${providers.movii}")
    private String sourceMovii;


    public AuthenticationService(@NotNull ClientFactory pclientFactory,
                                 @NotNull ParserFactory pparserFactory,
                                 @Qualifier("smsLoginConnector") @NotNull ReactiveConnector psmsLoginConnector,
                                 @NotNull PropertiesFactory propertiesFactory,
                                 @NotNull HelperFactory helperFactory,
                                 @NotNull ServiceFactory serviceConfig,
                                 RepositoryFactory repositoryFactory) {
        super();
        this.parserFactory = pparserFactory;
        this.clientFactory = pclientFactory;
        this.otpHelper = helperFactory.getOtpHelper();
        this.supportSmsProperties = propertiesFactory.getSupportSmsProperties();
        this.globalProperties = propertiesFactory.getGlobalProperties();
        this.extraValidations = propertiesFactory.getExtraValidations();
        this.smsLoginConnector = psmsLoginConnector;
        this.queryUserInfoService = serviceConfig.getQueryUserInfoService();
        this.validationPasswordService = serviceConfig.getValidationPasswordService();
        this.authRegisterPasswordRepository = repositoryFactory.getAuthRegisterPasswordRepository();
        this.cryptoUtility = helperFactory.getCryptoUtility();
        this.macBlackListRepository = repositoryFactory.getMacBlackListRepository();

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.authRegisterLoginRepository = repositoryFactory.getAuthRegisterLoginRepository();
        this.statusCodeConfig = helperFactory.getStatusCodeConfig();
        this.registerProperties = propertiesFactory.getRegisterProperties();
        this.registerConnector = propertiesFactory.getRegisterConnector();
    }


    private  Mono<Response> getInfoUser(AtomicReference<Request> requestAtomicReference, Request request){
        requestAtomicReference.set(request);
        asignarCorrelativo(request.getCorrelationId());
        try {
            log.info("additionalValidations  Request -->");
            requestInputMask(OperationType.VALIDATE_ADDITIONAL, request);

            // busca la información del usuario
            log.info("{}", "************ OBTENER INFORMACIÓN DEL USUARIO ************");

            if (request.getUser() == null) {
                return process(Mono.just(request), OperationType.LOGIN, null);
            }

            log.info("La información del usuario ya se tiene en el request");
            Response response = new Response();
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
            response.setErrorMessage("OK");
            response.setUser(request.getUser());

            return Mono.just(response);

        } catch (ParseException | JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<Response> validateAdditionalValidations(AtomicReference<Request> requestAtomicReference, Response response, OperationType opType){
        asignarCorrelativo(requestAtomicReference.get().getCorrelationId());
        try {
            Request request = requestAtomicReference.get();
            request.setUser(response.getUser());
            requestAtomicReference.set(request);


            if (response.getErrorCode().equalsIgnoreCase("003590") && (CHANGE_PASSWORD.equals(opType))) {
                return Mono.error(new ExtraValidationNotEnabledException());
            }

            // VERIFICACIÓN DE VALIDACIONES EXTRAS ACTIVAS
            if (!extraValidations.isEnable()) {
                return Mono.error(new ExtraValidationNotEnabledException());
            }

            // Verificar si el teléfono está en la WHITE-LIST
            log.info("{}", "whiteList");
            if (extraValidations.getWhiteList().contains(request.getUserLogin())) {
                return Mono.error(new ExtraValidationNotEnabledException("Usuario ingresado en la WHITE-LIST"));
            }

        } catch (Exception e) {
            return Mono.error(e);
        }

        return Mono.just(response);
    }



    public Mono<Response> validateImeiSoporteLogin(AtomicReference<Request> requestAtomicReference,
                                                   Response request,
                                                   AtomicReference<AuthRegisterLogin> loginauth,
                                                   AtomicReference<User> userAtomicReference){
        asignarCorrelativo(requestAtomicReference.get().getCorrelationId());
        if (!request.getErrorCode().equals("00")) {
            return Mono.error(new LoginNoValidException());
        }
        userAtomicReference.set(request.getUser());

        // No validar al Usuario de soporte
        if (requestAtomicReference.get().getImei().equals("SUPPORT-USER")) {
            Response response = new Response();
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
            response.setErrorMessage("OK");
            return Mono.just(response);
        }

        // VALIDAR IMEI
        log.info("{}", "************ STARTED - PROCESS VALIDATE ADDITIONAL ************");
        userAtomicReference.set(request.getUser());
        requestAtomicReference.get().setUser(request.getUser());

        // Obtener el último ingreso exitoso
        User user = requestAtomicReference.get().getUser();
        String userType = user.getUserType();

        // Primer Login (obligar a registrar dispositivo)
        AuthRegisterLogin login = authRegisterLoginRepository.findFirstByPhoneNumberAndClientTypeOrderByLoginDateDesc(requestAtomicReference.get().getUser().getMsisdn(), ClientType.resolve(userType));
        loginauth.set(login);
        if (login == null) {
            return Mono.just(request)
                    .flatMap(req -> otpHelper.generateOTP(requestAtomicReference.get().getSource(), requestAtomicReference.get().getUser())
                    ).flatMap(otpResponse -> {
                        if (otpResponse.getResponseCode().equals(StatusCode.Level.SUCCESS.value())) {
                            return Mono.error(new ValidateDateException("VALIDATE_DATE"));
                        }
                        return Mono.error(new GenerateOTPException(GENERATE_OTP));
                    });
        }

        // Validar el dispositivo
        return validateMac(login, requestAtomicReference.get());

    }


    public Mono<Response> validateLoginOfDay(AtomicReference<Request> requestAtomicReference,
                                             AtomicReference<AuthRegisterLogin> loginauth ){
        asignarCorrelativo(requestAtomicReference.get().getCorrelationId());
        try {
            // VALIDACIÓN DE INGRESOS EN EL DÍA
            if (loginauth.get() != null && extraValidations.isSuscriberDate()) {
                return validateDate(loginauth.get(), requestAtomicReference.get());
            }

        } catch (ParseException e) {
            return Mono.error(e);
        }

        Response response = new Response();
        response.setErrorCode(StatusCode.Level.SUCCESS.value());
        response.setErrorMessage("OK");
        return Mono.just(response);
    }


    public Mono<Response> validateFormatPassword(AtomicReference<Request> requestAtomicReference,
                                                 AtomicReference<User> userAtomicReference,
                                                 OperationType opType){
        asignarCorrelativo(requestAtomicReference.get().getCorrelationId());
        // VALIDACIÓN DE FORMATO
        log.info("{}", "************ VALIDATE FORMAT ************");
        if (!CHANGE_PASSWORD.equals(opType) && !validationPasswordService.isValidPasswordFormat(requestAtomicReference.get().getPin(),
                requestAtomicReference.get().getUser().getIdno(),
                requestAtomicReference.get().getUser().getMsisdn(),
                requestAtomicReference.get().getUser().getDob(),
                UtilHelper.DATE_FORMAT_DOB_LOGIN_MAHINDRA)) {
            return Mono.error(new PasswordFormatInvalidException(INVALID_CLV));
        } else {
            if (CHANGE_PASSWORD.equals(opType) &&
                    !validationPasswordService.isValidPasswordFormat(requestAtomicReference.get().getNewpin(),
                            requestAtomicReference.get().getUser().getIdno(),
                            requestAtomicReference.get().getUser().getMsisdn(),
                            requestAtomicReference.get().getUser().getDob(),
                            UtilHelper.DATE_FORMAT_DOB_LOGIN_MAHINDRA)) {
                return Mono.error(new PasswordFormatInvalidException(INVALID_CLV));
            }
        }

        Response response = new Response();
        response.setErrorCode(StatusCode.Level.SUCCESS.value());
        response.setErrorMessage("OK");
        response.setUser(userAtomicReference.get());
        log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);

        if (RESET_PASSWORD.equals(opType)) {
            saveRegisterChangePassword(requestAtomicReference.get().getUserLogin(), response.getErrorCode());
        }

        // Notificar login exitoso
        if (OperationType.LOGIN.equals(opType) || OperationType.VALIDATE_OTP.equals(opType)) {
            ExecutorService taskThread = Executors.newSingleThreadExecutor();
            Request request = requestAtomicReference.get();
            request.setUser(response.getUser());
            taskThread.submit(() -> this.notifyLoginSucess(request));
            taskThread.shutdown();
        }

        return Mono.just(response);

    }


    public Mono<Response> additionalValidations(@NotNull @RequestBody Mono<Request> brequest,
                                                OperationType opType) {
        AtomicReference<Request> requestAtomicReference = new AtomicReference<>();
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        AtomicReference<AuthRegisterLogin> loginauth = new AtomicReference<>();

        // Realizar las validaciones adicionales
        return brequest.flatMap(request -> getInfoUser(requestAtomicReference,request))
                .flatMap(response ->  validateAdditionalValidations(requestAtomicReference, response, opType))
                .flatMap(response -> validateImeiSoporteLogin(requestAtomicReference, response,loginauth,userAtomicReference))
                .flatMap(resp ->  validateLoginOfDay(requestAtomicReference, loginauth))
                .flatMap(resp -> validateFormatPassword(requestAtomicReference, userAtomicReference, opType))
                .onErrorResume(e -> errorResume(e, requestAtomicReference.get(), opType ) );
    }

    public Mono<Response> errorResume(Throwable e, Request request, OperationType opType) {
        if (e instanceof ExtraValidationNotEnabledException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of("OK");
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            response.setUser(request.getUser());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);

            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            // Notificar login exitoso
            if (OperationType.LOGIN.equals(opType) || RESET_PASSWORD.equals(opType)) {
                ExecutorService taskThread = Executors.newSingleThreadExecutor();
                request.setUser(response.getUser());
                taskThread.submit(() -> this.notifyLoginSucess(request));
                taskThread.shutdown();
            }

            return Mono.just(response);
        }

        if (e instanceof LoginNoValidException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of("LOGIN_INVALID");
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            return Mono.just(response);
        }

        if (e instanceof ValidateDateException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of("VALIDATION_DATE");
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            return Mono.just(response);
        }

        return errorResumePt2(e,request,opType);
    }

    public Mono<Response> errorResumePt2(Throwable e, Request request, OperationType opType) {
        if (e instanceof MaxDevicesException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of(ERROR_MAX_DEVICES);
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            return Mono.just(response);
        }

        if (e instanceof ValidateImeiException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of(VALIDATION_DEVICE);
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            return Mono.just(response);
        }

        if (e instanceof ValidateMacException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of(VALIDATION_DEVICE_MAC);
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);

            return Mono.just(response);
        }

        return errorResumePt3(e,  request, opType);
    }

    public Mono<Response> errorResumePt3(Throwable e, Request request, OperationType opType) {


        if (e instanceof GenerateOTPException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of("ERROR_GENERATE_OTP");
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }
            return Mono.just(response);
        }

        if (e instanceof PasswordFormatInvalidException) {
            Response response = new Response();
            StatusCode statusCode = statusCodeConfig.of(INVALID_CLV);
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
            if (RESET_PASSWORD.equals(opType)) {
                saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
            }

            return Mono.just(response);
        }

        Response response = new Response();
        response.setErrorCode("99");
        response.setErrorMessage(e.getMessage());
        response.setErrorType(ERROR);
        log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, response);
        if (RESET_PASSWORD.equals(opType)) {
            saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
        }

        return Mono.just(response);

    }


    public Mono<Response> process(@NotNull @RequestBody Mono<Request> brequest, OperationType opType, ProviderType provType) {
        AtomicReference<ProviderType> providerTypeAtomicReference = new AtomicReference<>();
        AtomicReference<Request> requestAtomicReference = new AtomicReference<>();

        return brequest.flatMap(request ->
                setProvider(request, opType, providerTypeAtomicReference, provType, requestAtomicReference)
        ).flatMap(obRespUserQueryInfo ->
                invokeProvider(requestAtomicReference.get().getCorrelationId(), providerTypeAtomicReference.get(), requestAtomicReference.get(), opType)
        ).flatMap(obResp ->
                transforrmReesponseClient(obResp, requestAtomicReference.get().getCorrelationId(), providerTypeAtomicReference.get(), requestAtomicReference.get(), opType)
        ).flatMap(resp -> {
            if (resp.getErrorCode().equals("00") &&
                    (opType == RESET_PASSWORD || opType == CHANGE_PASSWORD) &&
                    requestAtomicReference.get().getSource().equals(SUBSCRIBER)) {
                log.info("Invoking inactivate of pending registration for user {}", requestAtomicReference.get().getUserLogin());
                invokeInactivatePendingChangePassword(requestAtomicReference.get().getCorrelationId(),
                        requestAtomicReference.get().getUserLogin(), requestAtomicReference.get().getNewpin());
            }
            return Mono.just(resp);
        }).onErrorResume(e ->
                managerError(e, requestAtomicReference.get().getCorrelationId())
        );
    }

    public Mono<Request> setProvider(Request request, OperationType opType, AtomicReference<ProviderType> providerTypeAtomicReference, ProviderType provType, AtomicReference<Request> requestAtomicReference) {
        asignarCorrelativo(request.getCorrelationId());
        try {
            requestInputMask(opType, request);
        } catch (ParseException | JsonProcessingException e) {
            return Mono.error(e);
        }
        providerTypeAtomicReference.set(provType);
        if (provType == null) {
            providerTypeAtomicReference.set(ProviderType.MAHINDRA);
        }
        log.info("{}", "************ STARTED - PROCESS ************");
        requestAtomicReference.set(request);
        return Mono.just(request);
    }


    public Mono<Object> invokeProvider(
            String correlativeId,
            ProviderType providerType,
            Request request,
            OperationType opType
    ) {
        asignarCorrelativo(correlativeId);
        // INVOCAR A PROVIDER
        try {
            // Obtener los procesadores específicos al tipo de transacción
            ReactiveConnector provider = this.clientFactory.getClient(providerType);
            IParser parser = this.parserFactory.getParser(providerType, opType);
            IRequest irequest = parser.parseRequest(request);
            Map<String, String> headers = clientFactory.getHeaders(opType, providerType, request);

            IRequest mhrl = requestProviderMask(providerType, opType, irequest);
            log.info("URL A INVOCAR: " + this.clientFactory.getUrl(providerType, opType, request));
            log.info(LOG_FORMATTED_4, "==>", " Request", TWO_POINTS, this.clientFactory.getRequestString(providerType, mhrl));

            return provider.exchange(
                    this.clientFactory.getHttpMethod(providerType, opType),
                    this.clientFactory.getUrl(providerType, opType, request),
                    this.clientFactory.getRequestString(providerType, irequest),
                    String.class,
                    this.clientFactory.getMediaType(providerType, opType),
                    headers);

        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.error(e);
        }
    }


    public Mono<Response> transforrmReesponseClient(
            Object obResp,
            String correlativeId,
            ProviderType providerType,
            Request request,
            OperationType opType
    ) {
        asignarCorrelativo(correlativeId);
        // TRANSFORMAR RESPUESTA AL CLIENTE

        try {
            IParser parser = parserFactory.getParser(providerType, opType);

            // Transformar XML response a  response
            String mhr = (String) obResp;
            IResponse mhResponse = this.clientFactory.readValue(providerType, opType, mhr);
            if (OperationType.PROFILE_NAME.equals(opType)) {
                log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, "OK");
            } else {
                log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, this.xmlMapper.writeValueAsString(mhResponse));
            }
            // Transformar response especifica
            return Mono.just(parser.parseResponse(request, mhResponse));

        } catch (IOException | AuthException e) {
            log.error(e.getMessage());
            return Mono.error(e);
        }
    }


    public Mono<Response> managerError(Throwable e, String correlativeId) {

        asignarCorrelativo(correlativeId);
        Response response = new Response();

        log.error(e.getMessage());
        if (e instanceof NumberFormatException) {
            response.setErrorCode("99");
            response.setErrorMessage("La variable statusOperation debe ser numérica");
            response.setErrorType(ErrorType.DATA.name());
            return Mono.just(response);
        }

        if (e instanceof ConnectTimeoutException) {
            response.setErrorCode("408");
            response.setErrorMessage(e.getMessage());
            response.setErrorType(ErrorType.COMMUNICATION.name());
            return Mono.just(response);
        }

        if (e instanceof PasswordFormatInvalidException) {
            StatusCode sc = statusCodeConfig.of(INVALID_CLV);
            response.setErrorCode(sc.getCode());
            response.setErrorType(ErrorType.DATA.name());
            response.setErrorMessage(sc.getMessage());
            return Mono.just(response);
        }

        response.setErrorCode("99");
        response.setErrorMessage(e.getMessage());
        response.setErrorType(ERROR);
        return Mono.just(response);
    }


    public Mono<Response> resendOTP(Mono<Request> request) {
        AtomicReference<Request> atomicRequest = new AtomicReference<>();
        log.info("{}", "************ STARTED - RESEND OTP ************");
        return request.flatMap(req -> {
            atomicRequest.set(req);
            asignarCorrelativo(atomicRequest.get().getCorrelationId());

            return process(Mono.just(atomicRequest.get()), OperationType.USER_QUERY_INFO, null);
        }).flatMap(responseLogin -> {
            Request req = atomicRequest.get();
            return otpHelper.resendOTP(req.getSource(), responseLogin.getUser().getCellphone(), req.getNotifyChannel());
        }).map(otpResponse -> {
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, otpResponse);

            Response response = new Response();
            response.setErrorCode(otpResponse.getResponseCode());
            response.setErrorMessage(otpResponse.getResponseMessage());
            response.setErrorType("");

            return response;
        }).onErrorResume(e -> {
            log.error(e.getMessage(), e);

            Response response = new Response();
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());
            response.setErrorType(ERROR);
            return Mono.just(response);
        });
    }

    public Mono<Response> validateOTP(Mono<Request> request) {
        AtomicReference<Request> atomicRequest = new AtomicReference<>();
        log.info("{}", "************ STARTED - VALIDATE OTP ************");
        return request.flatMap(req -> {
            atomicRequest.set(req);
            asignarCorrelativo(atomicRequest.get().getCorrelationId());
            return process(Mono.just(atomicRequest.get()), OperationType.LOGIN, null);
        }).flatMap(responseLogin -> {
            Request req = atomicRequest.get();
            log.info("validando OTP");
            return otpHelper.isValid(req.getSource(), responseLogin.getUser().getCellphone(), req.getOtp());
        }).map(otpResponse -> {
            asignarCorrelativo(atomicRequest.get().getCorrelationId());
            log.info(LOG_FORMATTED_4, LEFT_ARROW, RESPONSE, TWO_POINTS, otpResponse);

            Response response = new Response();
            response.setErrorCode(otpResponse.getResponseCode());
            response.setErrorMessage(otpResponse.getResponseMessage());
            response.setErrorType("");

            if (otpResponse.isValid()) {
                Request req = atomicRequest.get();
                log.info("insert register {},{},{}", req.getUserLogin(), req.getImei(), req.getSource());
                saveRegister(req.getUserLogin(), req.getImei(), ClientType.resolve(req.getSource()));
            }

            return response;
        }).onErrorResume(e -> {
            log.error(e.getMessage(), e);

            Response response = new Response();
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());
            response.setErrorType(ERROR);
            return Mono.just(response);
        });
    }


    public Response validateInputValidateOtp(Request request, Response presponse) {
        Response response = presponse;
        if (request.getOtp() == null || request.getOtp().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo otp es obligatorio.");
        } else if (cryptoUtility.decryptAES(request.getOtp()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo otp no cumple con el formato de encriptación.");
        }

        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(USERLOGIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }


        return validateInputValidateOtpPt2(request, response);
    }


    public Response validateInputValidateOtpPt2(Request request, Response presponse) {
        Response response = presponse;
        if (request.getPin() == null || request.getPin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(PIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getPin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_PIN);
        }

        if (request.getImei() == null || request.getImei().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(IMEI_MANDATORY);
        }
        if (request.getSource() == null || request.getSource().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(SOURCE_MANDATORY);
        } else if (!request.getSource().equals(SUBSCRIBER)) {
            response.setErrorCode("99");
            response.setErrorMessage(TYPE_SOURCE);
        }
        return response;
    }


    public Response validateInputGenerateOtp(Request request, Response presponse) {
        Response response = presponse;
        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(USERLOGIN_MANDATORY);

        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }

        if (request.getImei() == null || request.getImei().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(IMEI_MANDATORY);
        }

        if (request.getSource() == null || request.getSource().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(SOURCE_MANDATORY);
        } else if (!request.getSource().equals(SUBSCRIBER)) {
            response.setErrorCode("99");
            response.setErrorMessage(TYPE_SOURCE);
        }
        return response;
    }


    public Response validateInputResendOtp(Request request, Response presponse) {
        Response response = presponse;
        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(USERLOGIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }

        if (request.getImei() == null || request.getImei().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(IMEI_MANDATORY);
        }

        return validateInputResendOtpPt2(request, response);
    }

    public Response validateInputResendOtpPt2(Request request, Response presponse) {
        Response response = presponse;
        if (request.getSource() == null || request.getSource().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(SOURCE_MANDATORY);
        } else if (!request.getSource().equals(SUBSCRIBER)) {
            response.setErrorCode("99");
            response.setErrorMessage(TYPE_SOURCE);
        }
        if (request.getNotifyChannel() == null || request.getNotifyChannel().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo notifyChannel es obligatorio.");
        } else if (!request.getNotifyChannel().equals("SMS") && !request.getNotifyChannel().equals("EMAIL") && !request.getNotifyChannel().equals("CALL")) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo notifyChannel debe ser SMS, EMAIL o CALL");
        }
        return response;
    }

    public Response validateInputChangePassword(Request request, Response presponse) {
        Response response = presponse;
        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(USERLOGIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }

        if (request.getPin() == null || request.getPin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(PIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getPin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_PIN);
        }

        if (request.getNewpin() == null || request.getNewpin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo newpin es obligatorio.");
        } else if (cryptoUtility.decryptAES(request.getNewpin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo newpin no cumple con el formato de encriptación.");
        }
        return validateInputChangePassword2(request, response);
    }

    public Response validateInputChangePassword2(Request request, Response presponse) {
        Response response = presponse;
        return getResponse(request, response);
    }


    public Response validateInputResetPasssword(Request request, Response presponse) {
        Response response = presponse;
        if (request.getOtp() == null || request.getOtp().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo otp es obligatorio.");
        } else if (cryptoUtility.decryptAES(request.getOtp()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo otp no cumple con el formato de encriptación.");
        }

        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage(USERLOGIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }

        if (request.getNewpin() == null || request.getNewpin().isEmpty()) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo newpin es obligatorio.");
        } else if (cryptoUtility.decryptAES(request.getNewpin()) == null) {
            response.setErrorCode("99");
            response.setErrorMessage("El campo newpin no cumple con el formato de encriptación.");
        }
        return validateInputResetPasssword2(request, response);
    }


    public Response validateInputResetPasssword2(Request request, Response presponse) {

        return getResponse(request, presponse);
    }

    private Response getResponse(Request request, Response presponse) {
        if (request.getConfirmnewpin() == null || request.getConfirmnewpin().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage("El campo confirmnewpin es obligatorio.");
        } else if (cryptoUtility.decryptAES(request.getConfirmnewpin()) == null) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage("El campo confirmnewpin no cumple con el formato de encriptación.");
        }
        if (request.getImei() == null || request.getImei().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(IMEI_MANDATORY);
        }
        if (request.getSource() == null || request.getSource().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(SOURCE_MANDATORY);
        } else if (!request.getSource().equals(SUBSCRIBER)) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(TYPE_SOURCE);
        }
        return presponse;
    }


    public Response validateInputLogin(Request request, Response presponse) {
        if (request.getUserLogin() == null || request.getUserLogin().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(USERLOGIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getUserLogin()) == null) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(BAD_FORMAT_USERLOGIN);
        }
        if (request.getPin() == null || request.getPin().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(PIN_MANDATORY);
        } else if (cryptoUtility.decryptAES(request.getPin()) == null) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(BAD_FORMAT_PIN);
        }

        return validateInputLoginPt2(request, presponse);
    }


    public Response validateInputLoginPt2(Request request, Response presponse) {
        if (request.getImei() == null || request.getImei().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(IMEI_MANDATORY);
        }
        if (request.getSource() == null || request.getSource().isEmpty()) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(SOURCE_MANDATORY);
        } else if (!request.getSource().equals(SUBSCRIBER)) {
            presponse.setErrorCode("99");
            presponse.setErrorMessage(TYPE_SOURCE);
        }
        return presponse;
    }

    public Response validateInput(@NotNull OperationType opType, Request request) {
        Response response = new Response();
        response.setErrorCode("00");
        response.setErrorMessage("datos correctos.");

            request.setChannel("ANDROID");
        switch (opType) {
            case VALIDATE_OTP:
                return validateInputValidateOtp(request, response);
            case GENERATE_OTP:
                return validateInputGenerateOtp(request, response);
            case RESEND_OTP:
                return validateInputResendOtp(request, response);
            case CHANGE_PASSWORD:
                return validateInputChangePassword(request, response);
            case RESET_PASSWORD:
                return validateInputResetPasssword(request, response);
            case LOGIN:
                return validateInputLogin(request, response);
            case USER_QUERY_INFO:
            case VALIDATE_ADDITIONAL:
                break;
            default:
                response.setErrorCode("99");
                response.setErrorMessage("La operación no es valida.");
        }

        return response;
    }

    // Devolver el request enmascarado para imprimir en el log
    public void requestInputMask(@NotNull OperationType opType, Request request) throws ParseException, JsonProcessingException {
        Request requestClone = new Request(request);
        switch (opType) {
            case GENERATE_OTP:
                break;

            case CHANGE_PASSWORD:
                requestClone.setNewpin(PMASK);
                requestClone.setPin(PMASK);
                requestClone.setConfirmnewpin(PMASK);
                break;

            case RESET_PASSWORD:
                requestClone.setNewpin(PMASK);
                requestClone.setConfirmnewpin(PMASK);
                requestClone.setOtp(PMASK);
                break;

            case LOGIN:
                requestClone.setPin(PMASK);
                requestClone.setNewpin(PMASK);
                requestClone.setConfirmnewpin(PMASK);
                requestClone.setOtp(PMASK);

                break;

            case USER_QUERY_INFO:
            case PROFILE_NAME:
            case VALIDATE_ADDITIONAL:
                validateAdditionalMask(requestClone);
                break;
            default:
                throw new ParseException(LBL_INVALID_OPERATION);
        }
        log.info(this.objectMapper.writer().writeValueAsString(requestClone));
    }

    // Guardar el cambio de dispositivo
    public void validateAdditionalMask(Request requestClone) {
        requestClone.setPin(PMASK);
        requestClone.setUserLogin(PMASK);

        if (requestClone.getNewpin() != null) {
            requestClone.setNewpin(PMASK);
        }

        if (requestClone.getOtp() != null) {
            requestClone.setOtp(PMASK);
        }

        if (requestClone.getConfirmnewpin() != null) {
            requestClone.setConfirmnewpin(PMASK);
        }

        if (requestClone.getUser() != null) {
            requestClone.getUser().setMpin(PMASK);
        }
    }

    // Guardar el cambio de dispositivo
    public void saveRegister(String login, String device, ClientType type) {
        AuthRegisterLogin auth = new AuthRegisterLogin();
        auth.setClientType(type);
        auth.setDevice(device);
        auth.setPhoneNumber(login);
        auth.setLoginDate(new Date());
        authRegisterLoginRepository.save(auth);
    }


    // Guardar registro de la respuesta de reset y cambio de contraseña
    public void saveRegisterChangePassword(String phoneNumber, String returnCode) {
        AuthRegisterPassword authPassword = new AuthRegisterPassword();
        authPassword.setReturnCode(returnCode);
        authPassword.setPhoneNumber(phoneNumber);
        authPassword.setChangePasswordDate(new Date());
        authRegisterPasswordRepository.save(authPassword);
    }

    // Notificar el bloqueo del usuario
    public void notifyBlockUser(Request request) {
        asignarCorrelativo(request.getCorrelationId());

        try {
            // Verificar si está habilitado el envío de notificaciones LOGIN exitosos
            if (((sourceMovii.equals(request.getSource())) && (!supportSmsProperties.isLoginMoviiEnable()))) {
                return;
            }

            // Variables del template #0003: ##ORIGIN## ##DATE## ##APPLICATION## ##IP##
            Map<String, String> variables = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            // Establecer el template/origen
            String template = supportSmsProperties.getUserBlockMoviiTemplate();
            String origin = MOVII;

            // Establecer la aplicación fuente
            String app = "APLICACIÓN MOVII";

            // Establecer el nombre de la persona
            Response r = this.queryUserInfoService.queryUserInfo(Mono.just(request), request.getUserLogin());

            variables.put(NOMBRE, r.getUser().getFirstName());
            variables.put("ORIGIN", origin);
            variables.put("APPLICATION", app);
            variables.put("DATE", sdf.format(new Date()));
            variables.put("IP", (request.getIp() != null) ? request.getIp() : "0.0.0.0");
            variables.put("IMEI", (request.getImei() != null) ? request.getImei() : "Dispositivo Móvil");

            // Armar el mensaje a enviar
            SmsMessage sms = SmsMessage.builder()
                    .phoneNumber(request.getUserLogin())
                    .templateCode(template)
                    .variables(variables)
                    .build();
            SmsRequest smsRequest = new SmsRequest(sms);

            // Invocar al servicio
            log.info("\tNotifyBlockSucess Request: {URL: '{}', SMS: '{}'", supportSmsProperties.getUrl(), Security.printIgnore(new ObjectMapper().writeValueAsString(smsRequest), "otp", "pin"));
            String resp = (String) this.smsLoginConnector.post(smsRequest, String.class, MediaType.APPLICATION_JSON, null).block();
            JsonNode jsonNode = objectMapper.readTree(resp);
            log.info("SMS NotifyBlockSucess: PhoneNumber: {}, Status envío: {}", request.getUserLogin(), jsonNode.get("outcome").get("error").get("errorMessage"));

        } catch (Exception e) {
            log.error("ERROR enviando el SMS de notificación de usuario bloqueado. Causa: {}", e.getMessage());
        }
    }

    // Crear el CorrelationID
    public String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilHelper.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "movii-authentication");
        return cId;
    }

    // METODOS PRIVADOS

    private Mono<Response> validateMac(@NotNull AuthRegisterLogin login, @NotNull Request request) {
        if (extraValidations.isSuscriberImei()) {
            return validateSubscriberImei(login, request);
        }
        Response response = new Response();
        response.setErrorCode(StatusCode.Level.SUCCESS.value());
        response.setErrorMessage("OK");
        return Mono.just(response);
    }

    // Validar cambio de dispositivo
    private Mono<Response> validateSubscriberImei(@NotNull AuthRegisterLogin login, @NotNull Request request) {
        asignarCorrelativo(request.getCorrelationId());
        log.info("{}", "**** VALIDATE IMEI ****");

        // Obtener la MAC address
        String imei = request.getImei();
        if (imei == null) {
            imei = "";
        }

        // Verificar si la MAC está en la lista
        String[] arr;
        if (!imei.trim().isEmpty()) {
            arr = imei.split(";");
        } else {
            arr = new String[]{""};
        }
        List<String> macAddresses = Arrays.asList(arr);

        // Verificar si la MAC está en lista negra
        List<MacBlackList> macBlackList = this.macBlackListRepository.findByMacIn(macAddresses);
        if ((macBlackList != null) && (!macBlackList.isEmpty())) {
            return Mono.error(new ValidateMacException("El IMEI: '" + imei + "', se encuentra en la BlackList"));
        }

        // Si se ingresa desde el mismo dispositivo dejar pasar
        if (login.getDevice().equals(request.getImei())) {
            Response response = new Response();
            response.setErrorCode("00");
            return Mono.just(response);
        }

        // Validar el máximo de cambios de dispositivos posibles
        try {
            this.validateDeviceChange(login, request);
        } catch (MaxDevicesException mxe) {
            return Mono.error(new MaxDevicesException(ERROR_MAX_DEVICES));
        }

        // Generar OTP de registro de dispositivo
        return Mono.just(request)
                .flatMap(req -> otpHelper.generateOTP(req.getSource(), req.getUser())
                ).flatMap(otpResponse -> {
                    if (otpResponse.getResponseCode().equals(StatusCode.Level.SUCCESS.value())) {
                        return Mono.error(new ValidateImeiException(VALIDATION_DEVICE));
                    }
                    return Mono.error(new GenerateOTPException(GENERATE_OTP));
                });
    }

    // Validar cambio de dispositivo
    public void validateDeviceChange(@NotNull AuthRegisterLogin login, @NotNull Request request) throws MaxDevicesException {
        asignarCorrelativo(request.getCorrelationId());
        log.info("{}", "************ VALIDATE IMEI ************");

        // Si se ingresa desde el mismo dispositivo dejar pasar
        if (login.getDevice().equals(request.getImei())) {
            return;
        }

        // Establecer la fecha de validación
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, AuthenticationService.NUM_0);
        startDate.set(Calendar.MINUTE, AuthenticationService.NUM_0);
        startDate.set(Calendar.SECOND, AuthenticationService.NUM_0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 1);
        endDate.set(Calendar.HOUR_OF_DAY, AuthenticationService.NUM_23);
        endDate.set(Calendar.MINUTE, AuthenticationService.NUM_59);
        endDate.set(Calendar.SECOND, AuthenticationService.NUM_59);

        // Establecer el MAX dispositivos
        int maxDispositivos = extraValidations.getSuscriberMaxDevices();

        // Validar cantidad de dispositivos registrados en el día actual
        String userType = SUBSCRIBER;
        if (!request.getUser().getUserType().equals(ClientType.SUBSCRIBER.name())) {
            userType = CHANNEL;
        }

        int cantDispositivos = this.authRegisterLoginRepository.countAllByPhoneNumberAndClientTypeAndLoginDateBetween(request.getUser().getMsisdn(), ClientType.resolve(userType), startDate.getTime(), endDate.getTime());
        if (cantDispositivos >= maxDispositivos) {
            throw new MaxDevicesException(ERROR_MAX_DEVICES);
        }
    }

    // Validar cantidad de ingresos por día
    private Mono<Response> validateDate(@NotNull AuthRegisterLogin login, @NotNull Request request) throws ParseException {
        asignarCorrelativo(request.getCorrelationId());
        log.info("{}", "************ VALIDATE DATE ************");
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNow = dateFormat.parse(dateFormat.format(new Date()));
            if (login.getLoginDate().before(dateNow)) {
                return Mono.just(request).flatMap(req -> otpHelper.generateOTP(req.getSource(), req.getUser())
                ).flatMap(otpResponse -> {
                    if (otpResponse.getResponseCode().equals(StatusCode.Level.SUCCESS.value())) {
                        return Mono.error(new ValidateDateException(VALIDATION_DEVICE));
                    }
                    return Mono.error(new GenerateOTPException(GENERATE_OTP));
                });
            }
        } catch (Exception ex) {
            throw new ParseException(BAD_VALIDATION_DATE, ex);
        }

        Response response = new Response();
        response.setErrorCode("00");
        return Mono.just(response);
    }

    // Devolver el request enmascarado para imprimir en el log
    private IRequest requestProviderMask(ProviderType providerType, @NotNull OperationType opType, IRequest request) throws ParseException {
        IRequest mhrl = request;

        switch (providerType) {
            case MAHINDRA:
                switch (opType) {
                    case GENERATE_OTP:
                        break;

                    case CHANGE_PASSWORD:
                        CommandChangePasswordRequest commandChangePasswordRequest = new CommandChangePasswordRequest((CommandChangePasswordRequest) request);
                        commandChangePasswordRequest.setNewmpin(PMASK);
                        commandChangePasswordRequest.setMpin(PMASK);
                        commandChangePasswordRequest.setConfirmmpin(PMASK);
                        mhrl = commandChangePasswordRequest;
                        break;

                    case RESET_PASSWORD:
                        CommandResetPasswordRequest commandResetPasswordRequest = new CommandResetPasswordRequest((CommandResetPasswordRequest) request);
                        commandResetPasswordRequest.setNewpin(PMASK);
                        commandResetPasswordRequest.setConfirmpin(PMASK);
                        commandResetPasswordRequest.setOtp(PMASK);
                        mhrl = commandResetPasswordRequest;
                        break;

                    case LOGIN:
                        CommandLoginServiceRequest commandLoginServiceRequest = new CommandLoginServiceRequest((CommandLoginServiceRequest) request);
                        commandLoginServiceRequest.setMpin(PMASK);
                        mhrl = commandLoginServiceRequest;
                        break;

                    case USER_QUERY_INFO:
                        mhrl = new CommandUserQueryInfoRequest((CommandUserQueryInfoRequest) request);
                        break;

                    default:
                        throw new ParseException(LBL_INVALID_OPERATION);
                }
                break;
            case SUPPORT_PROFILE:
                if (!opType.equals(OperationType.PROFILE_NAME)) {
                    throw new ParseException(LBL_INVALID_OPERATION);
                }
                mhrl = new ProfileNameRequest((ProfileNameRequest) request);

                break;

            default:
                throw new ParseException("Provider invalido");
        }

        return mhrl;
    }

    // Notificar el nuevo ingreso
    private void notifyLoginSucess(Request request) {
        asignarCorrelativo(request.getCorrelationId());

        try {
            // Verificar si está habilitado el envío de notificaciones LOGIN exitosos
            if (((sourceMovii.equals(request.getSource())) && (!supportSmsProperties.isLoginMoviiEnable()))) {
                return;
            }

            // Variables del template #0003: ##ORIGIN## ##DATE## ##APPLICATION## ##IP##
            Map<String, String> variables = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            // Establecer el template/origen
            String template = supportSmsProperties.getLoginTemplateMovii();
            String origin = MOVII;

            // Establecer la aplicación fuente
            String app = "APLICACIÓN MOVII";

            variables.put(NOMBRE, request.getUser().getFirstName());
            variables.put("ORIGIN", origin);
            variables.put("APPLICATION", app);
            variables.put("DATE", sdf.format(new Date()));
            variables.put("IP", (request.getIp() != null) ? request.getIp() : "0.0.0.0");
            variables.put("IMEI", (request.getImei() != null) ? request.getImei() : "Dispositivo Móvil");

            // Armar el mensaje a enviar
            SmsMessage sms = SmsMessage.builder()
                    .phoneNumber(request.getUser().getCellphone())
                    .templateCode(template)
                    .variables(variables)
                    .build();
            SmsRequest smsRequest = new SmsRequest(sms);

            // Invocar al servicio
            log.info("\tNotifyLoginSucess Request: {URL: '{}', SMS: '{}'", supportSmsProperties.getUrl(), Security.printIgnore(new ObjectMapper().writeValueAsString(smsRequest), "otp", "pin"));
            String resp = (String) this.smsLoginConnector.post(smsRequest, String.class, MediaType.APPLICATION_JSON, null).block();
            JsonNode jsonNode = objectMapper.readTree(resp);
            log.info("SMS Login: PhoneNumber: {}, Status envío: {}", request.getUserLogin(), jsonNode.get("outcome").get("error").get("errorMessage"));
        } catch (Exception e) {
            log.error("ERROR enviando el SMS de notificación de login exitoso. Causa: {}", e.getMessage());
        }
    }

    public Response encrypt(Request request, String token) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        Response response = new Response();
        log.info("{}", "************ encrypt ************");
        if (request == null || request.getValue() == null || request.getValue().isEmpty()) {
            response.setErrorMessage("El campo 'value' es un campo obligatorio.");
            response.setErrorCode("95");
            return response;
        }

        if (!token.equals(this.globalProperties.getToken())) {
            log.error("{}", NO_PERMISSIONS);
            response.setErrorMessage(NO_PERMISSIONS);
            response.setErrorCode("97");
            log.info("{}", ENCRYPT_END);
            return response;
        }

        try {
            Date today = formateador.parse(formateador.format(new Date()));
            Date expiration = formateador.parse(this.globalProperties.getExpiration());
            if (today.before(expiration)) {
                response.setValue(cryptoUtility.encryptAES(request.getValue()));
                log.info("{}", "Encriptado exitoso.");
            } else {
                log.info("{}", "Token Vencido");
                response.setErrorMessage("Token expirado.");
                response.setErrorCode("99");
                return response;
            }

            int dias = (int) ((expiration.getTime() - today.getTime()) / AuthenticationService.NUM_86400000);
            response.setErrorMessage("Transacción Exitosa. Quedan " + dias + " para usar el servicio con el token suministrado.");
            response.setErrorCode("00");

        } catch (java.text.ParseException e) {
            log.error("{}", CRYPT_ERROR);
            response.setErrorMessage(CRYPT_ERROR);
            response.setErrorCode("96");
        }
        log.info("{}", ENCRYPT_END);
        return response;
    }

    public Response decrypt(Request request, String token) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        Response response = new Response();
        log.info("{}", "************ encrypt ************");
        if (request == null || request.getValue() == null || request.getValue().isEmpty()) {
            response.setErrorMessage("El campo 'value' es un campo obligatorio.");
            response.setErrorCode("95");
            return response;
        }

        if (!token.equals(this.globalProperties.getToken())) {
            log.error("{}", NO_PERMISSIONS);
            response.setErrorMessage(NO_PERMISSIONS);
            response.setErrorCode("97");
            log.info("{}", ENCRYPT_END);
            return response;
        }

        try {
            Date today = formateador.parse(formateador.format(new Date()));
            Date expiration = formateador.parse(this.globalProperties.getExpiration());
            if (today.before(expiration)) {
                response.setValue(cryptoUtility.decryptAES(request.getValue()));
                log.info("{}", "Encriptado exitoso.");
            } else {
                log.info("{}", "Token Vencido");
                response.setErrorMessage("Token expirado.");
                response.setErrorCode("99");
                return response;
            }

            int dias = (int) ((expiration.getTime() - today.getTime()) / AuthenticationService.NUM_86400000);
            response.setErrorMessage("Transacción Exitosa. Quedan " + dias + " para usar el servicio con el token suministrado.");
            response.setErrorCode("00");

        } catch (java.text.ParseException e) {
            log.error("{}", CRYPT_ERROR);
            response.setErrorMessage(CRYPT_ERROR);
            response.setErrorCode("96");
        }
        log.info("{}", ENCRYPT_END);
        return response;
    }

    private void invokeInactivatePendingChangePassword(String correlative, String msisdn, String password) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(HEADER_AUTHORIZATION, cryptoUtility.encryptAES(msisdn) + TWO_DOTS + cryptoUtility.encryptAES(password));
        try {
            invokeRegister("INACTIVATE PENDING USER FOR CHANGE PASSWORD", correlative, HttpMethod.POST, registerProperties.getPathInactivatePendingChangePassword(),
                    new RegisterComponentDTO(), headers)
                    .onErrorResume(error -> {
                        asignarCorrelativo(correlative);
                        log.error("Error invoking inactivate pending user on Register component: {}", error.getMessage());
                        return Mono.just(new RegisterComponentDTO());
                    })
                    .subscribe();
        } catch (JsonProcessingException e) {
            log.error("Error invoking inactivate pending user on Register component: {}", e.getMessage());
        }
    }

    private Mono<RegisterComponentDTO> invokeRegister(String process, String correlative, HttpMethod httpMethod, String path, RegisterComponentDTO request, Map<String, String> headers) throws JsonProcessingException {
        log.info(LOG_THIRD_REQUEST, correlative, REGISTER_COMPONENT, registerProperties.getUrl() + path, this.objectMapper.writer().writeValueAsString(request));
        return registerConnector.exchange(httpMethod, path, request, String.class, MediaType.APPLICATION_JSON, headers)
                .flatMap(response -> {
                    asignarCorrelativo(correlative);
                    try {
                        RegisterComponentDTO registerComponentDTO = this.objectMapper.readValue((String) response, RegisterComponentDTO.class);
                        log.info(LBL_RESPONSE_REGISTER, correlative, process, REGISTER_COMPONENT.toUpperCase(), this.objectMapper.writer().writeValueAsString(registerComponentDTO));
                        return Mono.just(registerComponentDTO);
                    } catch (Exception e) {
                        log.error(LBL_ERROR_REGISTER, correlative, process, REGISTER_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.error(e);
                    }
                });
    }
}

