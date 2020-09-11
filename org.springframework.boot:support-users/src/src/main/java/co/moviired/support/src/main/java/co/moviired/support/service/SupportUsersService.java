package co.moviired.support.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.support.audit.PushService;
import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.conf.MahindraProperties;
import co.moviired.support.conf.SupportAutenticationProperties;
import co.moviired.support.domain.dto.Request;
import co.moviired.support.domain.dto.Response;
import co.moviired.support.domain.dto.UserDto;
import co.moviired.support.domain.dto.enums.Gender;
import co.moviired.support.domain.dto.enums.OperationType;
import co.moviired.support.domain.dto.enums.Status;
import co.moviired.support.domain.dto.validator.ValidatorHelper;
import co.moviired.support.domain.entity.User;
import co.moviired.support.domain.entity.UserTmp;
import co.moviired.support.helper.OTPHelper;
import co.moviired.support.helper.UtilidadesHelper;
import co.moviired.support.provider.mahindra.request.CommandLoginServiceRequest;
import co.moviired.support.provider.mahindra.request.CommandUserServiceRequest;
import co.moviired.support.repository.IUserRepository;
import co.moviired.support.repository.IUserTmpRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Data
@Slf4j
@Service
public final class SupportUsersService {

    private static final String NAME_USER = "nameUser";
    private static final String EMAIL_SUCCESS = "<== Se ha enviado del mensaje EMAIL de forma satisfactoria:";
    private static final String EMAIL_ERROR = "<== Error programando el envío del mensaje Email- Causa: ";
    private static final String PMASK = "****";
    private static final int SIGN_DEFAULT = 31231238;
    private static final String FLECHA = "==>";
    private static final String URL_ENVIO = "URL de envio de correos =";
    private static final String FLECHAIZQ = "<==";
    private static final String FORMAT_LOG = "{} {} {} {}";
    private static final String FORMAT_LOG2 = "{} {} {}";
    private static final String EMAIL = "email";
    private static final String USER_MOT_EXIST = "Usuario no existe";
    private static final String TRANSACCION_EXITOSA = "Transacción exitosa.";
    private static final String CODE_EXITOSA = "00";
    private static final String NUMBER100 = "100";
    private static final String NUMBER101 = "101";
    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String RESPONSE = "RESPONSE ";
    private static final String REQUEST = "REQUEST ";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS SUPPORT USER";

    private final PushService pushAuditService;
    private final SupportAutenticationProperties supportAutenticationProperties;
    private final MahindraProperties mahindraProperties;
    private final IUserRepository userRepository;
    private final IUserTmpRepository userTmpRepository;
    private final GlobalProperties globalProperties;
    private final XmlMapper xmlMapper;
    private final OTPHelper otpHelper;
    private final CryptoHelper cryptoHelper;


    @Autowired
    private RestTemplate restTemplate;

    public SupportUsersService(
            @NotNull GlobalProperties pglobalProperties,
            @NotNull OTPHelper potpHelper,
            @NotNull SupportAutenticationProperties psupportAutenticationProperties,
            @NotNull MahindraProperties pmahindraProperties,
            @NotNull PushService ppushAuditService,
            @NotNull IUserRepository puserRepository,
            @NotNull IUserTmpRepository puserTmpRepository,
            @NotNull CryptoHelper pcryptoHelper) {
        super();
        this.globalProperties = pglobalProperties;
        this.otpHelper = potpHelper;
        this.supportAutenticationProperties = psupportAutenticationProperties;
        this.mahindraProperties = pmahindraProperties;
        this.pushAuditService = ppushAuditService;
        this.userRepository = puserRepository;
        this.userTmpRepository = puserTmpRepository;
        this.cryptoHelper = pcryptoHelper;

        this.xmlMapper = new XmlMapper();
    }

    // SERVICE METHODS

    public Response login(String correlationId, Request brequest) {
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "login");
        Response response = new Response();
        try {

            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(requestMask(brequest, OperationType.LOGIN)));
            ValidatorHelper.validationInput(brequest, OperationType.LOGIN);
            Optional<User> oUser = this.userRepository.findFirstByMsisdnAndMpin(brequest.getMsisdn(), cryptoHelper.encoder(brequest.getMpin()));
            if (!oUser.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, "Credenciales invalidas");
            }
            User user = oUser.get();
            switch (user.getStatus()) {
                case ACTIVE:
                    if (true) {
                        response.setUser(user.toPublic());
                        user.setLastLogin(new Date());
                        user.setSign(user.hashCode());
                        this.userRepository.save(user);

                        response.getUser().setMpin("*******");
                        response.setErrorCode(CODE_EXITOSA);
                        response.setErrorMessage("Login Exitoso.");
                    } else {
                        response.setUser(null);
                        response.setErrorCode("93");
                        response.setErrorMessage("Usuario Manipulado.");
                    }
                    break;
                case ALTERED:
                    response.setUser(null);
                    response.setErrorCode("93");
                    response.setErrorMessage("Usuario Manipulado.");
                    break;
                case INACTIVE:
                    response.setUser(null);
                    response.setErrorCode("92");
                    response.setErrorMessage("Usuario Inactivo.");
                    break;
                case PENDING:
                    response.setUser(null);
                    response.setErrorCode("91");
                    response.setErrorMessage("Usuario Pendiente de ser validado.");
                    break;
                default:
                    response.setUser(null);
                    response.setErrorCode("91");
                    response.setErrorMessage("Usuario en estado no valido.");
            }
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));

        } catch (ServiceException e) {
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "login");
        return response;
    }

    public Response updateUser(String correlationId, Request brequest, String authorization) {
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "updateUser");

        Response response;
        try {
            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(requestMask(brequest, OperationType.UPDATE_ALL)));
            ValidatorHelper.validationInput(brequest, OperationType.UPDATE_ALL);
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(brequest.getUser().getMsisdn());
            if (!oUser.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST);
            }
            UserTmp user = new UserTmp();

            user.setId(oUser.get().getId());
            user.setFirstName(brequest.getUser().getFirstName());
            user.setLastName(brequest.getUser().getLastName());
            user.setMsisdn(brequest.getUser().getMsisdn());
            user.setUserType(brequest.getUser().getUserType());
            user.setIdtype(brequest.getUser().getIdtype());
            user.setIdno(brequest.getUser().getIdno());
            user.setGender(Gender.valueOf(brequest.getUser().getGender()));
            user.setDob(brequest.getUser().getDob());
            user.setEmail(brequest.getUser().getEmail());
            user.setCellphone(brequest.getUser().getCellphone());
            user.setMahindraUser(brequest.getUser().getMahindraUser());
            user.setChangePasswordRequired(brequest.getUser().getChangePasswordRequired());
            user.setWalletNumber(brequest.getUser().getWalletNumber());
            user.setStatus(Status.valueOf(brequest.getUser().getStatus()));
            user.setGrade(brequest.getUser().getGrade());
            user.setRegistrationDate(new Date());
            user.setDateUpdate(new Date());
            user.setCmlUserId(brequest.getUser().getCmlUserId());
            user.setUpdateUser(brequest.getUser().getUpdateUser());
            user.setSign(user.hashCode());
            user.setUser(oUser.get());
            this.userTmpRepository.save(user);

            response = Response.builder()
                    .errorCode(CODE_EXITOSA)
                    .errorMessage(TRANSACCION_EXITOSA)
                    .build();

            Request finalBrequest = brequest;
            taskThread.submit(() -> notificar(finalBrequest.getUser(), correlationId, this.globalProperties.getPathUpdateUser(), "RISK"));
            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_MODIFY",
                            correlationId,
                            "Ha modificado el usuario " + user.getMsisdn(),
                            generateDetailOperationUpdate(oUser.get(), user))
            );

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);

        } catch (ServiceException e) {
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "updateUser");
        return response;
    }


    public Response approvedUserTmp(String correlationId, String msisdn, String authorization) {
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "approveUserTmp");

        Response response = null;
        try {
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(msisdn);
            Optional<UserTmp> oUserTmp = this.userTmpRepository.findFirstByMsisdn(msisdn);
            if (!oUser.isPresent() || !oUserTmp.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, "Usuario no posee cambios");
            }

            Map<String, String> detailOperation;
            UserTmp userTmp = oUserTmp.get();
            User user = oUser.get();

            detailOperation = this.generateDetailOperationUpdate(user, userTmp);

            user.setFirstName(userTmp.getFirstName());
            user.setLastName(userTmp.getLastName());
            user.setMsisdn(userTmp.getMsisdn());
            user.setUserType(userTmp.getUserType());
            user.setIdtype(userTmp.getIdtype());
            user.setIdno(userTmp.getIdno());
            user.setGender(userTmp.getGender());
            user.setDob(userTmp.getDob());
            user.setEmail(userTmp.getEmail());
            user.setCellphone(userTmp.getCellphone());
            user.setMahindraUser(userTmp.getMahindraUser());
            user.setChangePasswordRequired(userTmp.getChangePasswordRequired());
            user.setWalletNumber(userTmp.getWalletNumber());
            user.setStatus(userTmp.getStatus());
            user.setGrade(userTmp.getGrade());
            user.setDateUpdate(userTmp.getDateUpdate());
            user.setCmlUserId(userTmp.getCmlUserId());
            user.setUpdateUser(userTmp.getUpdateUser());
            user.setSign(user.hashCode());
            this.userRepository.save(user);

            Optional<User> aUser = this.userRepository.findFirstByMsisdn(userTmp.getUpdateUser());
            if (aUser.isPresent()) {
                notificarUser(userTmp.toPublic(), correlationId, this.globalProperties.getPathApprovedUser(), aUser.get().getEmail(), aUser.get().getFirstName());
            }

            this.userTmpRepository.delete(oUserTmp.get());

            response = Response.builder()
                    .errorCode(CODE_EXITOSA)
                    .errorMessage(TRANSACCION_EXITOSA)
                    .build();


            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_MODIFY_APPROVED",
                            correlationId,
                            "Ha aprobado la modificacion del usuario " + user.getMsisdn(),
                            detailOperation)
            );

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);

        } catch (ServiceException e) {
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "approveUserTmp");
        return response;
    }

    public Response rejectedUserTmp(String correlationId, String msisdn, String authorization) {
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "rejectedUserTmp");

        Response response = null;
        try {
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(msisdn);
            Optional<UserTmp> oUserTmp = this.userTmpRepository.findFirstByMsisdn(msisdn);
            if (!oUser.isPresent() || !oUserTmp.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, "Usuario no posee cambios");
            }

            UserTmp userTmp = oUserTmp.get();
            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            taskThread.submit(() -> {
                Optional<User> aUser = this.userRepository.findFirstByMsisdn(userTmp.getUpdateUser());
                if (aUser.isPresent()) {
                    User user = aUser.get();
                    notificarUser(userTmp.toPublic(), correlationId, this.globalProperties.getPathRejectedUser(), user.getEmail(), user.getFirstName());
                }
            });
            this.userTmpRepository.delete(oUserTmp.get());

            response = Response.builder()
                    .errorCode(CODE_EXITOSA)
                    .errorMessage(TRANSACCION_EXITOSA)
                    .build();


            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_MODIFY_REJECTED",
                            correlationId,
                            "Ha rechazada la modificacion del usuario " + userTmp.getMsisdn(),
                            this.generateDetailOperationUpdate(oUser.get(), userTmp))
            );


            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);

        } catch (ServiceException e) {
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "rejectedUserTmp");
        return response;
    }


    public Mono<Response> resetPassword(String correlationId, Mono<Request> brequest) {
        AtomicReference<Request> requestAtomicReference = new AtomicReference<>();
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return brequest.flatMap(request -> {
            request.setCorrelationId(correlationId);
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(correlationId);
            log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "resetPassword");
            log.info("{}", "************ STARTED - PROCESS ************");

            requestAtomicReference.set(request);
            return Mono.just(request);
        }).flatMap(obRespUserQueryInfo -> {
            Request request = requestAtomicReference.get();
            asignarCorrelativo(request.getCorrelationId());
            // Verificar el usuario
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(request.getMsisdn());
            if (!oUser.isPresent()) {
                return Mono.error(new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST));
            }
            User user = oUser.get();
            if (user.getStatus().equals(Status.INACTIVE)) {
                return Mono.error(new DataException(NUMBER101, "Su usuario se encuentra inactivo."));
            }

            if (user.getStatus().equals(Status.ALTERED)) {
                return Mono.error(new DataException(NUMBER100, "Su usuario posee problemas, contacte al administrador."));
            }
            userAtomicReference.set(user);
            request.setUser(user.toPublic());
            requestAtomicReference.set(request);

            return Mono.just(request);
        }).flatMap(req -> {
            Request request = requestAtomicReference.get();
            // Validar el OTP
            String source = (request.getSource() == null) ? "support-user" : request.getSource();
            return this.otpHelper.isValid(request.getUser().getCellphone(), request.getOtp(), source);
        }).flatMap(otpResp -> {
            User user = userAtomicReference.get();
            // Verificar si el OTP es válido
            if (!otpResp.isValid()) {
                return Mono.error(new DataException(otpResp.getResponseCode(), otpResp.getResponseMessage()));
            }

            try {
                user.setMpin(cryptoHelper.encoder(requestAtomicReference.get().getNewmpin()));
            } catch (ParsingException e) {
                return Mono.error(new DataException("500", "No se pudo realizar el cambio de clave"));
            }
            user.setDateUpdate(new Date());
            user.setChangePasswordRequired("NO_REQUIRED");
            user.setDateUpdate(new Date());
            user.setSign(user.hashCode());
            // Si OTP es OK, resetear el password
            this.userRepository.save(user);
            Response response = Response.builder()
                    .errorCode(CODE_EXITOSA)
                    .errorMessage("operacion Exitosa")
                    .build();

            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));
            } catch (JsonProcessingException ex) {
                log.error(ex.getMessage());
            }

            log.info(LOG_FORMATED, LOG_COMPONENT, "Response validate OTP: ", "OK");
            log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "resetPassword");
            return Mono.just(response);
        }).onErrorResume(e -> {
            asignarCorrelativo(requestAtomicReference.get().getCorrelationId());

            if (e instanceof DataException) {
                Response response = new Response();
                response.setErrorCode(((DataException) e).getCode());
                response.setErrorMessage(e.getMessage());
                response.setErrorType(ErrorType.DATA.name());
                return Mono.just(response);
            }


            Response response = new Response();
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());
            return Mono.just(response);

        });
    }


    public Response createUser(String correlationId, Request brequest, String authorization) {
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "createUser");

        Response response = new Response();
        try {
            log.info(LOG_FORMATED, LOG_COMPONENT, "Request Create User: ", new ObjectMapper().writeValueAsString(requestMask(brequest, OperationType.INSERT)));
            ValidatorHelper.validationInput(brequest, OperationType.INSERT);

            // SE SETEA EL USUARIO CREADO SIEMPRE EN PENDIENTE
            brequest.getUser().setStatus("PENDING");

            if (userRepository.findFirstByMsisdn(brequest.getUser().getMsisdn()).isPresent()) {
                throw new DataException("101", "Usuario ya existe ");
            }
            if ((brequest.getUser().getMahindraUser() != null) && (!validateUserMahindra(brequest.getUser().getMahindraUser()))) {
                throw new DataException("102", "Usuario no existe en mahindra");
            }

            // VERIFICAR USUARIO CONTRA MAHINDRA SI EXISTE

            User user = new User();
            user.setFirstName(brequest.getUser().getFirstName());
            user.setLastName("");
            user.setMsisdn(brequest.getUser().getMsisdn());
            user.setMpin(cryptoHelper.encoder(brequest.getUser().getMpin()));
            user.setUserType(brequest.getUser().getUserType());
            user.setIdtype(brequest.getUser().getIdtype());
            user.setIdno(brequest.getUser().getIdno());
            user.setGender(Gender.valueOf(brequest.getUser().getGender()));
            user.setDob(brequest.getUser().getDob());
            user.setEmail(brequest.getUser().getEmail());
            user.setCellphone(brequest.getUser().getCellphone());
            user.setMahindraUser(brequest.getUser().getMahindraUser());
            user.setMahindraPassword("");
            user.setChangePasswordRequired("REQUIRED");
            user.setAgentCode("");
            user.setWalletNumber(brequest.getUser().getWalletNumber());
            user.setStatus(Status.valueOf(brequest.getUser().getStatus()));
            user.setGrade(brequest.getUser().getGrade());
            user.setCmlUserId(brequest.getUser().getCmlUserId());
            user.setRegistrationDate(new Date());
            user.setCreateUser(brequest.getUser().getCreateUser());
            user.setSign(user.hashCode());
            this.userRepository.save(user);


            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_CREATE",
                            correlationId,
                            "Ha Creado el usuario " + user.getMsisdn(),
                            this.generateDetailOperation(user))
            );
            response.setErrorCode(CODE_EXITOSA);
            response.setErrorMessage(TRANSACCION_EXITOSA);
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));
            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Request finalBrequest = brequest;
            taskThread.submit(() -> notificar(finalBrequest.getUser(), correlationId, this.globalProperties.getPathAssignPin(), "RISK"));


        } catch (ServiceException e) {
            log.error(LOG_FORMATED, LOG_COMPONENT, "Error creeando usuario ", e.getMessage());
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            log.error(LOG_FORMATED, LOG_COMPONENT, "Error creeando usuario ", e.getMessage());
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "createUser");
        return response;
    }

    public Response deleteUser(String correlationId, Request brequest, String authorization) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "deleteUser");

        Response response = new Response();
        try {
            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(brequest));
            ValidatorHelper.validationInput(brequest, OperationType.DELETE);
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(brequest.getMsisdn());
            if (!oUser.isPresent()) {
                Mono.error(new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST));
            }
            User user = (oUser.isPresent()) ? oUser.get() : new User();
            user.setStatus(Status.INACTIVE);
            user.setDateUpdate(new Date());
            user.setSign(user.hashCode());
            this.userRepository.save(user);
            response.setErrorCode(CODE_EXITOSA);
            response.setErrorMessage(TRANSACCION_EXITOSA);
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));
            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_DELETE",
                            correlationId,
                            "Ha eliminado el usuario " + user.getMsisdn(),
                            this.generateDetailOperation(user))
            );
        } catch (ServiceException e) {
            response = Response.builder()
                    .errorCode(NUMBER100)
                    .errorMessage(e.getMessage())
                    .build();

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "deleteUser");
        return response;
    }

    public Mono<Response> generateOTP(String correlationId, Mono<Request> brequest) {
        AtomicReference<Request> requestAtomicReference = new AtomicReference<>();
        return brequest.flatMap(request -> {
            request.setCorrelationId(correlationId);
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(correlationId);
            log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "generateOTP");
            log.info("{}", "************ STARTED - PROCESS ************");

            requestAtomicReference.set(request);
            return Mono.just(request);

        }).flatMap(obRespUserQueryInfo -> {
            Request request = requestAtomicReference.get();
            asignarCorrelativo(requestAtomicReference.get().getCorrelationId());
            try {

                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(request));
                // Verificar el usuario
                Optional<User> oUser = this.userRepository.findFirstByMsisdn(request.getMsisdn());
                if (!oUser.isPresent()) {
                    return Mono.error(new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST));
                }
                User user = oUser.get();
                if (user.getStatus().equals(Status.ALTERED)) {
                    return Mono.error(new DataException(NUMBER100, "Su usuario posee problemas, contacte al administrador."));
                }
                request.setUser(user.toPublic());
                requestAtomicReference.set(request);
            } catch (JsonProcessingException ex) {
                log.error(ex.getMessage());
            }

            return Mono.just(request);
        }).flatMap(req -> {
            // Generar y notificar el OTP
            Request request = requestAtomicReference.get();
            String origin = (request.getOrigin() == null) ? "support-user" : request.getOrigin();
            return this.otpHelper.generateOTP(request.getUser().getCellphone(), request.getUser().getEmail(), request.getUser().getFirstName(), origin);
        })
                .flatMap(otpResp -> {
                    // Verificar si se pudo crear y enviar el OTP
                    if (!CODE_EXITOSA.equals(otpResp.getResponseCode())) {
                        Mono.error(new DataException(otpResp.getResponseCode(), otpResp.getResponseMessage()));
                    }

                    // Devolver la respuesta del servicio
                    Response response = Response.builder()
                            .errorCode(otpResp.getResponseCode())
                            .errorMessage(otpResp.getResponseMessage())
                            .otp(otpResp.getOtp())
                            .build();

                    try {
                        log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));
                    } catch (JsonProcessingException ex) {
                        log.error(ex.getMessage());
                    }

                    log.info(LOG_FORMATED, LOG_COMPONENT, "Response Generate OTP: ", "OK");
                    log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "generateOTP");
                    return Mono.just(response);
                }).onErrorResume(e -> {
                    log.error(e.getMessage());
                    asignarCorrelativo(requestAtomicReference.get().getCorrelationId());

                    if (e instanceof DataException) {
                        Response response = new Response();
                        response.setErrorCode(((DataException) e).getCode());
                        response.setErrorMessage(e.getMessage());
                        response.setErrorType(ErrorType.DATA.name());
                        return Mono.just(response);
                    }


                    Response response = new Response();
                    response.setErrorCode("99");
                    response.setErrorMessage(e.getMessage());
                    return Mono.just(response);

                });
    }


    public Response findAllUsers(String correlationId) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "findAllUsers");
        Response response = new Response();
        try {
            ArrayList<UserDto> userDtos = new ArrayList<>();
            Iterable<User> myList = this.userRepository.findAll();
            for (User user : myList) {
                userDtos.add(user.toPublic());
            }
            response.setUsers(userDtos);
            response.setErrorCode(CODE_EXITOSA);

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "findAllUsers");
        return response;
    }

    public Response findAllUsersTmp(String correlationId) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "findAllUsersTmp");
        Response response = new Response();
        try {
            ArrayList<UserDto> userDtos = new ArrayList<>();
            Iterable<UserTmp> myList = this.userTmpRepository.findAll();
            for (UserTmp user : myList) {
                userDtos.add(user.toPublic());
            }
            response.setUsers(userDtos);
            response.setErrorCode(CODE_EXITOSA);

        } catch (Exception e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "findAllUsersTmp");
        return response;
    }

    public Response findUser(String correlationId, String msisdn) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "findUser");
        Response response = new Response();
        try {

            Optional<User> oUser = this.userRepository.findFirstByMsisdn(msisdn);
            if (!oUser.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST);
            }
            response.setUser(oUser.get().toPublic());
            response.setErrorCode(CODE_EXITOSA);

        } catch (Exception | ServiceException e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "findUser");
        return response;
    }

    public Response findUserTmp(String correlationId, String msisdn) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "findUserTmp");
        Response response = new Response();
        try {

            Optional<UserTmp> oUserTmp = this.userTmpRepository.findFirstByMsisdn(msisdn);
            if (!oUserTmp.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, "Usuario no tiene modificaciones");
            }

            response.setUser(oUserTmp.get().toPublic());
            response.setErrorCode(CODE_EXITOSA);

        } catch (Exception | ServiceException e) {
            response = Response.builder()
                    .errorCode("99")
                    .errorMessage(e.getMessage())
                    .build();
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "findUserTmp");
        return response;
    }

    public Response changePassword(String correlationId, Request brequest) {

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "changePassword");

        Response response = new Response();
        try {
            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(requestMask(brequest, OperationType.UPDATE_PASSWORD)));

            ValidatorHelper.validationInput(brequest, OperationType.UPDATE_PASSWORD);
            Optional<User> oUser = this.userRepository.findFirstByMsisdnAndMpin(brequest.getMsisdn(), cryptoHelper.encoder(brequest.getMpin()));
            if (!oUser.isPresent()) {
                throw new ServiceException(ErrorType.DATA, NUMBER100, "Credenciales invalidas , no se puede cambiar la contraseña");
            }
            User user = oUser.get();
            user.setMpin(cryptoHelper.encoder(brequest.getNewmpin()));
            user.setDateUpdate(new Date());
            user.setChangePasswordRequired("NO_REQUIRED");
            user.setStatus(Status.ACTIVE);
            user.setSign(user.hashCode());
            this.userRepository.save(user);

            response.setErrorCode(CODE_EXITOSA);
            response.setErrorMessage("Proceso Exitoso");

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));

        } catch (DataException e) {
            log.info(LOG_FORMATED, LOG_COMPONENT, "", e.getMessage());
            response.setErrorCode("10010");
            response.setErrorMessage(e.getMessage());

        } catch (ServiceException e) {
            response.setErrorCode("10011");
            response.setErrorMessage("Credenciales invalidas , no se puede cambiar la contraseña");

        } catch (Exception e) {
            response.setErrorCode("10012");
            if (e.getMessage().contains("invalid reuse of password present in password history")) {
                response.setErrorMessage("Password ya usado anteriormente. Intente con otra clave.");
            } else {
                response.setErrorMessage(e.getMessage());
            }

        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "changePassword");
        return response;
    }

    public Response setPasswordMahindra(String correlationId, Request brequest, String authorization) {
        Response response = new Response();
        Boolean userNew = false;
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "setPasswordMahindra");

        try {
            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, new ObjectMapper().writeValueAsString(requestMask(brequest, OperationType.SET_PASSWORD_MAHINDRA)));

            ValidatorHelper.validationInput(brequest, OperationType.SET_PASSWORD_MAHINDRA);

            // AUTENTICACION CONTRA MAHINDRA
            CommandLoginServiceRequest paramLogin = new CommandLoginServiceRequest();

            // Usuario
            paramLogin.setUserLogin(brequest.getUser().getMahindraUser());
            paramLogin.setPin("*********");
            paramLogin.setSource(brequest.getSource());
            paramLogin.setImei("SUPPORT-USER");

            log.info(FORMAT_LOG2, FLECHA, " Request url: ", this.supportAutenticationProperties.getUrl());
            log.info(FORMAT_LOG, FLECHA, " Request payload", ": \n", this.xmlMapper.writeValueAsString(paramLogin));

            paramLogin.setUserLogin(cryptoHelper.encoder(brequest.getUser().getMahindraUser()));
            paramLogin.setPin(cryptoHelper.encoder(brequest.getUser().getMahindraPassword()));

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            HttpEntity<CommandLoginServiceRequest> entity = new HttpEntity(paramLogin, headers);

            String mhr = restTemplate.postForEntity(
                    this.supportAutenticationProperties.getUrl(), entity, String.class).getBody();

            Response mhResponse = this.xmlMapper.readValue(mhr, Response.class);

            if (mhResponse.getErrorCode().equals(CODE_EXITOSA) || mhResponse.getErrorCode().equals("AUTH_90") || mhResponse.getErrorCode().equals("AUTH_91") || mhResponse.getErrorCode().equals("AUTH_92")) {
                response.setErrorCode(CODE_EXITOSA);
                response.setErrorMessage("Proceso Exitoso");

                Optional<User> oUser = this.userRepository.findFirstByMsisdn(brequest.getUser().getMsisdn());
                if (!oUser.isPresent()) {
                    Mono.error(new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST));
                }
                User user = (oUser.isPresent()) ? oUser.get() : new User();

                if (user.getStatus().equals(Status.PENDING)) {
                    userNew = true;
                }

                // ENCRITAR CLAVE
                user.setMahindraPassword(cryptoHelper.encoder(brequest.getUser().getMahindraPassword()));
                user.setStatus(Status.ACTIVE);
                user.setAgentCode(mhResponse.getUser().getAgentCode());
                user.setLastName(mhResponse.getUser().getLastName());
                user.setDateUpdate(new Date());
                user.setSign(user.hashCode());
                this.userRepository.save(user);

                this.pushAuditService.pushAudit(
                        this.pushAuditService.generarAudit(
                                authorization.split(":")[0],
                                "USER_APPROVED",
                                correlationId,
                                "Ha Aprobado el usuario " + user.getMsisdn(),
                                this.generateDetailOperation(user))
                );
                response.setErrorCode(CODE_EXITOSA);
                response.setErrorMessage(TRANSACCION_EXITOSA);


                envioCorreoNew(userNew, user);


            } else {
                response.setErrorCode("99");
                response.setErrorMessage("Auntenticación frente a mahindra fallida.");
            }

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));

        } catch (DataException e) {
            response.setErrorCode(NUMBER100);
            response.setErrorMessage(e.getMessage());
        } catch (Exception | ParsingException e) {
            log.error(e.getMessage(), e);
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());

        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "setPasswordMahindra");
        return response;
    }

    private void envioCorreoNew(Boolean userNew, User user) throws ParsingException {
        if (userNew.booleanValue()) {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("name", user.getFirstName());
            formData.add(EMAIL, user.getEmail());
            formData.add("infoUser", "");
            formData.add("urlPortal", this.globalProperties.getUrlPortal());
            formData.add("user", user.getMsisdn());
            formData.add("claveTmp", cryptoHelper.decoder(user.getMpin()));
            // Invocar al servicio de envío de correo
            try {
                log.info(URL_ENVIO + this.globalProperties.getUrlServiceSendEmail() + this.globalProperties.getPathRegistryUser());
                this.postEmail(formData, this.globalProperties.getPathRegistryUser());
                log.info(FLECHAIZQ + " Se ha enviado del mensaje EMAIL de forma satisfactoria:");

            } catch (Exception e) {
                log.error(FLECHAIZQ + " Error programando el envío del mensaje Email- Causa: " + e.getMessage());
            }
        }
    }


    public Response activateUser(String correlationId, Request brequest, String authorization) {
        Response response = new Response();
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "activateUser");

        try {
            Optional<User> oUser = this.userRepository.findFirstByMsisdn(brequest.getUser().getMsisdn());
            if (!oUser.isPresent()) {
                Mono.error(new ServiceException(ErrorType.DATA, NUMBER100, USER_MOT_EXIST));
            }
            User user = (oUser.isPresent()) ? oUser.get() : new User();
            // ENCRITAR CLAVE
            user.setStatus(Status.ACTIVE);
            user.setDateUpdate(new Date());
            user.setSign(user.hashCode());
            this.userRepository.save(user);


            envviarCorreeo(user);


            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "USER_APPROVED",
                            correlationId,
                            "Ha Aprobado el usuario " + user.getMsisdn(),
                            this.generateDetailOperation(user))
            );
            response.setErrorCode(CODE_EXITOSA);
            response.setErrorMessage(TRANSACCION_EXITOSA);

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response));

        } catch (Exception | ParsingException e) {
            log.error(e.getMessage(), e);
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());

        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "activateUser");
        return response;
    }

    private void envviarCorreeo(User user) throws ParsingException {
        if (user.getStatus().equals(Status.PENDING)) {
            //Informar por correo
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("name", user.getFirstName());
            formData.add(EMAIL, user.getEmail());
            formData.add("infoUser", "");
            formData.add("urlPortal", this.globalProperties.getUrlPortal());
            formData.add("user", user.getMsisdn());
            formData.add("claveTmp", cryptoHelper.decoder(user.getMpin()));
            // Invocar al servicio de envío de correo
            try {
                log.info(URL_ENVIO + this.globalProperties.getUrlServiceSendEmail() + this.globalProperties.getPathRegistryUser());
                this.postEmail(formData, this.globalProperties.getPathRegistryUser());
                log.info(FLECHAIZQ + " Se ha enviado del mensaje EMAIL de forma satisfactoria:");

            } catch (Exception e) {
                log.error(FLECHAIZQ + " Error programando el envío del mensaje Email- Causa: " + e.getMessage());
            }
        }
    }
    // UTIL METHODS

    private boolean validateUserMahindra(String userMahindra) {
        boolean valido = false;
        try {
            // AUTENTICACION CONTRA MAHINDRA
            CommandUserServiceRequest paramUser = new CommandUserServiceRequest();

            // Usuario
            paramUser.setMsisdn(userMahindra);
            paramUser.setProvider(this.mahindraProperties.getProvider());
            paramUser.setType(this.mahindraProperties.getType());
            paramUser.setUsertype(this.mahindraProperties.getUsertype());

            log.info(FORMAT_LOG2, FLECHA, " Request url: ", this.mahindraProperties.getUrl());

            log.info(FORMAT_LOG, FLECHA, " Request payload", ": \n", this.xmlMapper.writeValueAsString(paramUser));


            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            HttpEntity<CommandUserServiceRequest> entity = new HttpEntity(paramUser, headers);

            String mhr = restTemplate.postForEntity(
                    this.mahindraProperties.getUrl(), entity, String.class).getBody();

            log.info(FORMAT_LOG, "<==", " Response", ": \n", mhr);

            if (mhr.contains("TXNSTATUS>200")) {
                valido = true;
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return valido;
    }

    private void notificar(UserDto userRequest, String correlationId, String path, String perfil) {
        this.asignarCorrelativo(correlationId);
        Iterable<User> users = this.userRepository.findAllByUserTypeAndStatus(perfil, Status.ACTIVE);
        for (User user : users) {
            //Informar por correo
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            // datos usuario risk
            formData.add("name", user.getFirstName());
            formData.add(EMAIL, user.getEmail());

            // datos usuario creado
            formData.add(SupportUsersService.NAME_USER, userRequest.getFirstName());
            formData.add("typeUser", userRequest.getUserType());
            formData.add("cellPone", userRequest.getCellphone());
            formData.add("emailUser", userRequest.getEmail());

            // Invocar al servicio de envío de correo
            try {
                log.info(URL_ENVIO + this.globalProperties.getUrlServiceSendEmail() + path);
                this.postEmail(formData, path);
                log.info(SupportUsersService.EMAIL_SUCCESS);

            } catch (Exception e) {
                log.error(SupportUsersService.EMAIL_ERROR + e.getMessage());
            }
        }
    }


    private void notificarUser(UserDto userRequest, String correlationId, String path, String email, String name) {
        this.asignarCorrelativo(correlationId);
        //Informar por correo
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // datos usuario risk
        formData.add("name", name);
        formData.add(EMAIL, email);

        // datos usuario creado
        formData.add(SupportUsersService.NAME_USER, userRequest.getFirstName());

        // Invocar al servicio de envío de correo
        try {
            log.info(URL_ENVIO + this.globalProperties.getUrlServiceSendEmail() + path);
            this.postEmail(formData, path);
            log.info(SupportUsersService.EMAIL_SUCCESS);

        } catch (Exception e) {
            log.error(SupportUsersService.EMAIL_ERROR + e.getMessage());
        }
    }


    private Request requestMask(Request request, OperationType opType) {

        Request requestMask = new Request(request);
        switch (opType) {
            case LOGIN:
                requestMask.setMpin(PMASK);
                break;

            case UPDATE_PASSWORD:
                requestMask.setMpin(PMASK);
                requestMask.setNewmpin(PMASK);
                break;

            case SET_PASSWORD_MAHINDRA:
                requestMask.setUser(new UserDto(request.getUser()));
                requestMask.getUser().setMahindraPassword(PMASK);
                break;

            case RESET_PASSWORD:
                requestMask.setNewmpin(PMASK);
                break;

            case UPDATE_ALL:
            case INSERT:
                requestMask.setUser(new UserDto(request.getUser()));
                requestMask.getUser().setMpin(PMASK);
                requestMask.getUser().setMahindraPassword(PMASK);
                break;
            default:
                break;
        }

        return requestMask;
    }


    private void asignarCorrelativo(String correlativo) {
        if (correlativo == null || correlativo.isEmpty()) {
            correlativo = UtilidadesHelper.generateCorrelationId();
        }
        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());

    }

    private void postEmail(MultiValueMap<String, String> map, String path) {
        try {
            RestTemplate prestTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity =
                    new HttpEntity<>(map, headers);

            prestTemplate.exchange(this.globalProperties.getUrlServiceSendEmail() + path, HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            log.error(e.getMessage(), e);
            log.error(e.getResponseBodyAsString());
            log.info("Content-type = " + e.getResponseHeaders().getFirst("Content-Type"));
            log.info("Authorization = " + e.getResponseHeaders().getFirst("Authorization"));
            log.info("grant_type = " + e.getResponseHeaders().getFirst("grant_type"));
        }
    }


    private Map<String, String> generateDetailOperation(User user) {
        Map<String, String> detailOperation = new LinkedHashMap<>();
        detailOperation.put("nombre", user.getFirstName());
        detailOperation.put("msisdn", user.getMsisdn());
        detailOperation.put("perfil", user.getUserType());
        detailOperation.put("docIdentidad", user.getIdtype() + "-" + user.getIdno());
        detailOperation.put("genero", user.getGender().name());
        detailOperation.put("dob", user.getDob());
        detailOperation.put("correo", user.getEmail());
        detailOperation.put("celular", user.getCellphone());
        detailOperation.put("usuario Mahindra", user.getMahindraUser());
        detailOperation.put("usuario CML", (user.getCmlUserId() != null) ? user.getCmlUserId().toString() : "");
        return detailOperation;
    }

    private Map<String, String> generateDetailOperationUpdate(User userOld, UserTmp user) {
        Map<String, String> detailOperation = new LinkedHashMap<>();

        try {


            if ((userOld.getIdtype() != null && userOld.getIdno() != null) && (!userOld.getIdtype().equalsIgnoreCase(user.getIdtype())) || (!userOld.getIdno().equalsIgnoreCase(user.getIdno()))) {
                detailOperation.put("docIdentidad anterior", userOld.getIdtype() + "-" + userOld.getIdno());
                detailOperation.put("docIdentidad nuevo", user.getIdtype() + "-" + user.getIdno());
            }
            if ((userOld.getGender() != null) && userOld.getGender() != user.getGender()) {
                detailOperation.put("genero anterior", userOld.getGender().name());
                detailOperation.put("genero nuevo", user.getGender().name());
            }

            if ((userOld.getUserType() != null) && !userOld.getUserType().equalsIgnoreCase(user.getUserType())) {
                detailOperation.put("perfil anterior", userOld.getUserType());
                detailOperation.put("perfil nuevo", user.getUserType());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return generateDetailOperationUpdate1(userOld, user, detailOperation);
    }


    private Map<String, String> generateDetailOperationUpdate1(User userOld, UserTmp user, Map<String, String> detailOperationOld) {
        Map<String, String> detailOperation = detailOperationOld;
        try {

            if ((userOld.getIdtype() != null && userOld.getIdno() != null) && (!userOld.getIdtype().equalsIgnoreCase(user.getIdtype())) || (!userOld.getIdno().equalsIgnoreCase(user.getIdno()))) {
                detailOperation.put("docIdentidad anterior", userOld.getIdtype() + "-" + userOld.getIdno());
                detailOperation.put("docIdentidad nuevo", user.getIdtype() + "-" + user.getIdno());
            }
            if ((userOld.getGender() != null) && userOld.getGender() != user.getGender()) {
                detailOperation.put("genero anterior", userOld.getGender().name());
                detailOperation.put("genero nuevo", user.getGender().name());
            }

            if ((userOld.getDob() != null) && !userOld.getDob().equalsIgnoreCase(user.getDob())) {
                detailOperation.put("dob anterior", userOld.getDob());
                detailOperation.put("dob nuevo", user.getDob());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return generateDetailOperationUpdate2(userOld, user, detailOperation);
    }

    private Map<String, String> generateDetailOperationUpdate2(User userOld, UserTmp user, Map<String, String> detailOperationOld) {
        Map<String, String> detailOperation = detailOperationOld;

        try {

            if ((userOld.getEmail() != null) && !userOld.getEmail().equalsIgnoreCase(user.getEmail())) {
                detailOperation.put("correo anterior", userOld.getEmail());
                detailOperation.put("correo nuevo", user.getEmail());
            }
            if ((userOld.getCellphone() != null) && !userOld.getCellphone().equalsIgnoreCase(user.getCellphone())) {
                detailOperation.put("celular anterior", userOld.getCellphone());
                detailOperation.put("celular nuevo", user.getCellphone());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return generateDetailOperationUpdate3(userOld, user, detailOperation);
    }

    private Map<String, String> generateDetailOperationUpdate3(User userOld, UserTmp user, Map<String, String> detailOperationOld) {
        Map<String, String> detailOperation = detailOperationOld;

        try {
            if ((userOld.getMahindraUser() != null) && !userOld.getMahindraUser().equalsIgnoreCase(user.getMahindraUser())) {
                detailOperation.put("userMahindra anterior", userOld.getMahindraUser());
                detailOperation.put("userMahindra nuevo", user.getMahindraUser());
            }
            if ((userOld.getCmlUserId() != null) && !userOld.getCmlUserId().toString().equals(user.getCmlUserId().toString())) {
                detailOperation.put("userCML anterior", userOld.getCmlUserId().toString());
                detailOperation.put("userCML nuevo", user.getCmlUserId().toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return detailOperation;
    }


}

