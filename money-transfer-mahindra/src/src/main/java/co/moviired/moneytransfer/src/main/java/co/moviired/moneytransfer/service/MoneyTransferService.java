package co.moviired.moneytransfer.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.moneytransfer.client.mahindra.Request;
import co.moviired.moneytransfer.client.mahindra.Response;
import co.moviired.moneytransfer.domain.entity.redshift.GiroFlete;
import co.moviired.moneytransfer.domain.enums.OperationType;
import co.moviired.moneytransfer.domain.model.dto.MoneyTransferDTO;
import co.moviired.moneytransfer.domain.model.dto.PersonDTO;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.domain.model.response.MoneyTransferResponse;
import co.moviired.moneytransfer.domain.repository.redshift.IGiroFlete;
import co.moviired.moneytransfer.domain.validations.Validation;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.manager.mahindra.IMahindraClient;
import co.moviired.moneytransfer.properties.GlobalProperties;
import co.moviired.moneytransfer.properties.NetworksProperties;
import co.moviired.moneytransfer.properties.StatusCodeConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class MoneyTransferService {

    private final Validation validation;
    private final StatusCodeConfig statusCodeConfig;
    private final IMahindraClient mahindra;
    private final IGiroFlete giroFleteRepository;
    private final GlobalProperties globalProperties;
    private final NetworksProperties networksProperties;

    public Mono<MoneyTransferResponse> getPing() {
        log.info("I'm alive!!!");
        return Mono.just(new MoneyTransferResponse("OK", ConstanHelper.SUCCESS_CODE_0, ConstanHelper.SUCCESS_CODE_00, statusCodeConfig.of(ConstanHelper.SUCCESS_CODE_00).getMessage()));
    }

    public Mono<MoneyTransferResponse> serviceStart(Mono<MoneyTransferRequest> pRequest, String authorization) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {
            try {
                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());

                log.info("************** Iniciando el servicio serviceStart **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.START_GIRO);

                return validationUser(request, ConstanHelper.ORIGINATOR)
                        .flatMap(validationOriginatorResponse -> validationUser(request, ConstanHelper.BENEFICIARY))
                        .flatMap(validationBeneficiaryResponse -> validation.validationFreight(request))
                        .flatMap(validationFreightResponse -> generateSuccessResponse(request, validationFreightResponse, OperationType.START_GIRO));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()))
        .doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución serviceStart: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio serviceStart **************");
        });


    }

    public Mono<MoneyTransferResponse> servicePlace(Mono<MoneyTransferRequest> pRequest, String authorization, String merchantId, String posId) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {
            try {
                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());
                request.setPosId(posId);
                request.setMerchantId(merchantId);

                log.info("************** Iniciando el servicio servicePlace **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.PLACE_GIRO);

                return validationUser(request, ConstanHelper.ORIGINATOR)
                        .flatMap(validationOriginatorResponse -> validationUser(request, ConstanHelper.BENEFICIARY))
                        .flatMap(validationBeneficiaryResponse -> validation.validationFreight(request))
                        .flatMap(validationFreightResponse -> invokeMahindra(Mono.just(request), OperationType.PLACE_GIRO))
                        .flatMap(moneyTransferResponse -> generateSuccessResponse(request, moneyTransferResponse, OperationType.PLACE_GIRO));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución servicePlace: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio servicePlace **************");
        });
    }

    public Mono<MoneyTransferResponse> servicePay(Mono<MoneyTransferRequest> pRequest, String authorization, String posId) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {
            try {

                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());
                request.setPosId(posId);

                log.info("************** Iniciando el servicio servicePay **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.PAY_GIRO);

                return validation.validationRegistry(request, getTypePerson(request, ConstanHelper.BENEFICIARY))
                        .flatMap(validationRegistryResponse -> invokeMahindra(Mono.just(request), OperationType.PAY_GIRO))
                        .flatMap(invokeMahindraResponse -> generateSuccessResponse(request, invokeMahindraResponse, OperationType.PAY_GIRO));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución servicePay: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio servicePay **************");
        });

    }

    public Mono<MoneyTransferResponse> serviceCancel(Mono<MoneyTransferRequest> pRequest, String authorization) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {
            try {

                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());

                log.info("************** Iniciando el servicio serviceCancel **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.CANCEL_GIRO);

                return validation.validationRegistry(request, getTypePerson(request, ConstanHelper.ORIGINATOR))
                        .flatMap(validationRegistryResponse -> invokeMahindra(Mono.just(request), OperationType.CANCEL_GIRO))
                        .flatMap(invokeMahindraResponse -> generateSuccessResponse(request, invokeMahindraResponse, OperationType.CANCEL_GIRO));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución serviceCancel: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio serviceCancel **************");
        });

    }

    public Mono<MoneyTransferResponse> serviceCancelPortal(Mono<MoneyTransferRequest> pRequest, String authorization, String merchantId, String posId) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();
        AtomicReference<String> codeResponseMh = new AtomicReference<>();
        AtomicInteger numberAttempts = new AtomicInteger();

        return pRequest.flatMap(request -> {
            try {

                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());
                request.setPosId(posId);
                request.setMerchantId(merchantId);

                log.info("************** Iniciando el servicio serviceCancelPortal **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.CANCEL_GIRO_PLACE_GIRO);

                return validation.getTransaction(request)
                        .flatMap(responseTransation-> validation.validationRegistry(request, getTypePerson(request, ConstanHelper.ORIGINATOR)))
                        .flatMap(validationRegistryReponse -> invokeMahindra(Mono.just(request), OperationType.CANCEL_GIRO))
                        .flatMap(invokeMahindraCancelResponse -> {

                            Response rs = (Response) invokeMahindraCancelResponse.getData();
                            request.setTransactionId(rs.getTxnid());
                            return validation.validationFreight(request);
                        })
                        .flatMap(validationFreightResponse -> {
                            do {
                                return invokeMahindra(Mono.just(request), OperationType.CANCEL_GIRO_PLACE_GIRO).flatMap(invokeMahindraResponse -> {
                                    codeResponseMh.set(invokeMahindraResponse.getResponseCode());

                                    return invokeMahindra(Mono.just(request), OperationType.RTMREQ_GIRO)
                                            .flatMap(mahindraCancelResponse -> generateSuccessResponse(request, invokeMahindraResponse, OperationType.PLACE_GIRO));
                                });
                            } while (numberAttempts.incrementAndGet() < globalProperties.getNumberAttempts() && !StatusCode.Level.SUCCESS.value().equals(codeResponseMh.get()));
                        });

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución serviceCancelPortal: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio serviceCancelPortal **************");
        });


    }

    public Mono<MoneyTransferResponse> serviceReverse(Mono<MoneyTransferRequest> pRequest, String authorization) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {
            try {
                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());

                log.info("************** Iniciando el servicio serviceReverse **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.REVERSE_GIRO);

                return validation.validationRegistry(request, getTypePerson(request, ConstanHelper.ORIGINATOR))
                        .flatMap(originatorRegistryResponse -> validation.validationRegistry(request, getTypePerson(request, ConstanHelper.BENEFICIARY)))
                        .flatMap(validationRegistryResponse -> validation.validationFreight(request))
                        .flatMap(validationFreightResponse -> invokeMahindra(Mono.just(request), OperationType.REVERSE_GIRO))
                        .flatMap(

                                mahindraReverseResponse -> invokeMahindra(Mono.just(request), OperationType.RTMREQ_GIRO)
                                        .flatMap(invokeMahindraResponse -> generateSuccessResponse(request, mahindraReverseResponse, OperationType.REVERSE_GIRO))
                        );

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución serviceReverse: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio serviceReverse **************");
        });

    }

    public Mono<MoneyTransferResponse> servicelistPending(Mono<MoneyTransferRequest> pRequest, String authorization) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {

            try {
                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());

                log.info("************** Iniciando el servicio servicelistPending **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.LIST_PENDING_GIRO);

                return invokeMahindra(Mono.just(request), OperationType.LIST_PENDING_GIRO)
                        .flatMap(moneyTransferResponse -> generateSuccessResponse(request, moneyTransferResponse, OperationType.LIST_PENDING_GIRO));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución servicelistPending: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio servicelistPending **************");
        });

    }

    public Mono<MoneyTransferResponse> serviceResendOtp(Mono<MoneyTransferRequest> pRequest, String authorization) {

        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(request -> {

            try {
                correlationId.set(UtilHelper.assignCorrelative(request.getCorrelationId()));
                request.setCorrelationId(correlationId.get());

                log.info("************** Iniciando el servicio serviceResendOtp **************");
                log.info(ConstanHelper.LBL_REQUEST, request.toString());

                validation.validateInput(request, authorization, OperationType.RESEND_OTP);

                return invokeMahindra(Mono.just(request), OperationType.RESEND_OTP)
                        .flatMap(moneyTransferResponse -> generateSuccessResponse(request, moneyTransferResponse, OperationType.RESEND_OTP));

            } catch (DataException e) {
                return generateErrorResponse(ErrorType.DATA.name(), e.getCode(), e.getMessage());
            }

        }).onErrorResume(e -> generateErrorResponse(e, correlationId.get()
        )).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución serviceResendOtp: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio serviceResendOtp **************");
        });

    }

    public Mono<MoneyTransferResponse> allFreights() {
        List<GiroFlete> giroFleteList = (List<GiroFlete>) giroFleteRepository.findAll();
        return Mono.just(new MoneyTransferResponse(giroFleteList, ConstanHelper.SUCCESS_CODE_0, ConstanHelper.SUCCESS_CODE_00, statusCodeConfig.of(ConstanHelper.SUCCESS_CODE_00).getMessage()));
    }

    //Metodos de ayuda
    private Mono<MoneyTransferResponse> validationUser(MoneyTransferRequest request, String typePerson) {

        PersonDTO personDTO = getTypePerson(request, typePerson);

        return validation.validationRegistry(request, personDTO)
                .flatMap(validationRegistryResponse -> validation.validationClientMovii(request, personDTO))
                        .flatMap(clientMoviiResponse -> {
                            if (StatusCode.Level.SUCCESS.value().equals(clientMoviiResponse.getResponseCode())) {
                                return Mono.just(clientMoviiResponse);
                            } else {
                                return validation.validationBlackList(request, personDTO).flatMap(validationBlackListResponse -> validation.validationTop(request, personDTO));
                            }
                        }).onErrorResume(Mono::error);
    }

    private PersonDTO getTypePerson(MoneyTransferRequest request, String typePerson) {

        PersonDTO personDTO = new PersonDTO();

        //Verificar tipo de persona "Originador" o "Beneficiario"
        if (typePerson.equals(ConstanHelper.ORIGINATOR)) {
            personDTO.setDocumentType(request.getIdTypeSender());
            personDTO.setDocumentNumber(request.getIdSender());
            personDTO.setPhoneNumber(request.getPhoneNumberSender());
            personDTO.setTypeUser(request.getSource());
            personDTO.setTypePerson(ConstanHelper.ORIGINATOR);

        } else {
            personDTO.setDocumentType(request.getIdTypeReceiver());
            personDTO.setDocumentNumber(request.getIdReceiver());
            personDTO.setPhoneNumber(request.getPhoneNumberReceiver());
            personDTO.setTypeUser(request.getSource());
            personDTO.setTypePerson(ConstanHelper.BENEFICIARY);
        }

        log.info("Tipo persona:" + personDTO.getTypePerson());

        return personDTO;
    }

    private Mono<MoneyTransferResponse> generateSuccessResponse(MoneyTransferRequest request, MoneyTransferResponse moneyTransferResponse, OperationType opType) {

        MoneyTransferDTO moneyTransferDTO = new MoneyTransferDTO();
        moneyTransferDTO.setCorrelationId(request.getCorrelationId());
        moneyTransferDTO.setIssuerDate(request.getIssuerDate());
        moneyTransferDTO.setOrigin(request.getOrigin());
        moneyTransferDTO.setSource(request.getSource());
        moneyTransferDTO.setIdTypeSender(request.getIdTypeSender());
        moneyTransferDTO.setIdSender(request.getIdSender());
        moneyTransferDTO.setPhoneNumberSender(request.getPhoneNumberSender());
        moneyTransferDTO.setEmailSender(request.getEmailSender());
        moneyTransferDTO.setIdTypeReceiver(request.getIdTypeReceiver());
        moneyTransferDTO.setIdReceiver(request.getIdReceiver());
        moneyTransferDTO.setPhoneNumberReceiver(request.getPhoneNumberReceiver());
        moneyTransferDTO.setEmailReceiver(request.getEmailReceiver());
        moneyTransferDTO.setAmount(request.getAmount());
        moneyTransferDTO.setFreightIva(request.getFreightIva());
        moneyTransferDTO.setAmountTotal(request.getAmountTotal());
        moneyTransferDTO.setFreight(request.getFreight());
        moneyTransferDTO.setTransactionId(request.getTransactionId());

        if ( (request.getTransactionId()==null && opType.equals(OperationType.PLACE_GIRO) || opType.equals(OperationType.REVERSE_GIRO) || opType.equals(OperationType.CANCEL_GIRO))
            || opType.equals(OperationType.RESEND_OTP) ) {

            Response rs = (Response) moneyTransferResponse.getData();
            moneyTransferDTO.setTransactionId(rs.getTxnid());

        } else if (opType.equals(OperationType.LIST_PENDING_GIRO)) {

            Response rs = (Response) moneyTransferResponse.getData();
            moneyTransferDTO.setTransactionId(rs.getTxnid());
            moneyTransferDTO.setTxnCount(rs.getTxnCount());
            moneyTransferDTO.setTxn(rs.getTxn());

        } else if (opType.equals(OperationType.PAY_GIRO)) {
            Response rs = (Response) moneyTransferResponse.getData();
            moneyTransferDTO.setAmount(rs.getAmount().intValue());
            moneyTransferDTO.setTransactionId(rs.getTxnid());

        }

        MoneyTransferResponse response = new MoneyTransferResponse(moneyTransferDTO, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), statusCodeConfig.of(StatusCode.Level.SUCCESS.value()).getMessage());
        log.info(ConstanHelper.LBL_RESPONSE, response.toString());
        return Mono.just(response);
    }

    private Mono<MoneyTransferResponse> generateErrorResponse(String responseType, String responseCode, String responseMsj) {
        MoneyTransferResponse response = new MoneyTransferResponse(null, responseType, responseCode, responseMsj);
        log.error(ConstanHelper.LBL_RESPONSE, response.toString());
        return Mono.just(response);
    }

    private Mono<MoneyTransferResponse> generateErrorResponse(Throwable e, String correlationId) {

        MoneyTransferResponse response;
        UtilHelper.assignCorrelative(correlationId);
        String msj;

        if (e instanceof ServiceException) {
            ServiceException e1 = ((ServiceException) e);
            msj = e1.getMessage();
            response = new MoneyTransferResponse(null, e1.getErrorType().name(), e1.getCode(), msj);
        }else if (e instanceof WebExchangeBindException) {
            msj = ((WebExchangeBindException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
            response = new MoneyTransferResponse(null, ErrorType.DATA.name(), "10", msj);
        }
        else {
            log.error(e.getMessage());
            msj = statusCodeConfig.of(ConstanHelper.ERROR_GENERIC, StatusCode.Level.FAIL).getMessage();
            response = new MoneyTransferResponse(null, ErrorType.PROCESSING.name(), statusCodeConfig.of(ConstanHelper.ERROR_GENERIC, StatusCode.Level.FAIL).getCode(), msj );
        }

        log.error(msj);
        log.error(ConstanHelper.LBL_RESPONSE, response.toString());
        return Mono.just(response);

    }

    private Mono<MoneyTransferResponse> invokeMahindra(Mono<MoneyTransferRequest> request, OperationType opType) {
        return request.flatMap(pRequest -> {

            try {
                return mahindra.sendMahindraRequest(getRequestMahindra(pRequest, opType), pRequest).flatMap(response -> {
                    if (ConstanHelper.SUCCESS_CODE.equals(response.getTxnstatus())) {
                        return Mono.just(new MoneyTransferResponse(response, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso MH, " + opType));
                    } else {

                        StatusCode status = statusCodeConfig.of(response.getTxnstatus(), response.getMessage());
                        if ((!status.getCode().equals(StatusCode.Level.FAIL.value())) && (status.getLevel().equals(StatusCode.Level.FAIL))) {
                            response.setTxnstatus(status.getCode());
                            response.setMessage(status.getMessage());
                        }
                        return Mono.error(new ServiceException(ErrorType.PROCESSING, response.getTxnstatus(), response.getMessage()));
                    }
                }).onErrorResume(Mono::error);

            } catch (IOException e) {
                return Mono.error(e);
            }
        });
    }

    private Request getRequestMahindra(MoneyTransferRequest request, OperationType opType) {

        String userMerchant;
        Request requestMH = new Request();

        String emailSender = request.getEmailSender() != null ? request.getEmailSender() : globalProperties.getEmailDefault();
        String emailReceiver = request.getEmailReceiver() != null ? request.getEmailReceiver() : globalProperties.getEmailDefault();


        switch (opType) {

            case PLACE_GIRO:
                requestMH.setType(globalProperties.getTypePlaceGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setUserType(request.getSource());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setSndidtype(request.getIdTypeSender());
                requestMH.setSndidno(request.getIdSender());
                requestMH.setSndname(request.getNameSender());
                requestMH.setSndmsisdn(request.getPhoneNumberSender());
                requestMH.setSndemail(emailSender);
                requestMH.setRcvidtype(request.getIdTypeReceiver());
                requestMH.setRcvidno(request.getIdReceiver());
                requestMH.setRcvname(request.getNameReceiver());
                requestMH.setRcvmsisdn(request.getPhoneNumberReceiver());
                requestMH.setRcvemail(emailReceiver);
                requestMH.setAmount(String.valueOf(request.getAmount()));
                requestMH.setRemarks(request.getUser()+"|"+request.getAmount() + "|" + request.getFreightIva() + "|" + request.getFreight() + "|" + request.getMerchantId() + "|" + request.getPosId());
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;

            case PAY_GIRO:
                requestMH.setType(globalProperties.getTypePayGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setPasscode(request.getOtp());
                requestMH.setRcvidtype(request.getIdTypeReceiver());
                requestMH.setRcvidno(request.getIdReceiver());
                requestMH.setRcvname(request.getNameReceiver());
                requestMH.setRcvmsisdn(request.getPhoneNumberReceiver());
                requestMH.setRcvemail(emailReceiver);
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;

            case REVERSE_GIRO:
                requestMH.setType(globalProperties.getTypePlaceGiro());
                requestMH.setMsisdn(globalProperties.getUserOperationGiro());
                requestMH.setUserType(request.getSource());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setSndidtype(globalProperties.getSndIdTypeGiro());
                requestMH.setSndidno(globalProperties.getSndIdnoGiro());
                requestMH.setSndname(globalProperties.getSndNameGiro());
                requestMH.setSndmsisdn(globalProperties.getSndMsisdnGiro());
                requestMH.setSndemail(emailSender);
                requestMH.setRcvidtype(request.getIdTypeReceiver());
                requestMH.setRcvidno(request.getIdReceiver());
                requestMH.setRcvname(request.getNameReceiver());
                requestMH.setRcvmsisdn(request.getPhoneNumberReceiver());
                requestMH.setRcvemail(emailReceiver);
                requestMH.setAmount(String.valueOf(request.getAmount()));
                requestMH.setRemarks(request.getUser() + "|" + request.getAmount() + "|" + request.getFreightIva() + "|" + request.getFreight() + "|" + request.getMerchantId() + "|" + request.getPosId());
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;

            case CANCEL_GIRO:
                requestMH.setType(globalProperties.getTypePayGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setPasscode(request.getOtp());
                requestMH.setRcvidtype(request.getIdTypeSender());
                requestMH.setRcvidno(request.getIdSender());
                requestMH.setRcvname(request.getNameSender());
                requestMH.setRcvmsisdn(request.getPhoneNumberSender());
                requestMH.setRcvemail(emailSender);
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                requestMH.setRemarks(request.getDetailCancel());
                break;

            case LIST_PENDING_GIRO:
                requestMH.setType(globalProperties.getTypeListPendingGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setUserType(request.getSource());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setSndidtype(request.getIdTypeSender());
                requestMH.setSndidno(request.getIdSender());
                requestMH.setRcvidtype(request.getIdTypeReceiver());
                requestMH.setRcvidno(request.getIdReceiver());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;

            case RESEND_OTP:
                requestMH.setType(globalProperties.getTypeListPendingGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setUserType(request.getSource());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setTxnId(request.getMoneyTransferId());//Id transaccion listado general
                requestMH.setPassCodeTxn(request.getTransactionId());//Id transaccion particular
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;


            case CANCEL_GIRO_PLACE_GIRO:
                requestMH.setType(globalProperties.getTypePlaceGiro());
                requestMH.setMsisdn(request.getUser());
                requestMH.setUserType(request.getSource());
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setSndidtype(globalProperties.getSndIdTypeGiro());
                requestMH.setSndidno(globalProperties.getSndIdnoGiro());
                requestMH.setSndname(globalProperties.getSndNameGiro());
                requestMH.setSndmsisdn(globalProperties.getSndMsisdnGiro());
                requestMH.setSndemail(globalProperties.getEmailDefault());
                requestMH.setRcvidtype(request.getIdTypeSender());
                requestMH.setRcvidno(request.getIdSender());
                requestMH.setRcvname(request.getNameSender());
                requestMH.setRcvmsisdn(request.getPhoneNumberSender());
                requestMH.setRcvemail(emailSender);
                requestMH.setAmount(String.valueOf(request.getAmount() + request.getFreight()));
                requestMH.setRemarks(request.getUser()+"|"+request.getAmount() + "|" + request.getFreightIva() + "|" + request.getFreight() + "|" + request.getMerchantId() + "|" + request.getPosId());
                requestMH.setFtxnId(request.getCorrelationId());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                break;

            case RTMREQ_GIRO:
                userMerchant = getUserMerchant(request.getUserMerchant()!=null?request.getUserMerchant():request.getUser());

                requestMH.setType(globalProperties.getTypeRtmreqGiro());
                requestMH.setMsisdn(userMerchant.split(":")[0]);
                requestMH.setMpin(userMerchant.split(":")[1]);
                requestMH.setPin(userMerchant.split(":")[1]);
                requestMH.setMsisdn2(globalProperties.getUserOperationGiro());
                requestMH.setAmount(String.valueOf(request.getAmount() + request.getFreight()));
                requestMH.setProvider(globalProperties.getProviderGiro());
                requestMH.setProvider2(globalProperties.getProviderGiro());
                requestMH.setPayid(globalProperties.getPayIdGiro());
                requestMH.setPayid2(globalProperties.getPayIdGiro());
                requestMH.setLanguage(globalProperties.getLanguageGiro());
                requestMH.setLanguage2(globalProperties.getLanguageGiro());
                requestMH.setFtxnId(request.getCorrelationId());
                break;

            default:
                log.error("Request MH no valido");
                break;

        }

        return requestMH;
    }

    private String getUserMerchant(String user){

        String userMerchant = "";

        for (NetworksProperties.Network network: networksProperties.getNetworks()) {
            if (network.getUser().equals(user)){
                userMerchant = network.getUser()+":"+network.getPass();
                break;
            }
        }

        if (userMerchant.isEmpty()) {
           log.error("NO se encontro usuario merchant");
        }

        return userMerchant;
    }

}


