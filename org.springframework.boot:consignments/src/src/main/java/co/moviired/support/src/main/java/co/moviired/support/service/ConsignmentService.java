package co.moviired.support.service;

import co.moviired.audit.service.PushAuditService;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.util.Generator;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.connector.connector.RestConnector;
import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.domain.client.mahindra.CommandApproveConsignmentRequest;
import co.moviired.support.domain.client.mahindra.CommandApproveConsignmentResponse;
import co.moviired.support.domain.client.supportuser.Response;
import co.moviired.support.domain.dto.ConsignmentDetailDTO;
import co.moviired.support.domain.entity.mysql.Consignment;
import co.moviired.support.domain.enums.ConsignmentApproveType;
import co.moviired.support.domain.enums.ConsignmentStatus;
import co.moviired.support.domain.enums.ParameterValidator;
import co.moviired.support.domain.repository.mysql.IConsignmentRepository;
import co.moviired.support.domain.repository.mahindra.BankCustomRepository;
import co.moviired.support.domain.request.IApproveConsignment;
import co.moviired.support.domain.request.IConsignmentRegistry;
import co.moviired.support.domain.request.IConsignmentSearch;
import co.moviired.support.domain.request.impl.*;
import co.moviired.support.domain.response.*;
import co.moviired.support.domain.response.impl.*;
import co.moviired.support.helper.Validator;
import co.moviired.support.properties.CmdApproveConsignmentProperties;
import co.moviired.support.properties.SupportUserProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static co.moviired.support.helper.Utilidades.getConsignmentDetailDTO;
import static co.moviired.support.helper.Utilidades.getFilter;
import static co.moviired.support.util.Constants.SUPPORT_USER_API;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 */

@Service
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConsignmentService implements Serializable {

    private static final long serialVersionUID = -1143184049994629351L;

    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS ConsignmentService";
    private static final String PAYMENTREFERENCE = "paymentReference";
    private static final String REGISTRYDATE = "RegistryDate";
    private static final String AGREEMENTNUMBER = "agreementNumber";
    private static final String NAMEALLIANCE = "nameAlliance";
    private static final String NAMECLIENT = "nameClient";
    private static final String BANKNAME = "bankName";
    private static final String MSISDN = "msisdn";
    private static final String STATE = "state";
    private static final String DATA_IMAGE_JPG = "data:image/jpg;base64,";
    private static final String DATA_IMAGE_PNG = "data:image/png;base64,";
    private static final String DATA_IMAGE_JPEG = "data:image/jpeg;base64,";
    private static final String CITY = "city";
    private static final String BRANCHOFFICE = "branchOffice";

    private static final String OK_MESSAGE = "OK";

    private static final String LOG_ERROR_PATTERN = " [Error:{}]";

    private final IConsignmentRepository consignmentRepository;
    private final Validator validator = new Validator();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final XmlMapper xmlMapper = new XmlMapper();
    private final RestConnector mahindraClient;
    private final PushAuditService pushAuditService;
    private final GlobalProperties globalProperties;
    private final SupportUserProperties supportUserProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final ReactiveConnector supportUserClient;
    private final BankCustomRepository bankCustomRepo;
    private final CmdApproveConsignmentProperties approveProperties;
    private final ObjectMapper objectMapper;

    public ConsignmentService(IConsignmentRepository consignmentRepository,
                              PushAuditService pushAuditService,
                              @Qualifier("mahindraClient") RestConnector mahindraClient,
                              co.moviired.support.conf.GlobalProperties pglobalProperties1,
                              SupportUserProperties supportUserProperties,
                              StatusCodeConfig statusCodeConfig,
                              @Qualifier(SUPPORT_USER_API) ReactiveConnector supportUserClient,
                              BankCustomRepository bankCustomRepo,
                              CmdApproveConsignmentProperties approveProperties,
                              ObjectMapper objectMapper) {
        this.consignmentRepository = consignmentRepository;
        this.mahindraClient = mahindraClient;
        this.pushAuditService = pushAuditService;
        this.globalProperties = pglobalProperties1;
        this.supportUserProperties = supportUserProperties;
        this.statusCodeConfig = statusCodeConfig;
        this.supportUserClient = supportUserClient;
        this.bankCustomRepo = bankCustomRepo;
        this.approveProperties = approveProperties;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<IResponseApproveConsignment> approveConsignment(String authorization, String correlationId,
                                                                          IApproveConsignment iRequest) {
        ApproveConsignment request = (ApproveConsignment) iRequest;
        Consignment consignment = null;
        ResponseApproveConsignment response = ResponseApproveConsignment.builder().errorCode("400").errorType("0")
                .build();
        HttpStatus httpStatus = HttpStatus.OK;

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "approveConsignment");

        try {
            validator.validateField(request.getCorrelationId(), true, ParameterValidator.CORRELATIONID, null);
            List<Consignment> consignmentList = consignmentRepository.findByCorrelationId(request.getCorrelationId());
            consignment = consignmentList.get(0);

            if (!consignmentList.isEmpty()) {
                if (Byte.toString(consignment.getStatus()).equals(ConsignmentStatus.PROCESS.getId()))
                    throw new ProcessingException("400", "Esta consignación se encuentra procesando.");

                if (Byte.toString(consignment.getStatus()).equals(ConsignmentStatus.APPROVED.getId()) ||
                        Byte.toString(consignment.getStatus()).equals(ConsignmentStatus.REJECTED.getId()))
                    throw new ProcessingException("400", "Esta consignación ya fue procesada.");

                if (Byte.toString(consignment.getStatus()).equals(ConsignmentStatus.PENDING.getId()))
                    consignmentRepository.updateStatus(Byte.parseByte(ConsignmentStatus.PROCESS.getId()), request.getCorrelationId());


                ConsignmentStatus status = proccedToApproveMahindraConsignment(consignment,
                        authorization.split(":")[0], request);
                response.setStatus(status.name());
                response.setErrorMessage(status.equals(ConsignmentStatus.APPROVED) ? OK_MESSAGE
                        : "Ocurrió un error al aprobar su consignación");
                response.setErrorCode(status.equals(ConsignmentStatus.APPROVED) ? "00" : "400");

            } else {
                throw new ProcessingException("400",
                        "No existe consignación relacionada al correlationId");
            }


            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Consignment finalConsignment = consignment;
            taskThread.submit(() -> this.sendEmail(finalConsignment));
        } catch (ServiceException | IOException | ParseException e) {
            log.error(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Consignment finalConsignment = consignment;
        taskThread.submit(() -> this.sendEmail(finalConsignment));

        // AUDITORIA
        String msj = response.getErrorMessage();
        if (response.getErrorCode().equals("00")) {
            msj = "Ha aprobado una consignación";
        }
        Map<String, String> detailOperationApprove = new LinkedHashMap<>();
        detailOperationApprove.put(PAYMENTREFERENCE, consignment.getPaymentReference());
        detailOperationApprove.put(REGISTRYDATE, consignment.getRegistryDate().toString());
        detailOperationApprove.put(AGREEMENTNUMBER, consignment.getAgreementNumber());
        detailOperationApprove.put(NAMEALLIANCE, consignment.getNameAlliance());
        detailOperationApprove.put(NAMECLIENT, consignment.getNameClient());
        detailOperationApprove.put(BANKNAME, consignment.getBankName());
        detailOperationApprove.put(MSISDN, consignment.getMsisdn());
        detailOperationApprove.put(STATE, consignment.getState());

        this.pushAuditService.pushAudit(
                this.pushAuditService.generarAudit(
                        authorization.split(":")[0],
                        "CONSIGNMENT_APPROVED",
                        correlationId,
                        msj,
                        detailOperationApprove)
        );

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "approveConsignment");
        return new ResponseEntity<>(response, httpStatus);
    }

    public ResponseEntity<IResponseConsignmentRegistry> consignmentRegistry(String authorization,
                                                                            IConsignmentRegistry iRequest) {
        ConsignmentRegistry requestRegistry = (ConsignmentRegistry) iRequest;
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseConsignmentRegistry response = ResponseConsignmentRegistry.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        String correlationIdNew = this.asignarCorrelativo(null);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "consignmentRegistry");
        try {
            // validate required and optional fields
            String decodeBase64Authorization = null != requestRegistry.getAuthorization()
                    ? new String(Base64.decodeBase64(requestRegistry.getAuthorization().getBytes()))
                    : null;
            validateRegistryRequiredFields(decodeBase64Authorization, requestRegistry);

            String[] authSplit = null;
            if (decodeBase64Authorization != null) {
                authSplit = decodeBase64Authorization.split(":");
            }else{
                authSplit = new String[]{""};
            }
            if (consignmentRepository.existsByCorrelationId(correlationIdNew) ||
                    consignmentRepository.existsByPaymentReferenceAndStatus(requestRegistry.getPaymentReference(), Byte.parseByte(ConsignmentStatus.PENDING.getId())) ||
                    consignmentRepository.existsByPaymentReferenceAndStatus(requestRegistry.getPaymentReference(), Byte.parseByte(ConsignmentStatus.APPROVED.getId()))
            ) {
                throw new DataException("400", "ya existe la consignación en base de datos.");
            }

            final SimpleDateFormat paymentDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            requestRegistry.setVoucher(requestRegistry.getVoucher()
                    .replace(DATA_IMAGE_JPG, "")
                    .replace(DATA_IMAGE_PNG, "")
                    .replace(DATA_IMAGE_JPEG, ""));

            Consignment entityRegistry = Consignment.builder()
                    .usernamePortalRegistry(requestRegistry.getUsernamePortalRegistry())
                    .registryDate(new Date())
                    .msisdn(authSplit[0])
                    .nameClient(requestRegistry.getNameClient())
                    .nameAlliance(requestRegistry.getNameAlliance())
                    .bankId(requestRegistry.getBankId())
                    .bankName(getBankName(requestRegistry.getBankId()))
                    .amount(Double.valueOf(requestRegistry.getAmount()))
                    .paymentDate(paymentDateFormatter.parse(requestRegistry.getPaymentDate()))
                    .agreementNumber(requestRegistry.getAgreementNumber())
                    .paymentReference(requestRegistry.getPaymentReference())
                    .status(Byte.parseByte(ConsignmentStatus.PENDING.getId()))
                    .correlationId(correlationIdNew)
                    .state(requestRegistry.getState())
                    .city(requestRegistry.getCity())
                    .branchOffice(requestRegistry.getBranchOffice())
                    .voucher(Base64.decodeBase64(requestRegistry.getVoucher())).build();
            entityRegistry = consignmentRepository.save(entityRegistry);
            response.setCorrelationId(correlationIdNew);
            response.setRegistryDate(dateFormatter.format(entityRegistry.getRegistryDate()));
            response.setStatus(ConsignmentStatus.PENDING.name());
            response.setErrorMessage(OK_MESSAGE);

            // AUDITORIA
            Map<String, String> detailOperationRegistry = new LinkedHashMap<>();
            detailOperationRegistry.put(PAYMENTREFERENCE, entityRegistry.getPaymentReference());
            detailOperationRegistry.put(REGISTRYDATE, entityRegistry.getRegistryDate().toString());
            detailOperationRegistry.put(AGREEMENTNUMBER, entityRegistry.getAgreementNumber());
            detailOperationRegistry.put(NAMEALLIANCE, entityRegistry.getNameAlliance());
            detailOperationRegistry.put(NAMECLIENT, entityRegistry.getNameClient());
            detailOperationRegistry.put(BANKNAME, entityRegistry.getBankName());
            detailOperationRegistry.put(MSISDN, entityRegistry.getMsisdn());
            detailOperationRegistry.put(STATE, entityRegistry.getState());

            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "CONSIGNMENT_CREATE",
                            correlationIdNew,
                            "Ha registrado una consignación",
                            detailOperationRegistry)
            );

            response.setErrorCode(String.valueOf(httpStatus));
        } catch (ServiceException | NumberFormatException | ParseException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "consignmentRegistry");
        return new ResponseEntity<>(response, httpStatus);
    }

    public ResponseEntity<IResponseConsignmentRegistry> consignmentUpdater(String authorization,
                                                                           IConsignmentRegistry iRequest) {
        ConsignmentRegistry requestUpdater = (ConsignmentRegistry) iRequest;
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseConsignmentRegistry response = ResponseConsignmentRegistry.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        String correlationIdRequest = this.asignarCorrelativo(requestUpdater.getCorrelationId());
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "consignmentUpdater");
        try {
            // validate required and optional fields
            String decodeBase64Authorization = null != requestUpdater.getAuthorization()
                    ? new String(Base64.decodeBase64(requestUpdater.getAuthorization().getBytes()))
                    : null;
            validateRegistryRequiredFields(decodeBase64Authorization, requestUpdater);

            String[] authSplit = null;
            if (decodeBase64Authorization != null) {
                authSplit = decodeBase64Authorization.split(":");
            }else{
                authSplit = new String[]{""};
            }

            final SimpleDateFormat paymentDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            requestUpdater.setVoucher(requestUpdater.getVoucher()
                    .replace(DATA_IMAGE_JPG, "")
                    .replace(DATA_IMAGE_PNG, "")
                    .replace(DATA_IMAGE_JPEG, ""));

            Consignment entityUpdater = Consignment.builder()
                    .id(requestUpdater.getId())
                    .usernamePortalRegistry(requestUpdater.getUsernamePortalRegistry())
                    .registryDate(requestUpdater.getRegistryDate())
                    .usernamePortalEdit(requestUpdater.getUsernamePortalEdit())
                    .editDate(new Date())
                    .msisdn(authSplit[0])
                    .nameClient(requestUpdater.getNameClient())
                    .nameAlliance(requestUpdater.getNameAlliance())
                    .bankId(requestUpdater.getBankId())
                    .bankName(getBankName(requestUpdater.getBankId()))
                    .amount(Double.valueOf(requestUpdater.getAmount()))
                    .paymentDate(paymentDateFormatter.parse(requestUpdater.getPaymentDate()))
                    .agreementNumber(requestUpdater.getAgreementNumber())
                    .paymentReference(requestUpdater.getPaymentReference())
                    .status(Byte.parseByte(ConsignmentStatus.PENDING.getId()))
                    .correlationId(correlationIdRequest)
                    .state(requestUpdater.getState())
                    .city(requestUpdater.getCity())
                    .branchOffice(requestUpdater.getBranchOffice())
                    .voucher(Base64.decodeBase64(requestUpdater.getVoucher())).build();
            consignmentRepository.save(entityUpdater);
            response.setCorrelationId(correlationIdRequest);
            response.setRegistryDate(dateFormatter.format(entityUpdater.getRegistryDate()));
            response.setStatus(ConsignmentStatus.PENDING.name());
            response.setErrorMessage(OK_MESSAGE);

            // AUDITORIA
            String msj = response.getErrorMessage();
            if (response.getErrorCode().equals("00")) {
                msj = "Ha registrado una consignación";
            }
            Map<String, String> detailOperationUpdater = new LinkedHashMap<>();
            detailOperationUpdater.put(PAYMENTREFERENCE, entityUpdater.getPaymentReference());
            detailOperationUpdater.put(REGISTRYDATE, entityUpdater.getRegistryDate().toString());
            detailOperationUpdater.put(AGREEMENTNUMBER, entityUpdater.getAgreementNumber());
            detailOperationUpdater.put(NAMEALLIANCE, entityUpdater.getNameAlliance());
            detailOperationUpdater.put(NAMECLIENT, entityUpdater.getNameClient());
            detailOperationUpdater.put(BANKNAME, entityUpdater.getBankName());
            detailOperationUpdater.put(MSISDN, entityUpdater.getMsisdn());
            detailOperationUpdater.put(STATE, entityUpdater.getState());

            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "CONSIGNMENT_MODIFY",
                            correlationIdRequest,
                            msj,
                            detailOperationUpdater)
            );

            response.setErrorCode(String.valueOf(httpStatus));
        } catch (ServiceException | NumberFormatException | ParseException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "consignmentUpdater");
        return new ResponseEntity<>(response, httpStatus);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.moviired.service.IConsignmentService#listBankss()
     */
    public ResponseEntity<IResponseBankSearch> listBanks() {

        ResponseBankSearch response = ResponseBankSearch.builder().errorCode("00").errorType("0").build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "listBanks");
        response.setBanks(bankCustomRepo.findAll());
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "listBanks");
        HttpStatus httpStatus = HttpStatus.OK;
        return new ResponseEntity<>(response, httpStatus);
    }

    public ResponseEntity<IResponseConsignmentSearch> listConsignments(IConsignmentSearch iRequest) {

        HttpStatus httpStatus = HttpStatus.OK;
        ConsignmentSearch request = (ConsignmentSearch) iRequest;
        ResponseUserConsignmentSearch response = ResponseUserConsignmentSearch.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "listConsignments");
        try {
            validator.validateField(request.getRegistryDateInit(), true, ParameterValidator.DATE,
                    "Fecha inicio, formato debe ser yyyy-MM-dd");
            validator.validateField(request.getRegistryDateEnd(), true, ParameterValidator.DATE,
                    "Fecha fin, formato debe ser yyyy-MM-dd");
            ConsignmentDetailDTO filter = getConsignmentDetailDTO(request);
            List<Consignment> consignmentsList = findConsignmentsByDateRange(
                    dateFormatter.parse(request.getRegistryDateInit() + " 00:00:00"),
                    dateFormatter.parse(request.getRegistryDateEnd() + " 23:59:59")).stream()
                    .filter(consig -> getFilter(consig.getMsisdn(), filter.getMsisdn()))
                    .collect(Collectors.toList());
            returnSuccessSearchResponse(response, consignmentsList, filter);
        } catch (ParseException | ServiceException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "listConsignments");
        return new ResponseEntity<>(response, httpStatus);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.moviired.service.IConsignmentService#listUserConsignments(java.lang.
     * String, com.moviired.model.request.IRequestConsignmentSearch)
     */
    public ResponseEntity<IResponseConsignmentSearch> listUserConsignments(IConsignmentSearch request) {
        HttpStatus httpStatus = HttpStatus.OK;
        UserConsignmentSearch consignmentReq = (UserConsignmentSearch) request;
        ResponseUserConsignmentSearch response = ResponseUserConsignmentSearch.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "listUserConsignments");
        try {
            validator.validateField(consignmentReq.getMsisdn(), true, ParameterValidator.MSISDN, null);
            validator.validateField(consignmentReq.getRegistryDateInit(), true, ParameterValidator.DATE,
                    "Fecha inicio, formato debe ser yyyy-MM-dd");
            validator.validateField(consignmentReq.getRegistryDateEnd(), true, ParameterValidator.DATE,
                    "Fecha fin, formato debe ser yyyy-MM-dd");
            ConsignmentDetailDTO filter = ConsignmentDetailDTO.builder()
                    .correlationId(consignmentReq.getCorrelationId())
                    .agreementNumber(consignmentReq.getAgreementNumber())
                    .paymentReference(consignmentReq.getPaymentReference()).bankId(consignmentReq.getBankId())
                    .bankName(consignmentReq.getBankName()).status(ConsignmentStatus.parse(consignmentReq.getStatus()))
                    .build();
            List<Consignment> consignmentsList = findConsignmentsByDateRangeAndMsisdn(
                    dateFormatter.parse(consignmentReq.getRegistryDateInit() + " 00:00:00"),
                    dateFormatter.parse(consignmentReq.getRegistryDateEnd() + " 23:59:59"), consignmentReq.getMsisdn());
            returnSuccessSearchResponse(response, consignmentsList, filter);
        } catch (ParseException | ServiceException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "listUserConsignments");
        return new ResponseEntity<>(response, httpStatus);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.moviired.service.IConsignmentService#rejectConsignment(java.lang.String,
     * com.moviired.model.request.IRequestConsignmentRegistry)
     */
    public ResponseEntity<IResponseConsignmentReject> rejectConsignment(String authorization, String correlationId,
                                                                        IConsignmentRegistry iRequest) {
        HttpStatus httpStatus = HttpStatus.OK;
        Consignment consignment = null;
        ConsignmentReject request = (ConsignmentReject) iRequest;
        ResponseConsignmentReject response = ResponseConsignmentReject.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        correlationId = this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "rejectConsignment");
        try {
            validator.validateField(request.getCorrelationId(), true, ParameterValidator.CORRELATIONID, null);
            validator.validateField(request.getReason(), true, ParameterValidator.REASON, null);
            List<Consignment> consignmentList = consignmentRepository.findByCorrelationId(request.getCorrelationId());
            consignment = consignmentList.get(0);
            boolean existPendingConsignment = consignmentRepository.existsByCorrelationIdAndStatus(
                    request.getCorrelationId(), Byte.parseByte(ConsignmentStatus.PENDING.getId()));
            final int updated = existPendingConsignment ? consignmentRepository.updateStatusAndReasonAndProccesorUserAndType(
                    Byte.parseByte(ConsignmentStatus.REJECTED.getId()), request.getReason(), request.getCorrelationId(),
                    authorization.split(":")[0],
                    Byte.parseByte(String.valueOf(ConsignmentApproveType.MANUALLY.getId())), request.getUsernamePortalAuthorizer(), new Date()) : 0;
            log.info("[correlation {} update process rows affected :{}]", request.getCorrelationId(),
                    updated);
            if (updated >= 1) {/**/
                consignment.setStatus(Byte.parseByte(String.valueOf(ConsignmentStatus.REJECTED.getId())));
                response.setStatus(ConsignmentStatus.REJECTED.name());
                response.setErrorMessage(OK_MESSAGE);
                response.setErrorCode("00");
                response.setCorrelationId(request.getCorrelationId());
            } else {
                throw new ProcessingException("400",
                        !existPendingConsignment ? "Ya ha sido rechazada/aprobada" : "Rechazo de Consignación Fallido");
            }
        } catch (ServiceException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }


        ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Consignment finalConsignment = consignment;
        taskThread.submit(() -> this.sendEmail(finalConsignment));

        // AUDITORIA
        String msj = response.getErrorMessage();
        if (response.getErrorCode().equals("00")) {
            msj = "Ha aprobado una consignación";
        }

        if(consignment != null) {
            Map<String, String> detailOperationReject = new LinkedHashMap<>();
            detailOperationReject.put(PAYMENTREFERENCE, consignment.getPaymentReference());
            detailOperationReject.put(REGISTRYDATE, consignment.getRegistryDate().toString());
            detailOperationReject.put(AGREEMENTNUMBER, consignment.getAgreementNumber());
            detailOperationReject.put(NAMEALLIANCE, consignment.getNameAlliance());
            detailOperationReject.put(NAMECLIENT, consignment.getNameClient());
            detailOperationReject.put(BANKNAME, consignment.getBankName());
            detailOperationReject.put(MSISDN, consignment.getMsisdn());
            detailOperationReject.put(STATE, consignment.getState());

            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            authorization.split(":")[0],
                            "CONSIGNMENT_REJECTED",
                            correlationId,
                            msj,
                            detailOperationReject)
            );
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "rejectConsignment");
        return new ResponseEntity<>(response, httpStatus);
    }

    public ResponseEntity<IResponseConsignmentSearch> viewConsignmentDetail(IConsignmentSearch request) {
        HttpStatus httpStatus = HttpStatus.OK;
        UserConsignmentSearch consignmentReq = (UserConsignmentSearch) request;
        ResponseUserConsignmentSearch response = ResponseUserConsignmentSearch.builder().errorCode("400").errorType("0")
                .build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "viewConsignmentDetail");
        try {
            validator.validateField(consignmentReq.getCorrelationId(), true, ParameterValidator.CORRELATIONID, null);
            ConsignmentDetailDTO filter = ConsignmentDetailDTO.builder()
                    .correlationId(consignmentReq.getCorrelationId()).build();
            List<Consignment> consignmentsList = findConsignmentsByCorrelationId(consignmentReq.getCorrelationId());
            returnSuccessSearchResponse(response, consignmentsList, filter);
        } catch (ServiceException e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "viewConsignmentDetail");
        return new ResponseEntity<>(response, httpStatus);
    }

    private void sendEmail(Consignment finalConsignment) {

        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            log.info("{} {} {}", "==>", " Request url: ", this.supportUserProperties.getUrl());

            String mhr = (String) this.supportUserClient.exchange(
                    HttpMethod.POST,
                    this.supportUserProperties.getUrl(),
                    "{\"msisdn\": \"" + finalConsignment.getUsernamePortalRegistry() + "\"}",
                    String.class,
                    null,
                    null).block();
            Response mhResponse = objectMapper.readValue(mhr, Response.class);
            mhResponse.getUser().setMahindraPassword("*******");
            mhResponse.getUser().setMpin("*******");
            log.info("{} {} {} {}", "<==", " Response", ": \n", objectMapper.writeValueAsString(mhResponse));

            //Informar por correo
            if (mhResponse.getErrorCode().equalsIgnoreCase("00")) {
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("name", mhResponse.getUser().getFirstName());
                formData.add("email", mhResponse.getUser().getEmail());
                formData.add("banco", finalConsignment.getBankName());
                formData.add("convenio", finalConsignment.getAgreementNumber());
                formData.add("referencia", finalConsignment.getPaymentReference());
                formData.add("fechaConsignacion", finalConsignment.getPaymentDate().toString());
                formData.add("valor", finalConsignment.getAmount().toString());
                if (Byte.toString(finalConsignment.getStatus()).equals(ConsignmentStatus.APPROVED.getId())) {
                    formData.add("idTransaccion", finalConsignment.getMahindraTransactionId());
                    formData.add("status", "APROBADA");
                } else if (Byte.toString(finalConsignment.getStatus()).equals(ConsignmentStatus.REJECTED.getId())) {
                    formData.add("idTransaccion", finalConsignment.getMahindraTransactionId());
                    formData.add("status", "RECHAZADA");
                }

                // Invocar al servicio de envío de correo
                this.postEmail(formData, this.globalProperties.getPathConsignmentProcess());
                log.info("<== Se ha enviado del mensaje EMAIL de forma satisfactoria:");
            }
        } catch (Exception e) {
            log.error("<== Error programando el envío del mensaje Email- Causa: " + e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.moviired.service.IConsignmentService#viewConsignmentVoucher(java.lang.
     * String)
     */
    public ResponseEntity<IResponseConsignmentSearch> viewConsignmentVoucher(String id) {
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseConsignmentViewVoucher response = ResponseConsignmentViewVoucher.builder().errorCode("400")
                .errorType("0").build();

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "viewConsignmentVoucher");
        try { //NOSONAR
            byte[] path = consignmentRepository.findImagePathById(Integer.parseInt(id));
            response.setVoucher(DATA_IMAGE_JPG + Base64.encodeBase64String(path));
            response.setErrorMessage(OK_MESSAGE);
            response.setErrorCode("00");

        } catch (NullPointerException e) {
            log.warn(" {}", id, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "viewConsignmentVoucher");

        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * @param consignmentList
     * @return
     */
    public List<ConsignmentDetailDTO> fillConsignmentDetail(List<Consignment> consignmentList,
                                                            ConsignmentDetailDTO filter) {
        List<ConsignmentDetailDTO> detail = new ArrayList<>();
        consignmentList.stream().filter(consig -> getFilter(consig.getCorrelationId(), filter.getCorrelationId()))
                .filter(consig -> getFilter(consig.getAgreementNumber(), filter.getAgreementNumber()))
                .filter(consig -> getFilter(consig.getAmount().intValue(), filter.getAmount()))
                .filter(consig -> getFilter(consig.getMahindraApprovementId(), filter.getApprovementId()))
                .filter(consig -> getFilter(consig.getBankId(), filter.getBankId()))
                .filter(consig -> getFilter(consig.getBankName(), filter.getBankName()))
                .filter(consig -> getFilter(consig.getBranchOffice(), filter.getBranchOffice()))
                .filter(consig -> getFilter(consig.getCity(), filter.getCity()))
                .filter(consig -> getFilter(consig.getId(), filter.getId()))
                .filter(consig -> getFilter(consig.getPaymentDate().toString(), filter.getPaymentDate()))
                .filter(consig -> getFilter(consig.getNameClient(), filter.getNameClient()))
                .filter(consig -> getFilter(consig.getNameAlliance(), filter.getNameAlliance()))
                .filter(consig -> getFilter(consig.getPaymentReference(), filter.getPaymentReference()))
                .filter(consig -> getFilter(null != consig.getProcessDate() ? consig.getProcessDate().toString() : null,
                        filter.getProcessDate()))
                .filter(consig -> getFilter(consig.getReason(), filter.getReason()))
                .filter(consig -> getFilter(consig.getState(), filter.getState()))
                .filter(consig -> getFilter(consig.getStatus(), filter.getStatus()))
                .filter(consig -> getFilter(consig.getMahindraTransactionId(), filter.getTxnid()))
                .collect(Collectors.toList()).forEach(consignment -> {
            ConsignmentDetailDTO dto = ConsignmentDetailDTO.builder()
                    .usernamePortalRegistry(consignment.getUsernamePortalRegistry())
                    .usernamePortalEdit(consignment.getUsernamePortalEdit())
                    .usernamePortalAuthorizer(consignment.getUsernamePortalAuthorizer())
                    .editDate(consignment.getEditDate())
                    .agreementNumber(consignment.getAgreementNumber())
                    .registryDate(consignment.getRegistryDate())
                    .authorizerDate(consignment.getAuthorizerDate())
                    .amount(String.valueOf(consignment.getAmount().intValue()))
                    .approvementId(consignment.getMahindraApprovementId()).bankId(consignment.getBankId())
                    .bankName(consignment.getBankName()).branchOffice(consignment.getBranchOffice())
                    .city(consignment.getCity()).correlationId(consignment.getCorrelationId())
                    .id(consignment.getId().toString()).msisdn(consignment.getMsisdn())
                    .paymentDate(consignment.getPaymentDate().toString())
                    .paymentReference(consignment.getPaymentReference())
                    .processDate(null != consignment.getProcessDate() ? consignment.getProcessDate().toString()
                            : null)
                    .reason(consignment.getReason())
                    .nameClient(consignment.getNameClient())
                    .nameAlliance(consignment.getNameAlliance())
                    .state(consignment.getState())
                    .status(ConsignmentStatus.parse(Byte.toUnsignedInt(consignment.getStatus())))
                    .txnid(consignment.getMahindraTransactionId()).ipAddress(consignment.getIpAddress())
                    .approvementMethod(ConsignmentApproveType
                            .parse(Integer.parseInt(Byte.toString(consignment.getType()))).name())
                    .processorUser(consignment.getProcessorUser()).build();
            detail.add(dto);
        });
        log.info(" [consignments size: {}]", detail.size());
        return detail;
    }

    /**
     * @param bankId
     * @return
     * @throws ServiceException
     */
    private String getBankName(String bankId) throws ServiceException {
        try {
            return bankCustomRepo.getBankNameById(bankId);
        } catch (Exception e) {
            log.warn(LOG_ERROR_PATTERN, e.getMessage());
            throw new DataException("400", "Banco inválido");
        }
    }

    /**
     * find consignments by date range
     *
     * @param dateInit
     * @param dateEnd
     * @return list of consignments
     */
    public List<Consignment> findConsignmentsByDateRange(Date dateInit, Date dateEnd) {
        return consignmentRepository.findByRegistryDateBetweenOrderByRegistryDateDesc(dateInit, dateEnd);
    }

    /**
     * find consignments by date range and msisdn
     *
     * @param dateInit
     * @param dateEnd
     * @param msisdn
     * @return list of consignments
     */
    public List<Consignment> findConsignmentsByDateRangeAndMsisdn(Date dateInit, Date dateEnd, String msisdn) {
        return consignmentRepository.findByRegistryDateBetweenAndMsisdnOrderByRegistryDateDesc(dateInit, dateEnd, msisdn);
    }

    /**
     * find consigments by correlationId
     *
     * @param correlationId
     * @return list of consignments
     */
    public List<Consignment> findConsignmentsByCorrelationId(String correlationId) {
        return consignmentRepository.findByCorrelationId(correlationId);
    }

    /**
     * rta genérica para métodos de listar consignaciones
     *
     * @param response
     * @param consignmentsList
     * @param filter
     * @return
     */
    private ResponseUserConsignmentSearch returnSuccessSearchResponse(
            ResponseUserConsignmentSearch response, List<Consignment> consignmentsList, ConsignmentDetailDTO filter) {
        response.setConsigments(fillConsignmentDetail(consignmentsList, filter));
        response.setErrorMessage(OK_MESSAGE);
        response.setErrorCode("00");
        return response;
    }

    /**
     * @param consignment
     * @throws IOException
     * @throws ParseException
     */
    public ConsignmentStatus proccedToApproveMahindraConsignment(Consignment consignment,
                                                                 String processorUser, ApproveConsignment request) throws IOException, ParseException {
        ConsignmentStatus status;
        String consigmentStatus = Byte.toString(consignment.getStatus());
        if (ConsignmentStatus.PENDING.getId().equals(consigmentStatus)) {
            CommandApproveConsignmentRequest mahindraRequest = CommandApproveConsignmentRequest.builder()
                    .amount(String.valueOf(consignment.getAmount().intValue())).bankid(consignment.getBankId())
                    .ftxnid(consignment.getCorrelationId()).msisdn(consignment.getMsisdn())
                    .referenceid(consignment.getPaymentReference()).type(approveProperties.getType()).build();

            String xmlRequest = xmlMapper.writeValueAsString(mahindraRequest).toUpperCase();
            log.info("[Starting consume mahindra... {}]", xmlRequest);
            String result = consumeMahindraConsignment(xmlRequest);
            CommandApproveConsignmentResponse bodyResult = xmlMapper.readValue(result, CommandApproveConsignmentResponse.class);
            log.info(" [response:{}]", bodyResult);

            consignment.setProcessDate(new Date());
            consignment.setMahindraApprovementId(bodyResult.getTrid());
            consignment.setMahindraTransactionId(bodyResult.getTxnid());

            status = (StatusCode.Level.SUCCESS.equals(statusCodeConfig.of(bodyResult.getTxnStatus()).getLevel()))
                    ? ConsignmentStatus.APPROVED
                    : ConsignmentStatus.REJECTED;
            consignment.setStatus(Byte.parseByte(status.getId()));
            if (status.equals(ConsignmentStatus.REJECTED)) {
                consignment.setReason(statusCodeConfig.of(bodyResult.getTxnStatus()).getMessage());
            }
            consignment.setProcessorUser(processorUser);
            consignment.setAuthorizerDate(new Date());
            consignment.setUsernamePortalAuthorizer(request.getUsernamePortalAuthorizer());
            consignment.setType(Byte.parseByte(String.valueOf(ConsignmentApproveType.MANUALLY.getId())));
            consignmentRepository.save(consignment);
        } else {
            status = ConsignmentStatus.parseStr(consigmentStatus);
            log.warn("[consignemnt was already processed {}]", consignment.getCorrelationId());
        }

        return status;

    }

    /**
     * validar los parámetros request para registrar una nueva consignación
     *
     * @param decodeBase64Authorization
     * @param request
     * @throws ServiceException
     */
    public void validateRegistryRequiredFields(String decodeBase64Authorization, ConsignmentRegistry request)
            throws ServiceException {
        String[] auth = decodeBase64Authorization.split(":");
        if (auth.length != 2)
            throw new DataException("400", "Error en parametro de authorización");

        validator.validateField(request.getBankId(), true, ParameterValidator.BANKID, null);
        validator.validateField(request.getAmount(), true, ParameterValidator.AMOUNT, null);
        validator.validateField(request.getPaymentDate(), true, ParameterValidator.DATE,
                "paymentDate no cumple con formato yyyy-MM-dd");
        validator.validateField(request.getAgreementNumber(), true, ParameterValidator.AGREMENTNUMBER, null);
        validator.validateField(request.getPaymentReference(), true, ParameterValidator.PAYMENTREFERENCE, null);
        validator.validateField(request.getState(), false, ParameterValidator.ADDRESS, STATE);
        validator.validateField(request.getCity(), false, ParameterValidator.ADDRESS, CITY);
        validator.validateField(request.getBranchOffice(), false, ParameterValidator.BRANCHOFFICE, BRANCHOFFICE);
    }



    /**
     * consumir post de mahindra.
     *
     * @param xmlRequest
     * @return
     */
    public String consumeMahindraConsignment(String xmlRequest) {

        return mahindraClient.post(xmlRequest, String.class, MediaType.APPLICATION_XML);
    }

    /**
     * @param voucher
     * @throws ServiceException
     */
    public boolean putImageOnDisk(String path, String voucher) throws ProcessingException {
        try (FileOutputStream imageOutFile = new FileOutputStream(path)) {
            byte[] imagesByteArr = Base64.decodeBase64(voucher);
            imageOutFile.write(imagesByteArr);
        } catch (IOException e) {
            log.error(LOG_ERROR_PATTERN, e.getMessage());
            throw new ProcessingException(statusCodeConfig.of("CSMT_IMG_SAVE_FAIL"));
        }
        return true;
    }

    private String asignarCorrelativo(String correlativo) {
        if (correlativo == null || correlativo.isEmpty()) {
            correlativo = String.valueOf(Generator.correlationId());
        }

        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        return correlativo;
    }

    public final void postEmail(MultiValueMap<String, String> map, String path) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            log.info(this.globalProperties.getUrlServiceSendEmail() + path);
            restTemplate.exchange(this.globalProperties.getUrlServiceSendEmail() + path, HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            log.info("Content-type = " + Objects.requireNonNull(e.getResponseHeaders()).getFirst("Content-Type"));
            log.info("Authorization = " + e.getResponseHeaders().getFirst("Authorization"));
            log.info("grant_type = " + e.getResponseHeaders().getFirst("grant_type"));
        }
    }
}

