package co.moviired.business.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.*;
import co.moviired.base.helper.CommandHelper;
import co.moviired.base.util.Generator;
import co.moviired.base.util.Security;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.ValidatorFactory;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.dto.banking.response.ResponseAgreement;
import co.moviired.business.domain.enums.*;
import co.moviired.business.domain.jpa.mahindra.entity.User;
import co.moviired.business.domain.jpa.mahindra.repository.IUserRepository;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.domain.jpa.movii.entity.BillerCategory;
import co.moviired.business.domain.jpa.movii.repository.IBillerCategoriesRepository;
import co.moviired.business.domain.jpa.movii.repository.IBillerRepository;
import co.moviired.business.properties.GlobalProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.*;
import co.moviired.business.provider.bankingswitch.response.CommandCashOutBankingResponse;
import co.moviired.business.provider.bankingswitch.response.CommandQueryBankingResponse;
import co.moviired.business.provider.integrator.response.ResponseIntegrator;
import co.moviired.business.provider.mahindra.response.*;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class BankingService {

    private static final String ERROR = "ERROR: ";
    private static final String FORMAT_LOG = "{} {}";
    private static final String FINAL_RESPONSE = "FINAL RESPONSE ";
    private static final String[] PROTECT_FIELDS = {"pin", "mpin", "otp"};

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;
    private final IUserRepository iUserRepository;
    private final StatusCodeConfig statusCodeConfig;
    private final ValidatorFactory validatorFactory;
    private final GlobalProperties globalProperties;
    private final AgreementService agreementService;
    private final IBillerRepository iBillerRepository;
    private final MahindraProperties mahindraProperties;
    private final IBillerCategoriesRepository iBillerCategoriesRepository;

    // Consulta las categorias o convenios de acuerdo al filtro
    public Mono<ResponseAgreement> listAgreements(Seller source, String authorization, Integer idCategory, String textFilter) {
        String queryType = (idCategory == null) ? "CATEGORIES" : "AGREEMENTS";
        return Mono.just(source).flatMap(seller -> {

            log.info("************ STARTED QUERY " + queryType + " *************");
            log.info("REQUEST HEADER - " + seller.name());
            String newTextFilter = (textFilter == null) ? null : "%".concat(textFilter).concat("%");

            try {
                RequestFormatBanking banking = new RequestFormatBanking();
                String[] posAuth = authorization.split(":");
                banking.setMsisdn1(posAuth[0]);
                banking.setMpin(posAuth[1]);

                return loginMahindra(banking).flatMap(login -> {

                    List<?> genericList;
                    ResponseAgreement response;
                    LoginResponseQuery loginResponse = new LoginResponseQuery(login.getMsisdn(), login.getUsertype(), login.getFirstname(),
                            login.getAgentcode(), login.getIdtype(), login.getIdno(), login.getEmail(), login.getGender(), login.getDob());

                    if (idCategory == null && newTextFilter == null) {
                        genericList = iBillerCategoriesRepository.getAllCategories(seller);
                    } else if (idCategory != null && newTextFilter == null) {
                        genericList = iBillerRepository.getAgreementsByCategory(idCategory, Boolean.TRUE, CollectionType.AUTOMATIC, seller);
                    } else {
                        genericList = iBillerRepository.getAgreementsByFilterText(newTextFilter, Boolean.TRUE, CollectionType.AUTOMATIC, seller);
                    }

                    response = mappingResultQuery(genericList, loginResponse);

                    log.info("FINAL RESPONSE - " + response.getErrorMessage());
                    return Mono.just(response);
                });

            } catch (ParsingException | ProcessingException | Exception | DataException e) {
                return Mono.error(e);
            }

        }).onErrorResume(e -> {
            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                log.error(ERROR + se.toString());
                ResponseAgreement response = new ResponseAgreement(se.getErrorType().name(), se.getCode(), se.getMessage());
                log.info(FORMAT_LOG, FINAL_RESPONSE, response);
                return Mono.just(response);
            }

            log.error(ERROR + e.toString());
            ResponseAgreement response = new ResponseAgreement(ErrorType.PROCESSING.name(), "401", e.toString());
            log.info(FORMAT_LOG, FINAL_RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info("************ FINISHED QUERY " + queryType + " ************"));
    }

    // Filtra y mapea el resultado de la consulta
    private ResponseAgreement mappingResultQuery(List<?> genericList, LoginResponseQuery login) {
        ResponseAgreement response;
        List<Biller> listAgreements = null;
        List<BillerCategory> listCategories = null;
        if (!genericList.isEmpty()) {
            if (genericList.get(0) instanceof BillerCategory) {
                listCategories = ((List<BillerCategory>) genericList);
                listCategories.forEach(BillerCategory::toPublic);
            } else {
                listAgreements = ((List<Biller>) genericList);
                listAgreements.forEach(Biller::toPublic);
            }
            response = new ResponseAgreement("", "00", "OK", login, listAgreements, listCategories);
        } else {
            response = new ResponseAgreement(ErrorType.DATA.name(), String.valueOf(ErrorType.DATA), "NO SE ENCONTRO INFORMACION");
        }
        return response;
    }

    //METHODS SERVICE.
    public Mono<Response> service(@NotNull OperationType opType, @NotNull Mono<RequestFormatBanking> brequest,
                                  String referenceNumber, String merchantId, String posId, String userpass, String correlationId) {

        AtomicReference<String> requestType = new AtomicReference<>();
        AtomicReference<RequestFormatBanking> requestFormatBanking = new AtomicReference<>();

        switch (opType) {
            case DEPOSIT:
                requestType.set("DEPOSITO");
                break;
            case WITHDRAWAL:
                requestType.set("RETIRO");
                break;
            case QUERY:
                requestType.set("CONSULTA");
                break;
            case QUERY_OBLIGATION:
                requestType.set("CONSULTA DE OBLIGACIONES");
                break;
            case QUERY_WITHDRAWAL:
                requestType.set("CONSULTA DE RETIRO");
                break;
            case QUERY_BILL:
                requestType.set("CONSULTA DE FACTURAS");
                break;
            case PAY_BILL:
                requestType.set("PAGO DE FACTURAS");
                break;
            default:
                requestType.set("PAGO DE OBLIGACIONES");
                break;
        }

        return brequest.flatMap(request -> {
            try {
                // Generación del nuevo correlation id
                String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
                setVariablesLog(newCorrelationId);
                request.setReferenceNumber((referenceNumber != null) ? referenceNumber : request.getReferenceNumber());

                log.info("************ INICIANDO - PROCESO BANKING " + requestType + " ************");
                log.info("NUMERO DE REFERENCIA " + request.getReferenceNumber());
                log.info("CABECERAS " + "merchantId = " + merchantId + " , posId = " + posId + " , user = " + userpass.substring(0, userpass.length() - 5));
                log.info("CUERPO: \n" + this.objectMapper.writeValueAsString(request));

                // B. Validar datos obligatorios
                request.setCorrelationIdPortal((correlationId != null) ? correlationId : request.getCorrelationId());
                requestFormatBanking.set(request);
                BankingValidator validator = validatorFactory.getValidator(opType);
                validator.validationInput(request, merchantId, posId, userpass);

                request.setCorrelationId(newCorrelationId);
                log.info("CORRELATION_ID GENERADO: " + request.getCorrelationId());

            } catch (Exception | ParsingException | DataException e) {
                return Mono.error(e);
            }
            return Mono.just(request);

        }).flatMap(request -> {
            // Autenticación contra mahindra
            try {
                if (opType.equals(OperationType.DEPOSIT) || opType.equals(OperationType.PAY_BILL) || opType.equals(OperationType.PAY_OBLIGATION)) {
                    return Mono.just("Sin Validacion");
                } else {
                    return loginMahindra(requestFormatBanking.get());
                }
            } catch (ParsingException | ProcessingException | JsonProcessingException | DataException e) {
                return Mono.error(e);
            }

        }).flatMap(responseMahindraByAgentCode -> {
            // Obtener datos de Mahindra por AgentCode y completar informacion
            try {
                return completeInformation(requestFormatBanking.get(), opType);
            } catch (Exception | DataException e) {
                return Mono.error(e);
            }

        }).flatMap(responseProvider -> {
            // Ejecutar proveedor y obtener respuesta final
            try {
                return invokeProvider(requestFormatBanking.get(), opType);
            } catch (JsonProcessingException | ServiceException e) {
                return Mono.error(e);
            }

        }).onErrorResume(e -> {
            // Manejo de las excepciones
            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                log.error(ERROR + se.toString(), se);
                Response res = new Response(se.getCode(), se.getMessage(), se.getErrorType().name(), requestFormatBanking.get().getCorrelationId());
                log.info(FORMAT_LOG, FINAL_RESPONSE, res);
                return Mono.just(res);
            }

            StatusCode statusCode = statusCodeConfig.of("-1", e.toString());
            Response res = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING.name(), requestFormatBanking.get().getCorrelationId());
            log.info(FORMAT_LOG, FINAL_RESPONSE, res);
            return Mono.just(res);

        }).doAfterTerminate(() -> log.info("************ FINALIZADO - PROCESO DE BANKING " + requestType + "************"));
    }

    // Login Mahindra
    private Mono<CommandLoginServiceResponse> loginMahindra(RequestFormatBanking bankingRequest) throws ParsingException, JsonProcessingException, ProcessingException, DataException {
        Mono<CommandLoginServiceResponse> responseCliente;
        ReactiveConnector clientLogin = clientFactory.getClient(OperationType.LOGIN_USER, bankingRequest);
        IParser loginParser = this.parserFactory.getParser(OperationType.LOGIN_USER, bankingRequest);
        IRequest mhRequest = loginParser.parseRequest(bankingRequest);
        log.info("PETICION DE AUTENTICACION MAHINDRA: \n" + CommandHelper.printIgnore(toXmlString(mhRequest), PROTECT_FIELDS).toUpperCase());

        log.info("Conectandose a Mahindra..." + mahindraProperties.getUrlTransactional());
        responseCliente = clientLogin.post(this.xmlMapper.writeValueAsString(mhRequest).toUpperCase(), String.class, MediaType.APPLICATION_XML, null)
                .flatMap(obResp -> {
                    try {
                        CommandLoginServiceResponse mhResponse;
                        // Transformar XML response a  response
                        setVariablesLog(bankingRequest.getCorrelationId());
                        mhResponse = this.xmlMapper.readValue((String) obResp, CommandLoginServiceResponse.class);
                        log.info("RESPUESTA DE AUTENTICACION MAHINDRA: \n" + this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());

                        if (!mhResponse.getTxnstatus().equals("200")) {
                            StatusCode statusCode = statusCodeConfig.of("3");
                            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                        }
                        return Mono.just(mhResponse);

                    } catch (IOException | ProcessingException io) {
                        return Mono.error(io);
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof ProcessingException) {
                        return Mono.error(e);
                    }
                    CommunicationException ce = new CommunicationException("400", e.getMessage());
                    return Mono.error(ce);
                });
        return responseCliente;
    }

    private Mono<RequestFormatBanking> completeInformation(RequestFormatBanking bankingRequest, OperationType opType) throws JsonProcessingException, DataException {
        User user = new User();
        Mono<RequestFormatBanking> bankingResponse;
        SimpleDateFormat output = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        bankingRequest.setComponentDate(output.format(new Date()));
        if (bankingRequest.getSource().equals(Seller.CHANNEL.name())) {
            log.info("CONSULTA A MAHINDRA CON AGENTCODE: " + bankingRequest.getAgentCode());
            user = iUserRepository.findByAgentCodeAndStatus(bankingRequest.getAgentCode(), "Y");
            user.toPublic();
            log.info("RESPUESTA DE CONSULTA A MAHINDRA: \n" + this.objectMapper.writeValueAsString(user));
        }
        RequestFormatBanking banking = assignVariables(bankingRequest, user, opType);
        bankingResponse = Mono.just(banking);
        return bankingResponse;
    }

    // Asigna los valores de la consulta realizada a mahindra
    private RequestFormatBanking assignVariables(RequestFormatBanking bankingRequest, User user, OperationType opType) throws DataException {
        if (opType.equals(OperationType.QUERY_BILL) || opType.equals(OperationType.PAY_BILL)) {

            bankingRequest.setTercId("");
            bankingRequest.setHomologateBankId("");
            bankingRequest.setHomologateIncom((user.getCommercialField10() == null) ? "" : user.getCommercialField10());

            if (user.getCommercialField7() != null) {
                String commercialField7 = user.getCommercialField7().replaceAll("[^\\d]", "");
                bankingRequest.setTercId(commercialField7);
            }

            if (user.getCommercialField11() != null) {
                if (!user.getCommercialField11().equals(user.getCommercialField7()) && !user.getCommercialField11().equals(bankingRequest.getAgentCode())) {
                    String commercialField11 = user.getCommercialField11().replaceAll("[^\\d]", "");
                    bankingRequest.setHomologateBankId(commercialField11);
                } else {
                    StatusCode statusCode = statusCodeConfig.of("33");
                    throw new DataException(statusCode.getCode(), statusCode.getMessage());
                }
            }

        } else if (user.getCommercialField7() == null) {
            if (bankingRequest.getAgentCode().length() > 8) {
                int index = bankingRequest.getAgentCode().length() - 8;
                bankingRequest.setTercId(bankingRequest.getAgentCode().substring(index));
            } else {
                bankingRequest.setTercId(bankingRequest.getAgentCode());
            }
        } else {
            bankingRequest.setTercId(user.getCommercialField7());
        }

        return bankingRequest;
    }

    // Invoca el servicio configurado para la peticion
    private Mono<Response> invokeProvider(RequestFormatBanking bankingRequest, final OperationType opType) throws ServiceException, JsonProcessingException {

        agreementService.findAgreementByProccessBill(bankingRequest, opType);
        ReactiveConnector client = clientFactory.getClient(opType, bankingRequest);
        IParser parser = parserFactory.getParser(opType, bankingRequest);
        IRequest mhRequest = parser.parseRequest(bankingRequest);

        Object request;
        MediaType contentType;

        boolean isIntegrator = (!opType.equals(OperationType.DEPOSIT) && !opType.equals(OperationType.PAY_BILL) && !opType.equals(OperationType.PAY_OBLIGATION)
                && !(opType.equals(OperationType.QUERY_BILL) && bankingRequest.getModality().equals(Modality.BATCH)));

        if (isIntegrator) {
            contentType = MediaType.APPLICATION_JSON;
            log.info("PETICION ENVIADA AL INTEGRADOR: \n" + Security.printIgnore(toObjectString(mhRequest), PROTECT_FIELDS));
            request = mhRequest;
        } else {
            contentType = MediaType.APPLICATION_XML;
            log.info("PETICION ENVIADA A MAHINDRA: \n" + CommandHelper.printIgnore(toXmlString(mhRequest), PROTECT_FIELDS).toUpperCase());
            request = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
        }

        //Enviar la peticion
        log.info("Conectandose a " + bankingRequest.getUrl());
        return client.post(request, String.class, contentType, null)
                .flatMap(obResp -> {
                    Response resp;
                    try {
                        // Transformar XML response a  response
                        String mhr = ((String) obResp);
                        IResponse mhResponse = readValue(opType, mhr, bankingRequest);

                        if (isIntegrator) {
                            log.info("RESPUESTA DEL PROVEEDOR \n" + this.objectMapper.writeValueAsString(mhResponse));
                        } else {
                            log.info("RESPUESTA DE MAHINDRA: \n" + this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());
                        }

                        // Transformar response especifica a Banking response
                        resp = parser.parseResponse(bankingRequest, mhResponse);

                        //Se modifica el correlationId por el original
                        resp.setCorrelationId(bankingRequest.getCorrelationIdPortal());

                        // Registrar transaccion para retiros
                        if (opType.equals(OperationType.WITHDRAWAL) && ("00").equals(resp.getErrorCode())) {
                            // Enviar por nuevo hilo
                            registerOperation(bankingRequest, resp);
                        }

                        if (!resp.getErrorCode().equals("200") && !resp.getErrorCode().equals("00")) {
                            Response res = new Response(resp.getErrorCode(), resp.getErrorMessage(), ErrorType.PROCESSING.name(), bankingRequest.getCorrelationId());
                            log.info(FORMAT_LOG, FINAL_RESPONSE, res);
                            return Mono.just(res);
                        }

                        // Devolver la respuesta transformada o vacía (en caso de error)
                        log.info(FORMAT_LOG, FINAL_RESPONSE, resp);
                        return Mono.just(resp);

                    } catch (ParsingException | IOException e) {
                        return Mono.error(e);
                    }

                }).onErrorResume(e -> {
                    if (e instanceof ParsingException) {
                        ParsingException ep = new ParsingException("99", e.getMessage());
                        return Mono.error(ep);
                    }
                    CommunicationException ce = new CommunicationException("400", e.getMessage());
                    return Mono.error(ce);
                });
    }

    private void registerOperation(RequestFormatBanking bankingRequest, Response respSwitch) {
        ExecutorService taskReverse = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        taskReverse.submit(() -> processCashInTransactionMahindra(bankingRequest, respSwitch));
    }

    // METODOS COMPLEMENTOS
    private void processCashInTransactionMahindra(RequestFormatBanking bankingRequest, Response respSwitch) {
        try {
            IParser parser = parserFactory.getParser(OperationType.CASH_IN, bankingRequest);

            //Armar la petición
            IRequest mhRequest = parser.parseRequest(bankingRequest, respSwitch);
            log.info(FORMAT_LOG, " Request CASHIN MAHINDRA:\n", this.xmlMapper.writeValueAsString(mhRequest).toUpperCase());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<>(this.xmlMapper.writeValueAsString(mhRequest).toUpperCase(), headers);

            int count = 1;

            while (count <= mahindraProperties.getAttempts()) {

                ResponseEntity<String> response =
                        restTemplate.postForEntity(mahindraProperties.getUrlTransactional(), request, String.class);

                IResponse mhResponse = readValue(OperationType.CASH_IN, response.getBody(), bankingRequest);

                log.info("[" + bankingRequest.getCorrelationId() + "] Intento: " + count + " Response CASHIN MAHINDRA:" + this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());
                // Transformar response especifica a Banking response

                CommandCashInResponse resp = (CommandCashInResponse) mhResponse;

                count++;
                if (resp.getTxnstatus().equals("200") || resp.getTxnstatus().equals("00")) {
                    count = mahindraProperties.getAttempts() + 1;
                    log.info("[{}] {}", bankingRequest.getCorrelationId(), " PROCESO EXITOSO CASH IN");
                }
            }

        } catch (ParsingException | IOException | ProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    // Leer la respuesta especifica en el COMMAND general
    public IResponse readValue(@NotNull OperationType opType, @NotNull String mhr, RequestFormatBanking bankingRequest) throws ParsingException, IOException {
        IResponse response = null;
        switch (opType) {
            case PAY_BILL:
                if (Modality.ONLINE.equals(bankingRequest.getModality())) {
                    response = this.xmlMapper.readValue(mhr, CommandBillPayResponse.class);
                } else if (Modality.BATCH.equals(bankingRequest.getModality())) {
                    response = this.xmlMapper.readValue(mhr, CommandPayBillBatchResponse.class);
                }
                break;

            case DEPOSIT:
            case PAY_OBLIGATION:
                response = this.xmlMapper.readValue(mhr, CommandBillPayResponse.class);
                break;

            case CASH_IN:
                response = this.xmlMapper.readValue(mhr, CommandCashInResponse.class);
                break;

            case LOGIN_USER:
                response = this.xmlMapper.readValue(mhr, CommandLoginServiceResponse.class);
                break;

            case QUERY:
            case QUERY_OBLIGATION:
            case QUERY_WITHDRAWAL:
                response = this.objectMapper.readValue(mhr, CommandQueryBankingResponse.class);
                break;

            case QUERY_BILL:
                if (Modality.ONLINE.equals(bankingRequest.getModality())) {
                    response = this.objectMapper.readValue(mhr, ResponseIntegrator.class);
                } else if (Modality.BATCH.equals(bankingRequest.getModality())) {
                    response = this.xmlMapper.readValue(mhr, CommandQueryBillBatchResponse.class);
                }
                break;

            case WITHDRAWAL:
                response = this.objectMapper.readValue(mhr, CommandCashOutBankingResponse.class);
                break;

            case VALIDATE_BILLPAYMENT_EANCODE:
            case VALIDATE_BILLPAYMENT_REFERENCE:
                response = this.objectMapper.readValue(mhr, ResponseIntegrator.class);
                break;

            default:
                StatusCode statusCode = statusCodeConfig.of("1");
                throw new ParsingException(statusCode.getCode(), statusCode.getMessage());
        }
        return response;
    }

    private String toXmlString(Object xmlObject) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(xmlObject);
    }

    private String toObjectString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private void setVariablesLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

