package co.moviired.support.otp.service;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-29
 * @since 1.0
 */

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import co.moviired.support.otp.conf.StatusCodeConfig;
import co.moviired.support.otp.exception.ExpiredOtpException;
import co.moviired.support.otp.exception.InvalidOtpAttempsException;
import co.moviired.support.otp.helper.OtpHelper;
import co.moviired.support.otp.model.dto.Request;
import co.moviired.support.otp.model.dto.Response;
import co.moviired.support.otp.model.entity.Otp;
import co.moviired.support.otp.model.enums.NotifyChannel;
import co.moviired.support.otp.model.enums.OtpState;
import co.moviired.support.otp.notifier.INotifier;
import co.moviired.support.otp.notifier.NotifierFactory;
import co.moviired.support.otp.properties.OtpProperties;
import co.moviired.support.otp.properties.SmsProperties;
import co.moviired.support.otp.repository.IOtpRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OtpService implements Serializable {

    private static final String LBL_START = "[{}] TRANSACCIÓN INICIADA";
    private static final String LBL_END = "[{}] TRANSACCIÓN FINALIZADA";
    private static final String LBL_REQUEST = "[{}] REST REQUEST  - Value [{}]";
    private static final String LBL_REQUEST_TYPE = "[{}] REST REQUEST  - Type  [{}]";
    private static final String LBL_RESPONSE = "[{}] REST RESPONSE - Value [{}]";
    private static final String LBL_EXPIRE_OTP = "[{}] Número de OTP expiradas: {}";
    private static final String LBL_VALIDATE_OTP_ERROR = "[{}] Validate OTP: FAILED. Cause: {}";

    private static final String ERR_OTP_NOT_FOUND = "NO SE ENCONTRÓ LA OTP SUMINISTRADA";
    private static final String ERR_OTP_EXPIRED = "LA OTP INDICADA ESTÁ EXPIRADA";

    private final SmsProperties smsProperties;
    private final OtpProperties otpProperties;
    private final OtpHelper otpHelper;
    private final StatusCodeConfig statusCodeConfig;
    private final IOtpRepository otpRepository;
    private final ObjectWriter jsonWriter;

    private final NotifierFactory notifierFactory;

    public OtpService(@NotNull OtpHelper otpHelper,
                      @NotNull StatusCodeConfig statusCodeConfig,
                      @NotNull IOtpRepository otpRepository,
                      @NotNull OtpProperties otpProperties,
                      @NotNull SmsProperties smsProperties,
                      @NotNull NotifierFactory notifierFactory) {
        super();
        this.otpHelper = otpHelper;
        this.statusCodeConfig = statusCodeConfig;
        this.otpRepository = otpRepository;
        this.otpProperties = otpProperties;
        this.smsProperties = smsProperties;
        this.notifierFactory = notifierFactory;
        this.jsonWriter = new ObjectMapper().writer();
    }

    // SERVICE METHODS

    // Verificar si el servicio está Activo
    Mono<String> ping() {
        // Generar el identificador único de operación
        String uuidOperation = UUID.randomUUID().toString().replace("-", "");

        // Respuesta
        String response = "OK";

        // Ejecutar la operación
        log.info("");
        log.info(LBL_START, uuidOperation);
        log.info(LBL_REQUEST_TYPE, uuidOperation, "PING");
        log.info(LBL_REQUEST, uuidOperation, "-");
        log.info(LBL_RESPONSE, uuidOperation, response);
        log.info(LBL_END, uuidOperation);
        log.info("");

        return Mono.just(response);
    }

    // Expirar los Otp vencidos
    synchronized void expireOtps() {
        final String uuidOperation = UUID.randomUUID().toString().replace("-", "");

        try {
            // Establecer la fecha de expiración
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSSS");

            // LOGS
            log.info("");
            log.info(LBL_START, uuidOperation);
            log.info(LBL_REQUEST_TYPE, uuidOperation, "JOB - Expire OTP");
            log.info(LBL_REQUEST, uuidOperation, sdf.format(currentDate));

            // Expirar las OTP
            int numberExpired = this.otpRepository.expiredOtps(currentDate);
            log.info(LBL_EXPIRE_OTP, uuidOperation, numberExpired);

        } catch (Exception e) {
            log.error("[{}] JOB - Expire OTP: Falló. Causa: {}", uuidOperation, e.getMessage(), e);

        } finally {
            log.info(LBL_END, uuidOperation);
            log.info("");
        }
    }


    // Generar el OTP
    Mono<Response> generate(@NotNull Mono<Request> request) {
        return request.flatMap(req -> {
            Response response = new Response();
            final String uuidOperation = UUID.randomUUID().toString().replace("-", "");
            try {
                // LOGS
                log.info("");
                log.info(LBL_START, uuidOperation);
                log.info(LBL_REQUEST_TYPE, uuidOperation, "Generate OTP");
                log.info(LBL_REQUEST, uuidOperation, Security.printIgnore(this.jsonWriter.writeValueAsString(req), "otp"));

                // Generar la OTP
                String encriptedOtp = this.otpHelper.generate(req.getOtpLength(), req.getOtpAlphanumeric());

                // Generar la fecha de expiracion
                int expiration = this.otpProperties.getDefaultExpirationLapse();
                Calendar expirationDate = Calendar.getInstance();
                if (req.getOtpExpiration() != null) {
                    expiration = req.getOtpExpiration();
                }
                expirationDate.add(Calendar.MINUTE, expiration);

                // SMS templateCode
                String templateCode = req.getTemplateCode();
                if (templateCode == null) {
                    templateCode = smsProperties.getSmsDefaultTemplateCode();
                }

                // Guardar la OTP en BD
                Otp otp = Otp.builder()
                        .component(req.getComponent())
                        .phoneNumber(req.getPhoneNumber())
                        .email(req.getEmail())
                        .value(encriptedOtp)
                        .expirationDate(expirationDate.getTime())
                        .expirationLapse(expiration)
                        .templateCode(templateCode)
                        .origin(req.getOrigin())
                        .variables(req.getVariables())
                        .build();

                // Validar el número de OTP generadas
                verifyCreationAttemps(uuidOperation, req.getOrigin(), otp);

                // Guardar la OTP
                otp = this.otpRepository.save(otp);

                // Notificar la OTP
                if (req.isSendSms()) {
                    if (req.getNotifyChannel() == null) {
                        req.setNotifyChannel(NotifyChannel.SMS);
                    }
                    log.debug(req.getNotifyChannel().value());
                    INotifier notifier = this.notifierFactory.get(req.getNotifyChannel());
                    notifier.notify(uuidOperation, otp);
                }

                // Establecer que se generó OK la OTP
                StatusCode statusCode = statusCodeConfig.of("00");
                response.setOtp(encriptedOtp);
                response.setResponseCode(statusCode.getCode());
                response.setResponseMessage(statusCode.getMessage());

            } catch (Exception | ParsingException e) {
                log.error("[{}] Generate OTP: FAILED. Cause: {}", uuidOperation, e.getMessage());

                // Generar la respuesta errada
                StatusCode statusCode = statusCodeConfig.of("99");
                response.setResponseCode(statusCode.getCode());
                response.setResponseMessage(statusCode.getMessage());

            } finally {
                try {
                    log.info(LBL_RESPONSE, uuidOperation, this.jsonWriter.writeValueAsString(response));
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
                log.info(LBL_END, uuidOperation);
                log.info("");
            }

            return Mono.just(response);
        });
    }

    // Validar el OTP
    Mono<Response> validate(@NotNull Mono<Request> request) {
        return request.flatMap(
                req -> {
                    Response response = new Response();
                    final String uuidOperation = UUID.randomUUID().toString().replace("-", "");
                    try {
                        // LOGS
                        log.info("");
                        log.info(LBL_START, uuidOperation);
                        log.info(LBL_REQUEST_TYPE, uuidOperation, "Validate OTP");
                        log.info(LBL_REQUEST, uuidOperation, Security.printIgnore(this.jsonWriter.writeValueAsString(req), "otp"));

                        // Buscar el OTP en la BD: encriptada y pendiente
                        Optional<Otp> optionalOtp = this.otpRepository.findTopByComponentAndPhoneNumberAndStateOrderByCreationDateDesc(req.getComponent(), req.getPhoneNumber(), OtpState.PENDING);

                        // Verificar si la OTP es valida
                        if (!optionalOtp.isPresent()) {
                            throw new InvalidObjectException(ERR_OTP_NOT_FOUND);
                        }
                        Otp otp = optionalOtp.get();

                        // Verificar que no esté expirada
                        Calendar expirationDate = Calendar.getInstance();
                        Date actual = expirationDate.getTime();
                        if (otp.getExpirationDate().before(actual)) {
                            // Invocar al método que expira OTPS
                            int numberExpired = this.otpRepository.expiredOtps(expirationDate.getTime());
                            log.info(LBL_EXPIRE_OTP, uuidOperation, numberExpired);
                            throw new ExpiredOtpException(ERR_OTP_EXPIRED);
                        }

                        // Verificar que la OTP corresponda
                        String encriptedOtp = this.otpHelper.encryptOtp(req.getOtp());
                        if (!encriptedOtp.equals(otp.getValue())) {
                            // Validar los reintentos de fallida
                            verifyValidateAttemps(uuidOperation, req.getOrigin(), otp);
                            throw new InvalidObjectException(ERR_OTP_NOT_FOUND);
                        }

                        // Marcar como validada la OTP
                        otp.setState(OtpState.USED);
                        otp.setValidationDate(actual);
                        otp.setModificationDate(actual);
                        this.otpRepository.save(otp);

                        //Devolver que la OTP es válida
                        StatusCode statusCode = statusCodeConfig.of("00");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(statusCode.getMessage());
                        response.setValid(Boolean.TRUE);

                    } catch (InvalidOtpAttempsException e) {
                        log.error(LBL_VALIDATE_OTP_ERROR, uuidOperation, e.getMessage());

                        // Generar la respuesta errada
                        StatusCode statusCode = statusCodeConfig.of("98");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(e.getMessage());
                        response.setValid(Boolean.FALSE);

                    } catch (ExpiredOtpException e) {
                        log.error(LBL_VALIDATE_OTP_ERROR, uuidOperation, e.getMessage());

                        // Generar la respuesta errada
                        StatusCode statusCode = statusCodeConfig.of("97");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(e.getMessage());
                        response.setValid(Boolean.FALSE);

                    } catch (Exception | ParsingException e) {
                        log.error(LBL_VALIDATE_OTP_ERROR, uuidOperation, e.getMessage());

                        // Generar la respuesta errada
                        StatusCode statusCode = statusCodeConfig.of("96");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(e.getMessage());

                    } finally {
                        try {
                            log.info(LBL_RESPONSE, uuidOperation, this.jsonWriter.writeValueAsString(response));
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                        }
                        log.info(LBL_END, uuidOperation);
                        log.info("");
                    }

                    return Mono.just(response);
                });
    }

    // Reenviar el OTP
    Mono<Response> resend(@NotNull Mono<Request> request) {
        Response response = new Response();
        return request.flatMap(
                req -> {

                    final String uuidOperation = UUID.randomUUID().toString().replace("-", "");
                    try {
                        // LOGS
                        log.info("");
                        log.info(LBL_START, uuidOperation);
                        log.info(LBL_REQUEST_TYPE, uuidOperation, "Resend OTP");
                        log.info("[{}] CHANNEL: [{}]", uuidOperation, req.getNotifyChannel());
                        log.info(LBL_REQUEST, uuidOperation, Security.printIgnore(this.jsonWriter.writeValueAsString(req), "otp"));

                        // Buscar el OTP en la BD: encriptada y pendiente
                        Optional<Otp> optionalOtp = this.otpRepository.findTopByComponentAndPhoneNumberAndStateOrderByCreationDateDesc(req.getComponent(), req.getPhoneNumber(), OtpState.PENDING);

                        // Verificar si no se encuentra OTP generada, se genera una nueva
                        if (!optionalOtp.isPresent()) {
                            throw new InvalidObjectException(ERR_OTP_NOT_FOUND);
                        }
                        Otp otp = optionalOtp.get();

                        // Verificar que no esté expirada
                        Calendar expirationDate = Calendar.getInstance();
                        Date actual = expirationDate.getTime();
                        if (otp.getExpirationDate().before(actual)) {
                            // Invocar al método que expira OTPS
                            int numberExpired = this.otpRepository.expiredOtps(expirationDate.getTime());
                            log.info(LBL_EXPIRE_OTP, uuidOperation, numberExpired);
                            throw new InvalidObjectException(ERR_OTP_EXPIRED);
                        }

                        // Notificar la OTP
                        INotifier notifier = this.notifierFactory.get(req.getNotifyChannel());
                        notifier.notify(uuidOperation, otp);

                        // Establecer que se generó OK la OTP
                        StatusCode statusCode = statusCodeConfig.of("00");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(statusCode.getMessage());

                    } catch (InvalidObjectException e) {
                        log.error("[{}] Resend OTP: FAILED. Cause: {}", uuidOperation, e.getMessage());

                        // Generar la respuesta errada
                        StatusCode statusCode = statusCodeConfig.of("00");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(e.getMessage());
                        response.setValid(Boolean.FALSE);

                    } catch (Exception e) {
                        log.error("[{}] Resend OTP: FAILED. Cause: {}", uuidOperation, e.getMessage());

                        // Generar la respuesta errada
                        StatusCode statusCode = statusCodeConfig.of("99");
                        response.setResponseCode(statusCode.getCode());
                        response.setResponseMessage(e.getMessage());

                    } finally {
                        try {
                            log.info(LBL_RESPONSE, uuidOperation, this.jsonWriter.writeValueAsString(response));
                        } catch (JsonProcessingException e) {
                            log.error(e.getMessage(), e);
                        }
                        log.info(LBL_END, uuidOperation);
                        log.info("");
                    }

                    return Mono.just(response);

                }).onErrorResume(e -> Mono.just(response));
    }

    // UTILS METHODS

    // Verificar el número de intentos de validación del OTP
    private void verifyValidateAttemps(@NotNull String uuid, @NotNull String origin, @NotNull Otp otp) throws InvalidOtpAttempsException {
        // Tope de intentos d evalidación
        int tope = otpProperties.getMoviiValidateAttemps();
        if (origin.equals(otpProperties.getMoviiredOrigin())) {
            tope = otpProperties.getMoviiredValidateAttemps();
        }

        // Validar el tope de intentos
        int nroAttemps = otp.getValidationAttemps();
        if (nroAttemps > tope) {
            otp.setState(OtpState.INVALIDATED);
            otp.setModificationDate(new Date());
            log.error("[{}] OTP invalidado por número de intentos fallidos. Actual: {} - Máximo: {}", uuid, nroAttemps, tope);
            // actualizar el OTP en BD
            otp.setValidationAttemps(++nroAttemps);
            this.otpRepository.save(otp);

            throw new InvalidOtpAttempsException("OTP INVALIDADA POR NÚMERO DE INTENTOS FALLIDOS");
        }

        // actualizar el OTP en BD
        otp.setValidationAttemps(++nroAttemps);
        this.otpRepository.save(otp);

    }

    // Verificar el número de intentos de validación del OTP
    private void verifyCreationAttemps(@NotNull String uuid, @NotNull String origin, @NotNull Otp otp) throws InvalidObjectException {
        // Tope de intentos d evalidación
        int tope = otpProperties.getMoviiGenerateByDay();
        if (origin.equals(otpProperties.getMoviiredOrigin())) {
            tope = otpProperties.getMoviiredGenerateByDay();
        }

        // Obtener el número de intentos
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 59);
        int nroAttemps = this.otpRepository.countByComponentAndPhoneNumberAndDate(otp.getComponent(), otp.getPhoneNumber(), startDate.getTime(), endDate.getTime());

        // Validar el tope de intentos
        if (nroAttemps > tope) {
            log.error("[{}] Creación invalidada por número de intentos alcanzado. Component: {}, Origin: {}, PhoneNumber: {}", uuid, otp.getComponent(), origin, otp.getPhoneNumber());
            throw new InvalidObjectException("Límite de generación OTP alcanzado para la fecha");
        }
    }

}

