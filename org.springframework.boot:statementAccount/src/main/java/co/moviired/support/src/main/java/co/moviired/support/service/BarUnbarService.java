package co.moviired.support.service;

import co.moviired.audit.service.PushAuditService;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.util.Generator;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.properties.GlobalProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.domain.client.mahindra.CommandBarUnbarRequest;
import co.moviired.support.domain.client.mahindra.Response;
import co.moviired.support.domain.dto.UnbarBarHistoryDTO;
import co.moviired.support.domain.entity.account.BarExemptionDays;
import co.moviired.support.domain.entity.account.BarTemplate;
import co.moviired.support.domain.entity.account.BarUnbarConfig;
import co.moviired.support.domain.entity.account.UnbarBarHistory;
import co.moviired.support.domain.entity.redshift.StatementAccounts;
import co.moviired.support.domain.enums.HistoryType;
import co.moviired.support.domain.repository.account.RepositoryBarFactory;
import co.moviired.support.domain.repository.redshift.RepositoryStatementAccountFactory;
import co.moviired.support.domain.response.impl.ResponseBarTemplate;
import co.moviired.support.domain.response.impl.ResponseBarUnbarHistory;
import co.moviired.support.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 */

@Service
@Slf4j
public final class BarUnbarService {

    public static final String LOGS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String EMPTY_STRING = "";
    public static final String LBL_START = "STARTING TRANSACTION";
    public static final String LBL_END = "END TRANSACTION";
    public static final String LBL_REQUEST_TYPE = "REQUEST  - Type  [{}]";
    public static final String LOG_JOB_BAR = "JOB - Bar account";
    public static final String LOG_JOB_UNBAR = "JOB - unBar account";
    public static final String LBL_REQUEST = "REQUEST  - Value [{}]";
    public static final String LOG_FAIL_JOB_BAR = "JOB - Bar account: Fail. cause: {}";
    private static final String TRANSACCION_EXITOSA = "Transacción exitosa.";
    private static final String BAR = "BAR";
    private static final String UNBAR = "UNBAR";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS BarUnbar";

    private final GlobalProperties globalProperties;
    private final ReactiveConnector mahindraClient;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final StatusCodeConfig statusCodeConfig;

    private final PushAuditService pushAuditService;
    private final RepositoryBarFactory repositoryBarFactory;
    private final RepositoryStatementAccountFactory repositoryStatementAccountFactory;

    public BarUnbarService(GlobalProperties pglobalProperties,
                           ReactiveConnector pmahindraClient,
                           @Qualifier("statusCodeConfig") StatusCodeConfig pstatusCodeConfig,
                           PushAuditService ppushAuditService,
                           RepositoryBarFactory repositoryBarFactory,
                           RepositoryStatementAccountFactory repositoryStatementAccountFactory) {
        this.globalProperties = pglobalProperties;
        this.mahindraClient = pmahindraClient;
        this.statusCodeConfig = pstatusCodeConfig;
        this.pushAuditService = ppushAuditService;
        this.repositoryBarFactory = repositoryBarFactory;
        this.repositoryStatementAccountFactory = repositoryStatementAccountFactory;
    }
    // JOB *************************************************************************************************************

    /**
     * BLOQUEO DE USUARIOS POR MONTO MINIMO A PAGAR MENOR AL LIMITE DE PAGO MINIMO DEL TEMPLATE
     */
    public synchronized void barAccount(BarUnbarConfig config) {
        AtomicReference<Integer> accountBar = new AtomicReference<>(0);
        String correlativo = asignarCorrelativo(null);
        try {
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(LOGS_DATE_FORMAT);
            log.info(EMPTY_STRING);
            log.info(LBL_START);
            log.info(LBL_REQUEST_TYPE, LOG_JOB_BAR);
            log.info(LBL_REQUEST, sdf.format(currentDate));

            // A. VERIFICA DIAS EXCENTOS.
            validateBarExemptionDays();
            log.info("Es un dia no excento.");

            // B. CONSULTA CUENTAS DEESBLOQUEADAS
            ArrayList<StatementAccounts> statementAccountsUnbar = (ArrayList<StatementAccounts>) this.repositoryStatementAccountFactory.getStatementAccountsRepository().findByEstadoCarteraAndTipoBloqueoIsNotNullAndTipoBloqueoIsNot(BarUnbarService.UNBAR, "");
            log.info("Se consiguieron " + statementAccountsUnbar.size() + " a evaluar.");
            statementAccountsUnbar.forEach(account -> {
                asignarCorrelativo(null);

                printDebugAccount(account);

                try {
                    // D. POR USUARIO BUSCA POR TIPO DE BLOQUEO EL TEMPLATE
                    BarTemplate template = this.repositoryBarFactory.getBarTemplateRepository().findByBarType(account.getTipoBloqueo());

                    // E. VERIFICA EXISTE TEMPLATE Y SI TIENE ENABLE_TEMPLATE TRUE
                    validateEnabledTemplate(template);

                    printDebugTemplate(template);

                    // F. SI HOY ES DIA DE BLOQUEO
                    validateBarDayAndHours(template);
                    log.debug("ES HORA y FECHA DE BLOQUEO");

                    // G. SI EL VALOR MINIMO A PAGAR ES MENOR AL LIMITE DEL TEMPLATE
                    if (validateBarLimitAmount(template, account)) {


                        // H. INVOCA A MAHINDRA PARA BLOQUEEAR.
                        CommandBarUnbarRequest request = generarRequest(account, BarUnbarService.BAR);
                        String xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();
                        log.debug("==> REQUEST: " + xmlRequest);
                        String result = (String) mahindraClient.post(xmlRequest, String.class, MediaType.APPLICATION_XML, null).block();
                        log.debug("<== RESPONSE: " + result);
                        Response bodyResult = xmlMapper.readValue(result, Response.class);

                        StatusCode statusCode = statusCodeConfig.of(bodyResult.getTxnstatus(), bodyResult.getMessage());
                        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                            accountBar.set(accountBar.get() + 1);
                            // I. GUARDAR HISTORIAL
                            saveBarUnbarHistory(account, HistoryType.BAR);
                        }
                    }

                } catch (BarNotDaysException | BarNotHoursException e) {
                    log.debug("Fecha y hora: AUN NO");

                } catch (BarDisabledTemplateException e) {
                    log.debug("Template no existe o deshabilitado");
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }
            });
        } catch (BarExemptionDaysException e) {
            log.info(LOG_FAIL_JOB_BAR, e.getMessage());
        } finally {
            asignarCorrelativo(correlativo);
            config.setRunningBar(false);
            this.repositoryBarFactory.getBarUnbarConfigRepository().save(config);
            log.info("Se bloquearon " + accountBar.get());
            log.info(LBL_END);
            log.info(EMPTY_STRING);
        }

    }


    /**
     * DESBLOQUEO DE USUARIOS POR MONTO MINIMO A PAGAR MAYOR AL LIMITE DE PAGO MINIMO DEL TEMPLATE
     */
    public synchronized void unBarAccount(BarUnbarConfig config) {
        AtomicReference<Integer> accountUnBar = new AtomicReference<>(0);

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(LOGS_DATE_FORMAT);
        String correlativo = asignarCorrelativo(null);
        log.info(EMPTY_STRING);
        log.info(LBL_START);
        log.info(LBL_REQUEST_TYPE, LOG_JOB_UNBAR);
        log.info(LBL_REQUEST, sdf.format(currentDate));

        // A. CONSULTA CUENTAS BLOQUEADAS
        ArrayList<StatementAccounts> statementAccountsUnbar = (ArrayList<StatementAccounts>) this.repositoryStatementAccountFactory.getStatementAccountsRepository().findByEstadoCartera(BarUnbarService.BAR);
        log.info("Se consiguieron " + statementAccountsUnbar.size() + " a evaluar.");

        statementAccountsUnbar.parallelStream().forEach(account -> {
            asignarCorrelativo(null);
            try {

                printDebugAccount(account);

                // B. POR USUARIO BUSCA POR TIPO DE BLOQUEO EL TEMPLATE
                BarTemplate template = this.repositoryBarFactory.getBarTemplateRepository().findByBarType(account.getTipoBloqueo());

                if (template != null) {
                    printDebugTemplate(template);
                } else {
                    log.debug("         No existe temple");
                }

                // C. SEE VERIFICA SI EL TEEMPLATEE PEERMITEE DESBLOQUEAR
                validateEnabledUnbar(template);

                // D. SI EL VALOR MINIMO A PAGAR ES MENOR AL LIMITE DEL TEMPLATE
                if (template == null || !validateBarLimitAmount(template, account)) {

                    // D. INVOCA A MAHINDRA PARA BLOQUEEAR.

                    CommandBarUnbarRequest request = generarRequest(account, BarUnbarService.UNBAR);

                    String xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();
                    log.debug("==> REQUEST: " + xmlRequest);
                    String result = (String) mahindraClient.post(xmlRequest, String.class, MediaType.APPLICATION_XML, null).block();
                    log.debug("<== RESPONSE: " + result);
                    Response bodyResult = xmlMapper.readValue(result, Response.class);

                    StatusCode statusCode = statusCodeConfig.of(bodyResult.getTxnstatus(), bodyResult.getMessage());
                    if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                        accountUnBar.set(accountUnBar.get() + 1);
                        saveBarUnbarHistory(account, HistoryType.UNBAR);
                    }
                }
            } catch (JsonProcessingException | UnbarDisabledTemplateException e) {
                log.debug(e.getMessage());
            }
        });

        asignarCorrelativo(correlativo);
        config.setRunningUnbar(false);
        this.repositoryBarFactory.getBarUnbarConfigRepository().save(config);
        log.info("Se desbloquearon " + accountUnBar.get());
        log.info(LBL_END);
        log.info(EMPTY_STRING);
    }


    private CommandBarUnbarRequest generarRequest(StatementAccounts account, String accion) {
        CommandBarUnbarRequest request = new CommandBarUnbarRequest();
        request.setType(this.globalProperties.getTypeBarUnbar());
        request.setAction(accion);
        request.setMsisdn(account.getCelular());
        request.setProvider(this.globalProperties.getBarProvider());
        request.setReason(this.globalProperties.getBarReason());
        request.setRemark("statementaccount");
        request.setBarType(this.globalProperties.getBarType());
        request.setUserType(this.globalProperties.getBarUserType());
        return request;
    }

    private void printDebugTemplate(BarTemplate template) {
        log.debug("         Template existee y habilitado: ");
        log.debug("         Name:           " + template.getName());
        log.debug("         Tipo Bloqueo:   " + template.getBarType());
        log.debug("         Lunees:         " + template.getBarDayMonday().booleanValue());
        log.debug("         Martes:         " + template.getBarDayTuesday().booleanValue());
        log.debug("         Miercoles:      " + template.getBarDayWednesday().booleanValue());
        log.debug("         Jueves:         " + template.getBarDayThursday().booleanValue());
        log.debug("         Viernes:        " + template.getBarDayFriday().booleanValue());
        log.debug("         Tiempo:         " + template.getBarTime());
        log.debug("         Limite Amount:  " + template.getBarLimitAmount());
        log.debug("         Habilitado desbloqueo:  " + template.getEnabledUnbar().booleanValue());
    }

    private void printDebugAccount(StatementAccounts account) {
        log.debug("---------------------------------------------");
        log.debug("Celular:         " + account.getCelular());
        log.debug("Valor pagar min: " + account.getValorMinimoPagar());
        log.debug("Estado carte:    " + account.getEstadoCartera());
        log.debug("tipo Bloqueo:    " + account.getTipoBloqueo());
    }


    public ResponseBarUnbarHistory barUnbarHistory(String correlationId, UnbarBarHistoryDTO barHistory) {
        ResponseBarUnbarHistory response = ResponseBarUnbarHistory.builder().errorCode("99").errorType("0")
                .build();

        ArrayList<UnbarBarHistory> histories;
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_START, "barUnbarHistory");

        histories = (ArrayList<UnbarBarHistory>) this.repositoryBarFactory.getBarUnbarHistoryCustomRepository().searchHistory(barHistory);

        response.setHistorys(histories);
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_END, "barUnbarHistory");
        return response;
    }

    // TEMPLATE

    public ResponseBarTemplate getTemplateBar(String correlationId) {
        ResponseBarTemplate response = ResponseBarTemplate.builder().errorCode("99").errorType("0")
                .build();

        ArrayList<BarTemplate> histories;
        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_START, "getTemplateBar");

        histories = (ArrayList<BarTemplate>) this.repositoryBarFactory.getBarTemplateRepository().findAll();

        response.setTemplates(histories);
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_END, "getTemplateBar");
        return response;
    }


    public ResponseBarTemplate saveTemplateBar(String correlationId, BarTemplate request, String authorization) {
        ResponseBarTemplate response = ResponseBarTemplate.builder().errorCode("400").errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_START, "saveTemplate Bar");

        if (this.repositoryBarFactory.getBarTemplateRepository().findByBarType(request.getBarType()) != null) {
            response.setErrorCode("01");
            response.setErrorMessage("Ya existe un template para estee tipo de bloqueo.");
            return response;
        }

        this.repositoryBarFactory.getBarTemplateRepository().save(request);

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization,
                        "TEMPLATE_BAR_REGISTRY",
                        correlationId,
                        "Ha registrado un template de bloqueo " + request.getName(),
                        null)
        );
        response.setErrorCode("00");
        response.setErrorMessage(TRANSACCION_EXITOSA);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_END, "saveTemplate Bar");
        return response;
    }


    public ResponseBarTemplate updateTemplateBar(String correlationId, BarTemplate request, String authorization) {
        ResponseBarTemplate response = ResponseBarTemplate.builder().errorCode("400").errorType("0")
                .build();


        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_START, "update Template Bar");


        if (request.getId() == null) {
            response.setErrorCode("03");
            response.setErrorMessage("No se recibe el campo ID del template.");
            return response;
        }

        // MODIFICAR TEMPLATE

        Optional<BarTemplate> oGrade = this.repositoryBarFactory.getBarTemplateRepository().findById(request.getId());
        if (!oGrade.isPresent()) {
            response.setErrorCode("02");
            response.setErrorMessage("No existe el template.");
            return response;
        }
        // ACTUALIZAR OBJ DE BD
        BarTemplate barTemplate = oGrade.get();
        barTemplate.setName(request.getName());
        barTemplate.setBarDayFriday(request.getBarDayFriday());
        barTemplate.setBarDayMonday(request.getBarDayMonday());
        barTemplate.setBarDayThursday(request.getBarDayThursday());
        barTemplate.setBarDayTuesday(request.getBarDayTuesday());
        barTemplate.setBarDayWednesday(request.getBarDayWednesday());
        barTemplate.setBarLimitAmount(request.getBarLimitAmount());
        barTemplate.setBarTime(request.getBarTime());
        barTemplate.setEnabledTemplate(request.getEnabledTemplate());
        barTemplate.setEnabledUnbar(request.getEnabledUnbar());
        barTemplate.setBarType(request.getBarType());
        this.repositoryBarFactory.getBarTemplateRepository().save(barTemplate);

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization,
                        "TEMPLATE_BAR_MODIFY",
                        correlationId,
                        "Ha modificado un template de bloqueo " + request.getName(),
                        null)
        );
        response.setErrorCode("00");
        response.setErrorMessage("update exitoso.");
        log.info(LOG_FORMATED, LOG_COMPONENT, LBL_END, "update Template Bar");
        return response;
    }

    private void validateEnabledTemplate(BarTemplate template) throws BarDisabledTemplateException {
        if (template == null || !template.getEnabledTemplate()) {
            throw new BarDisabledTemplateException();
        }
    }

    private void validateEnabledUnbar(BarTemplate template) throws UnbarDisabledTemplateException {
        if ((template != null) && (!template.getEnabledUnbar())) {
            throw new UnbarDisabledTemplateException();
        }
    }

    private void validateBarExemptionDays() throws BarExemptionDaysException {
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(Calendar.HOUR, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MILLISECOND, 0);
        BarExemptionDays barExemptionDays = this.repositoryBarFactory.getBarExemptionDaysRepository().findByDay(rightNow);

        if (barExemptionDays != null) {
            throw new BarExemptionDaysException();
        }
    }

    private boolean validateBarLimitAmount(BarTemplate template, StatementAccounts account) {
        return (account.getValorMinimoPagar() > template.getBarLimitAmount());
    }

    private void saveBarUnbarHistory(StatementAccounts account, HistoryType type) {
        UnbarBarHistory unbarBarHistory = new UnbarBarHistory();
        unbarBarHistory.setMsisdn(account.getCelular());
        unbarBarHistory.setDate(new Date());
        unbarBarHistory.setBalance(account.getSaldo());
        unbarBarHistory.setQuota(account.getCupo());
        unbarBarHistory.setMinimumValuePay(account.getValorMinimoPagar());
        unbarBarHistory.setFullPayment(account.getPagoTotal());
        unbarBarHistory.setMonthlyCommission(account.getComisionMensual());
        unbarBarHistory.setDailyCommission(account.getComisionDiaria());
        unbarBarHistory.setType(type);

        this.repositoryBarFactory.getBarUnbarHistoryRepository().save(unbarBarHistory);
    }


    private void validateBarDayAndHours(BarTemplate template) throws BarNotDaysException, BarNotHoursException {
        Calendar hoursBar = Calendar.getInstance();
        Calendar rightNow = Calendar.getInstance();
        Calendar hoursBarRange = Calendar.getInstance();

        // VALIDAR DIA DE BLOQUEO

        int dia = rightNow.get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.MONDAY:
                addValidatorBarDayHours(
                        template.getBarDayMonday(),
                        template,
                        hoursBar,
                        hoursBarRange,
                        rightNow);
                break;
            case Calendar.TUESDAY:
                addValidatorBarDayHours(
                        template.getBarDayTuesday(),
                        template,
                        hoursBar,
                        hoursBarRange,
                        rightNow);
                break;

            case Calendar.WEDNESDAY:
                addValidatorBarDayHours(
                        template.getBarDayWednesday(),
                        template,
                        hoursBar,
                        hoursBarRange,
                        rightNow);
                break;

            case Calendar.THURSDAY:
                addValidatorBarDayHours(
                        template.getBarDayThursday(),
                        template,
                        hoursBar,
                        hoursBarRange,
                        rightNow);
                break;

            case Calendar.FRIDAY:
                addValidatorBarDayHours(
                        template.getBarDayFriday(),
                        template,
                        hoursBar,
                        hoursBarRange,
                        rightNow);
                break;
            default:
                throw new BarNotDaysException(template);
        }


    }

    private void addValidatorBarDayHours(Boolean barDay,
                                         BarTemplate template,
                                         Calendar hoursBar,
                                         Calendar hoursBarRange,
                                         Calendar rightNow) throws BarNotDaysException, BarNotHoursException {
        if (!barDay.booleanValue()) {
            throw new BarNotDaysException(template);
        }

        // VALIDAR HORA DE BLOQUEO
        Integer hourBar = Integer.valueOf(template.getBarTime().split(":")[0]);
        Integer minuteBar = Integer.valueOf(template.getBarTime().split(":")[1]);
        hoursBar.set(Calendar.HOUR_OF_DAY, hourBar);
        hoursBar.set(Calendar.MINUTE, minuteBar);

        hoursBarRange.set(Calendar.HOUR_OF_DAY, hoursBar.get(Calendar.HOUR_OF_DAY));
        hoursBarRange.set(Calendar.MINUTE, hoursBar.get(Calendar.MINUTE));
        hoursBarRange.add(Calendar.MINUTE, this.globalProperties.getRangeBar());

        // VALIDAR SI ESTA DENTRO DEL RANGO
        boolean hoursValid = false;
        if ((rightNow.after(hoursBar) && (rightNow.before(hoursBarRange))) || (hoursBar.getTime().compareTo(rightNow.getTime()) == 0)) {
            hoursValid = true;
        }
        if (!hoursValid) {
            throw new BarNotHoursException(template);
        }
    }


    private String asignarCorrelativo(String pcorrelativo) {
        String correlativo = pcorrelativo;

        if (correlativo == null || correlativo.isEmpty()) {
            correlativo = String.valueOf(Generator.correlationId());
        }

        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        return correlativo;
    }
}

