package co.moviired.support.service;

import co.moviired.audit.service.PushAuditService;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Generator;
import co.moviired.connector.connector.RestConnector;
import co.moviired.support.domain.client.mahindra.CommandConsultBalanceRequest;
import co.moviired.support.domain.client.mahindra.CommandConsultBalanceResponse;
import co.moviired.support.domain.entity.account.BarExemptionDays;
import co.moviired.support.domain.entity.redshift.Grade;
import co.moviired.support.domain.entity.redshift.StatementAccounts;
import co.moviired.support.domain.repository.redshift.RepositoryStatementAccountFactory;
import co.moviired.support.domain.request.impl.Request;
import co.moviired.support.domain.response.impl.ResponseBarExemptionDays;
import co.moviired.support.domain.response.impl.ResponseConsultBalance;
import co.moviired.support.domain.response.impl.ResponseGrade;
import co.moviired.support.domain.response.impl.ResponseStatementAccounts;
import co.moviired.support.properties.CmdConsultBalanceProperties;
import co.moviired.support.properties.PropertiesFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 */

@Service
@Slf4j
public final class StatementAccountsService implements Serializable {

    private static final int NUMBER_864000000 = 86400000;

    private static final long serialVersionUID = -1143184049994629351L;

    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String ERROR_GENERIC = "400";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String TRANSACCION_EXITOSA = "Transacción exitosa.";
    private static final String REQUIRED_ID = "No se recibe el campo ID del grado.";
    private static final String FORMAT_DATE = "yyyy-mm-dd hh:mm:ss";
    private static final String LOG_COMPONENT = "PROCESS StatementAccounts";
    private static final String LOG_ERROR_PATTERN = " [Error:{}]";
    private final PushAuditService pushAuditService;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final RestConnector mahindraClientRest;
    private final CryptoHelper cryptoHelperAuthorization;
    private final CmdConsultBalanceProperties consultBalanceProperties;
    private final PropertiesFactory propertiesFactory;
    private final RepositoryStatementAccountFactory repositoryStatementAccountFactory;

    public StatementAccountsService(PropertiesFactory ppropertiesFactory,
                                    PushAuditService ppushAuditService,
                                    ObjectMapper pobjectMapper,
                                    RestConnector pmahindraClient,
                                    CryptoHelper pcryptoHelperAuthorization,
                                    CmdConsultBalanceProperties pconsultBalanceProperties,
                                    RepositoryStatementAccountFactory prepositoryStatementAccountFactory) {
        this.propertiesFactory = ppropertiesFactory;
        this.pushAuditService = ppushAuditService;
        this.objectMapper = pobjectMapper;
        this.mahindraClientRest = pmahindraClient;
        this.cryptoHelperAuthorization = pcryptoHelperAuthorization;
        this.consultBalanceProperties = pconsultBalanceProperties;
        this.repositoryStatementAccountFactory = prepositoryStatementAccountFactory;
    }

    public ResponseConsultBalance consultBalance(String pauthorization, String pcorrelationId, String pgrade) {
        ResponseConsultBalance response = ResponseConsultBalance.builder().errorCode(ERROR_GENERIC).errorType("0").build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        String correlationId = this.asignarCorrelativo(pcorrelationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "consultBalance");

        try {


            // validate required and optional fields
            String decodeBase64Authorization = new String(Base64.decodeBase64(pauthorization.getBytes()));
            String[] auth = decodeBase64Authorization.split(":");
            if (auth.length != 2) {
                throw new DataException(ERROR_GENERIC, "Error en parametro de authorización");
            }

            auth[1] = cryptoHelperAuthorization.decoder(auth[1]);

            if (existGradeStatementAccount(pgrade, correlationId)) {
                response = getBalanceToStatementAccount(auth[0], correlationId);
                if ("00".equals(response.getErrorCode())) {
                    return response;
                }
            }

            CommandConsultBalanceRequest request = CommandConsultBalanceRequest.builder()
                    .type(consultBalanceProperties.getTypeSubscriber()).msisdn(auth[0])
                    .provider(consultBalanceProperties.getProvider()).payid(consultBalanceProperties.getPayId())
                    .mpin(auth[1]).build();
            String xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();
            String result = mahindraClientRest.post(xmlRequest, String.class, MediaType.APPLICATION_XML);
            //CONSULTA CONTRA SUBSCRIBER
            CommandConsultBalanceResponse bodyResult = xmlMapper.readValue(result, CommandConsultBalanceResponse.class);
            StatusCode statusCode = this.propertiesFactory.getStatusCodeConfig().of(bodyResult.getTxnStatus(), bodyResult.getMessage());
            if (!StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                //CONSULTA CONTRA CHANNEL
                request.setType(consultBalanceProperties.getTypeChannel());
                xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();
                result = mahindraClientRest.post(xmlRequest, String.class, MediaType.APPLICATION_XML);
                bodyResult = xmlMapper.readValue(result, CommandConsultBalanceResponse.class);
            }
            statusCode = this.propertiesFactory.getStatusCodeConfig().of(bodyResult.getTxnStatus(), bodyResult.getMessage());
            if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                response.setErrorCode(statusCode.getCode());
                response.setBalance(bodyResult.getBalance());
                response.setErrorMessage(statusCode.getMessage());

                response.setErrorMessage(statusCode.getMessage());
                response.setCorrelationId(correlationId);
                response.setTransactionDate(new Date());
                response.setTransactionId(bodyResult.getTxnid());
            } else {
                response.setErrorCode(statusCode.getCode());
                response.setBalance(bodyResult.getBalance());
                response.setErrorMessage(statusCode.getMessage());
                response.setCorrelationId(correlationId);
                response.setTransactionDate(new Date());
            }

        } catch (ServiceException | IOException e) {
            log.error(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());

        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "consultBalance");
        return response;
    }


    public boolean existGradeStatementAccount(String grade, String correlationId) {
        ResponseGrade responseGrade = this.getGrade(correlationId);
        if (!"00".equals(responseGrade.getErrorCode())) {
            return false;
        }

        boolean existe = false;
        for (String gradeString : responseGrade.getGrades()) {
            if (gradeString.equals(grade)) {
                existe = true;
            }
        }

        return existe;
    }

    public ResponseConsultBalance getBalanceToStatementAccount(String username, String correlationId) {
        ResponseConsultBalance response = ResponseConsultBalance.builder().errorCode(ERROR_GENERIC).errorType("0").build();


        ResponseStatementAccounts responseStatementAccounts = this.getStatementAccounts(correlationId, username, false);

        StatusCode statusCode = this.propertiesFactory.getStatusCodeConfig().of(responseStatementAccounts.getErrorCode(), responseStatementAccounts.getErrorMessage());
        response.setErrorCode(statusCode.getCode());
        response.setErrorMessage(statusCode.getMessage());
        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setBalance(responseStatementAccounts.getStatementAccount().getSaldo().toString());
            response.setCorrelationId(correlationId);
            response.setTransactionDate(new Date());
        }


        return response;
    }

    public ResponseGrade getGrade(String correlationId) {
        ResponseGrade response = ResponseGrade.builder().errorCode(ERROR_GENERIC).errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "gradeList");

        ArrayList<Grade> grades = (ArrayList<Grade>) this.repositoryStatementAccountFactory.getGradeRepository().findAll();
        ArrayList<String> gradesString = new ArrayList<>();
        for (Grade grade : grades) {
            gradesString.add(grade.getGrado());
        }
        response.setGrades(gradesString);
        response.setGradeObjs(grades);
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "gradeList");
        return response;
    }

    public ResponseGrade saveGrade(String correlationId, Grade request, String authorization) {
        ResponseGrade response = ResponseGrade.builder().errorCode(ERROR_GENERIC).errorType("0").grade(request.getGrado())
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "saveGrade");

        // CREEAR GRADO
        if (this.repositoryStatementAccountFactory.getGradeRepository().findByGrado(request.getGrado()) != null) {
            response.setErrorCode("01");
            response.setErrorMessage("Ya existe el grado.");
            return response;

        }

        this.repositoryStatementAccountFactory.getGradeRepository().save(request);
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "GRADE_REGISTRY",
                        correlationId,
                        "Ha creado el grado " + request.getGrado(),
                        null)
        );
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "saveGrade");
        return response;
    }


    public ResponseGrade updateGrade(String correlationId, Grade request, String authorization) {
        ResponseGrade response = ResponseGrade.builder().errorCode(ERROR_GENERIC).errorType("0").grade(request.getGrado())
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "updateGrade");


        if (request.getId() == null) {
            response.setErrorCode("03");
            response.setErrorMessage(REQUIRED_ID);
            return response;
        }

        // MODIFICAR GRADO

        Optional<Grade> oGrade = this.repositoryStatementAccountFactory.getGradeRepository().findById(request.getId());
        if (!oGrade.isPresent()) {
            response.setErrorCode("02");
            response.setErrorMessage("No existe el grado.");
            return response;
        }
        Grade grade = oGrade.get();
        String oldGrade = grade.getGrado();
        grade.setGrado(request.getGrado());
        this.repositoryStatementAccountFactory.getGradeRepository().save(grade);

        response.setErrorCode("01");
        response.setErrorMessage("update exitoso.");

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "GRADE_MODIFY",
                        correlationId,
                        "Ha modificado el grado " + oldGrade + " por " + grade.getGrado(),
                        null)
        );
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "updateGrade");
        return response;
    }

    public ResponseGrade deleteGrade(String correlationId, Grade request, String authorization) {
        ResponseGrade response = ResponseGrade.builder().errorCode(ERROR_GENERIC).errorType("0").grade(request.getGrado())
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "deleteGrade");


        if (request.getId() == null) {
            response.setErrorCode("03");
            response.setErrorMessage(REQUIRED_ID);
            return response;
        }

        try {
            if (this.repositoryStatementAccountFactory.getGradeRepository().findByGrado(request.getGrado()) == null) {
                throw new NoResultException();
            }

            this.repositoryStatementAccountFactory.getGradeRepository().delete(request);

            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "GRADE_DELETE",
                            correlationId,
                            "Ha eliminado el grado " + request.getGrado(),
                            null)
            );
        } catch (NoResultException e) {
            response.setErrorCode("02");
            response.setErrorMessage("No existe el grado.");
            return response;
        }

        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "deleteGrade");
        return response;
    }

    // DIAS EXENTOS DE BLOQUEO

    public ResponseBarExemptionDays getExemptionDays(String correlationId) {
        ResponseBarExemptionDays response = ResponseBarExemptionDays.builder().errorCode(ERROR_GENERIC).errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "getExemptionDays");

        ArrayList<BarExemptionDays> barExemptionDays = (ArrayList<BarExemptionDays>) this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().findAll();
        response.setBarExemptionDays(barExemptionDays);
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "getExemptionDays");
        return response;
    }

    public ResponseBarExemptionDays saveExemptionDays(String correlationId, Request request, String authorization) {
        ResponseBarExemptionDays response = ResponseBarExemptionDays.builder().errorCode(ERROR_GENERIC).errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "saveExemptionDays");

        // CREEAR GRADO
        BarExemptionDays day = this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().findByDay(request.getExemptionDay().getDay());
        if (day != null) {
            response.setErrorCode("01");
            response.setErrorMessage("Ya existe el dia exento.");
            return response;
        }

        this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().save(request.getExemptionDay());

        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);

        Date date = request.getExemptionDay().getDay().getTime();
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        String strDate = dateFormat.format(date);

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "EXEMPTDAY_REGISTRY",
                        correlationId,
                        "Ha registrado un dia excento " + strDate,
                        null)
        );
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "saveExemptionDays");
        return response;
    }


    public ResponseBarExemptionDays updateExemptionDays(String correlationId, Request request, String authorization) {
        ResponseBarExemptionDays response = ResponseBarExemptionDays.builder().errorCode(ERROR_GENERIC).errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "updateExemptionDays");


        if (request.getExemptionDay().getId() == null) {
            response.setErrorCode("03");
            response.setErrorMessage(REQUIRED_ID);
            return response;
        }

        // MODIFICAR GRADO
        Optional<BarExemptionDays> oDay = this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().findById(request.getExemptionDay().getId());

        if (oDay.isPresent()) {
            BarExemptionDays day = oDay.get();
            day.setDay(request.getExemptionDay().getDay());
            this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().save(day);
        } else {
            response.setErrorCode("02");
            response.setErrorMessage("No existe el dia exento.");
            return response;
        }

        Date date = request.getExemptionDay().getDay().getTime();
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        String strDate = dateFormat.format(date);

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "EXEMPTDAY_MODIFY",
                        correlationId,
                        "Ha modificado un dia excento " + strDate,
                        null)
        );
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "updateExemptionDays");
        return response;
    }

    public ResponseBarExemptionDays deleteExemptionDays(String correlationId, Request request, String authorization) {
        ResponseBarExemptionDays response = ResponseBarExemptionDays.builder().errorCode(ERROR_GENERIC).errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "deleteExemptionDays");


        if (request.getExemptionDay().getId() == null) {
            response.setErrorCode("03");
            response.setErrorMessage("No se recibe el campo ID del dia exento.");
            return response;
        }

        Optional<BarExemptionDays> oDay = this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().findById(request.getExemptionDay().getId());
        if (!oDay.isPresent()) {
            response.setErrorCode("02");
            response.setErrorMessage("No existe el dia exento.");
            return response;
        }

        this.repositoryStatementAccountFactory.getBarExemptionDaysRepository().delete(oDay.get());
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);

        Date date = oDay.get().getDay().getTime();
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE);
        String strDate = dateFormat.format(date);

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "EXEMPTDAY_DELETE",
                        correlationId,
                        "Ha eliminado un dia excento " + strDate,
                        null)
        );
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "deleteExemptionDays");
        return response;
    }

    // ESTADO DE CUENTA

    public ResponseStatementAccounts getStatementAccounts(String correlationId, String phone, boolean envioCorreo) {
        ResponseStatementAccounts response = ResponseStatementAccounts.builder().errorCode("99").errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "StatementAccounts");

        ArrayList<StatementAccounts> statementAccounts = (ArrayList<StatementAccounts>) this.repositoryStatementAccountFactory.getStatementAccountsRepository().findByCelular(phone);
        if (statementAccounts.isEmpty()) {
            response.setErrorCode("96");
            response.setErrorMessage("Usuario no existe para estado de cuenta");
            return response;
        }

        if (!statementAccounts.isEmpty() && statementAccounts.size() > 1 && envioCorreo) {
            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            taskThread.submit(() -> this.sendEmailRiskData(phone));
            response.setErrorCode("95");
            response.setErrorMessage("Problemas con su Usuario y  el estado de cuenta");
            return response;
        }

        response.setStatementAccount(statementAccounts.get(0));
        //Personalizar estado de cartera.
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date today = formateador.parse(formateador.format(new Date()));
            Date expiration = response.getStatementAccount().getFechaBloqueo();
            if (expiration == null) {
                expiration = today;
            }
            int dias = (int) ((today.getTime() - expiration.getTime()) / NUMBER_864000000);

            if (dias >= this.propertiesFactory.getGlobalProperties().getPreJuridicoMin() && dias <= this.propertiesFactory.getGlobalProperties().getPreJuridicoMax()) {
                response.getStatementAccount().setEstadoCartera("PreJurídico");
            }
            if (dias > this.propertiesFactory.getGlobalProperties().getPreJuridicoMax()) {
                response.getStatementAccount().setEstadoCartera("Jurídico");
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "StatementAccounts");
        return response;
    }


    private String asignarCorrelativo(String pcorrelativo) {
        String correlativo = pcorrelativo;
        if (correlativo == null || correlativo.isEmpty()) {
            correlativo = String.valueOf(Generator.correlationId());
        }

        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.propertiesFactory.getGlobalProperties().getApplicationName());
        return correlativo;
    }


    private void sendEmailRiskData(String phone) {
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            log.info("{} {} {}", "==>", " Request url: ", this.propertiesFactory.getGlobalProperties().getUrlServiceSendEmail() + this.propertiesFactory.getGlobalProperties().getPathUserWarning());

            //Informar por correo
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("email", this.propertiesFactory.getGlobalProperties().getEmailRiskData());
            formData.add("phone", phone);

            // Invocar al servicio de envío de correo
            this.postEmail(formData, this.propertiesFactory.getGlobalProperties().getPathUserWarning());
            log.info("<== Se ha enviado del mensaje EMAIL de forma satisfactoria:");

        } catch (Exception e) {
            log.error("<== Error programando el envío del mensaje Email- Causa: " + e.getMessage());
        }
    }

    public void postEmail(MultiValueMap<String, String> map, String path) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity =
                    new HttpEntity<>(map, headers);

            log.info(this.propertiesFactory.getGlobalProperties().getUrlServiceSendEmail() + path);
            restTemplate.exchange(this.propertiesFactory.getGlobalProperties().getUrlServiceSendEmail() + path, HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            log.info("Content-type = " + e.getResponseHeaders().getFirst("Content-Type"));
            log.info("Authorization = " + e.getResponseHeaders().getFirst("Authorization"));
            log.info("grant_type = " + e.getResponseHeaders().getFirst("grant_type"));
        }
    }

}

