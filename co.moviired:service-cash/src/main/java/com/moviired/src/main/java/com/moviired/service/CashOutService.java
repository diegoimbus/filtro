package com.moviired.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Generator;
import com.moviired.client.mahindra.command.Request;
import com.moviired.client.mahindra.command.Response;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.excepciones.ManagerException;
import com.moviired.helper.*;
import com.moviired.manager.IOtpManager;
import com.moviired.manager.OtpManagerFactory;
import com.moviired.model.Configurations;
import com.moviired.model.Network;
import com.moviired.model.dto.OtpDTO;
import com.moviired.model.entities.cashservice.CashOut;
import com.moviired.model.entities.cashservice.CashOutExemptUsers;
import com.moviired.model.entities.cashservice.CashOutExemptedNetworks;
import com.moviired.model.entities.giros.Giro;
import com.moviired.model.entities.moviiregister.PendingUser;
import com.moviired.model.enums.CashOutStatus;
import com.moviired.model.enums.UserType;
import com.moviired.model.request.CashOutRequest;
import com.moviired.model.response.CashOutResponse;
import com.moviired.model.response.Data;
import com.moviired.model.response.NetworkResponse;
import com.moviired.model.response.impl.ResponseConsultBalance;
import com.moviired.properties.GiroProperties;
import com.moviired.repository.ICashRepository;
import com.moviired.repository.jpa.cashservice.ICashOutExemptUsers;
import com.moviired.repository.jpa.cashservice.ICashOutExemptedNetworks;
import com.moviired.repository.jpa.cashservice.ICashOutRepository;
import com.moviired.repository.jpa.giros.IGiroRepository;
import com.moviired.repository.jpa.moviiregister.IPendingUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.moviired.helper.Constant.*;

@Slf4j
@Service
@AllArgsConstructor
public class CashOutService implements Serializable {

    private static final long serialVersionUID = -1514823821736151381L;
    private final SimpleDateFormat dateFormatMedCorrelationId = new SimpleDateFormat("yyMMddHHmmssSSS");
    private final Configurations configurations;
    private final GiroProperties giroProperties;
    private final Network network;
    private final StatusCodeConfig statusCodeConfig;
    private final ICashRepository cashRepository;
    private final ICashOutRepository cashOutRepository;
    private final IGiroRepository giroRepository;
    private final CryptoHelper cryptoHelperOtp;
    private final CryptoHelper cryptoHelperAuthorization;
    private final OtpManagerFactory otpManagerFactory;
    private final SignatureHelper signaturaHelper;
    private final ConsignmentServiceImpl consignmentService;
    private final IPendingUserRepository pendingUserRepository;
    private final ICashOutExemptUsers cashOutExemptUsersRepository;
    private final ICashOutExemptedNetworks cashOutExemptedNetworksRepository;

    //servicios de negocio
    public final Data initialize(CashOutRequest request) {
        Data data;
        String holdId = null;
        try {
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            Validator.validateFields(request, networkDetail, ProcessType.INITIALIZATE_CASHOUT);
            Utilidad.infoRequestAndDataService(request, null);
            log.info("**********INICIANDO SERVICIO INICIALIZADOR***********");

            validateCorrelationId(request.getCorrelationId(), request.getAgentCode(), networkDetail.getType());

            Response loginMh = this.sendLoginMahindra(request);

            validatorInitialize(request, loginMh);

            boolean transactionCost = validateTransactionCost(request, loginMh);

            Response responseHold = createHoldRequest(request, networkDetail, transactionCost);

            StatusCode statusCode = statusCodeConfig.of(responseHold.getTxnstatus(), responseHold.getMessage());
            if (!StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                throw new ServiceException(ErrorType.PROCESSING, statusCodeConfig.of(Constant.HOLD_MONEY, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(Constant.HOLD_MONEY, StatusCode.Level.FAIL).getMessage());
            }

            holdId = responseHold.getTxnid();

            // CashOut como Giro
            Giro giro = null;
            if (Network.Type.ALLIES.equals(networkDetail.getType())) {
                giro = this.registerGiro(request, networkDetail, holdId, loginMh);
            }

            CashOut cashOut = new CashOut();
            cashOut.setIssuerDateCashOut(Utilidad.dateFormat(request.getIssueDate()));
            cashOut.setStateCashOut(CashOutStatus.INITIALIZED);
            cashOut.setCorrelationId(request.getCorrelationId());
            cashOut.setPhoneNumberCashOut(request.getUserLogin());
            cashOut.setIdentificationNumber(loginMh.getIdno());
            cashOut.setAgentCodeCashOut(request.getAgentCode());
            cashOut.setAmountCashOut(request.getAmount());
            cashOut.setTransactionCost(networkDetail.getCostTransaction());
            cashOut.setTargetAppCashOut(request.getSource().concat("|").concat(networkDetail.getName()));
            cashOut.setExpirationDateCashOut(networkDetail.getExpirationDate(networkDetail.getTimeOtpCashout()));
            cashOut.setTxnHold(holdId);
            cashOut.setSignatureCashOut(this.signaturaHelper.signCashOut(cashOut));
            cashOut.setTransactionCostProcess(transactionCost);

            // Genera el OTP
            com.moviired.client.supportotp.Response responseCreateOtp = createOtp(request, loginMh, networkDetail, true);

            if (responseCreateOtp.getOtp() == null || !responseCreateOtp.getResponseCode().equals(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode())) {
                throw new ProcessingException(statusCodeConfig.of(Constant.OTP_NOT_GENERATED, StatusCode.Level.FAIL));
            }

            cashOut.setOtp(responseCreateOtp.getOtp());
            cashOut = cashOutRepository.save(cashOut);

            if ((Network.Type.ALLIES.equals(networkDetail.getType())) && (giro != null)) {
                giro.setFacturaEnvio(String.valueOf(cashOut.getId()));
                giro.setEnvio(cashOut.getId().toString());
                giro.setPinGiro(cryptoHelperOtp.decoder(responseCreateOtp.getOtp()));
                giro = this.giroRepository.save(giro);
                cashOut.setGiroId(giro.getGiroId());
            }

            // Actualizar cashout con el OTP y/o Giro generado
            cashOut = cashOutRepository.save(cashOut);

            // Genera la respuesta
            data = new Data();
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setOtp(cryptoHelperOtp.decoder(responseCreateOtp.getOtp()));
            data.setCorrelationId(request.getCorrelationId());
            data.setCashOutId(cashOut.getId().toString());
            data.setAmount(cashOut.getAmountCashOut());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));

        } catch (ProcessingException | Exception throwable) {
            unFreeze(holdId, request.getCorrelationId());
            data = Utilidad.generateResponseError(throwable);
        } catch (ServiceException throwable) {
            data = Utilidad.generateResponseError(throwable);
        }
        log.info("***********FIN SERVICIO INICIALIZADOR**************");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data decline(CashOutRequest request) {
        Data data;
        try {
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            Validator.validateFields(request, networkDetail, ProcessType.DECLINE_CASHOUT);
            Utilidad.infoRequestAndDataService(request, null);
            log.info("***********INICIANDO SERVICIO DECLINAR***********");

            if (Network.Type.ALLIES.equals(networkDetail.getType())) {
                giroValidationProcessComplete(request);
            }

            //valida correlationId
            validateCorrelationId(request.getCorrelationId(), request.getAgentCode(), networkDetail.getType());

            log.info("Buscando cashOut {} ", request.getCashOutId());
            Optional<CashOut> ocashOut = cashOutRepository.findById(request.getCashOutId());

            if (!ocashOut.isPresent()) {
                throw new DataException(statusCodeConfig.of(Constant.CASH_OUT_NO_FOUND, StatusCode.Level.FAIL));
            }

            CashOut cashOut = ocashOut.get();

            //Autentica con mahindra
            Response loginMh = this.sendLoginMahindra(request);

            if (!UserType.SUBSCRIBER.name().equals(loginMh.getUserType())) {
                throw new ProcessingException(statusCodeConfig.of(Constant.USER_TYPE_NO_AUTHORIZED, StatusCode.Level.FAIL));
            }

            if (!cashOut.getAgentCodeCashOut().equals(request.getAgentCode())) {
                throw new DataException(statusCodeConfig.of(Constant.NO_AUTHORIZED_AGENT_CODE, StatusCode.Level.FAIL));
            }

            if (!CashOutStatus.INITIALIZED.equals(cashOut.getStateCashOut())) {
                throw new ProcessingException(statusCodeConfig.of(Constant.NO_AUTHORIZED_STATUS, StatusCode.Level.FAIL));
            }

            if (cashOut.getGiroId() != null) {
                Optional<Giro> giro = giroRepository.findById(cashOut.getGiroId());
                if ((giro.isPresent()) && (CashOutStatus.COMPLETED.giroCode().equals(giro.get().getEstadoId()))) {
                    throw new ProcessingException(statusCodeConfig.of(Constant.NO_AUTHORIZED_STATUS, StatusCode.Level.FAIL));
                }
            }

            data = new Data();

            if (validateSignature(cashOut) != null) {
                cashOut.setStateCashOut(CashOutStatus.ALTERED);
                cashOutRepository.save(cashOut);
                return validateSignature(cashOut);
            }

            // Actualiza el estado del cashOut
            cashOut.setStateCashOut(CashOutStatus.DECLINED);
            cashOut.setIssuerDateCashOut(new Date());
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setCorrelationId(cashOut.getCorrelationId());
            data.setCashOutId(cashOut.getId().toString());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));

            log.info("CashOut Declinado : {}", cashOut.getId().toString());

            cashOutRepository.save(cashOut);

        } catch (ServiceException | Exception throwable) {
            data = Utilidad.generateResponseError(throwable);
        }

        log.info("***********FIN SERVICIO DECLINAR***********");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data complete(CashOutRequest request) {
        Data data = new Data();

        try {
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            Validator.validateFields(request, networkDetail, ProcessType.COMPLETE_CASHOUT);
            Utilidad.infoRequestAndDataService(request, null);
            log.info("***********INICIANDO SERVICIO COMPLETE***********");

            // Validacion para que la red no pueda completar (ejemplo: Éxito, esta red completa via giro )
            if (!networkDetail.isPendingByMerchant()) {
                throw new ProcessingException(statusCodeConfig.of(Constant.NETWORK_NOT_ENABLED_TO_CONSUME, StatusCode.Level.FAIL));
            }

            CashOut cashOut = new CashOut();

            switch (networkDetail.getProtocol()) {
                case PROTOCOL_ONE:
                    //Protocolo cashOut dos pasos : Busqueda id cashOut
                    cashOut = cashOutRepository.findById(request.getCashOutId()).orElse(null);
                    break;
                case PROTOCOL_TWO:
                    //Protocolo cashOut un paso : Busqueda por otp y numero celular
                    cashOut = cashOutRepository.findByOtpAndPhoneNumberCashOut(request.getOtp(), request.getPhoneNumber());
                    break;
                case PROTOCOL_THREE:
                    //Protocolo Servibanca : Busqueda por otp y cedula
                    cashOut = cashOutRepository.findByOtpAndIdentificationNumber(request.getOtp(), request.getIdentificationNumber());
                    break;
                default:
                    log.info(Constant.NOT_CONFIGURATION_FOR_PROTOCOL);
            }

            validatorComplete(cashOut, request, networkDetail);

            //Validar la integridad de la transacción
            if (validateSignature(cashOut) != null) {
                cashOut.setStateCashOut(CashOutStatus.ALTERED);
                cashOutRepository.save(cashOut);
                return validateSignature(cashOut);
            }

            cashOut.setIssuerDateCashOut(new Date());
            cashOut.setIssuerCredCashOut(request.getUserLogin().concat(":").concat(request.getPin()));

            if (!unFreeze(cashOut.getTxnHold(), cashOut.getCorrelationId())) {
                throw new ProcessingException(statusCodeConfig.of(StatusCode.Level.FAIL.value()));
            }

            CashOut cashOutDiscountBalance = discountBalance(cashOut, request, networkDetail, false);

            cashOut.setStateCashOut(cashOutDiscountBalance.getStateCashOut());
            cashOut.setTransactionCostProcess(cashOutDiscountBalance.isTransactionCostProcess());
            cashOut.setProcessedCashOut(Boolean.TRUE);
            cashOut.setTakenCashOut(Boolean.TRUE);
            cashOut.setCorrelationIdComplete(request.getCorrelationId());
            cashOut.setIssuerDateCashOut(new Date());
            cashOut.setIssuerCredCashOut(cryptoHelperAuthorization.encoder(request.getUserLogin().concat(":").concat(request.getPin())));

            if (cashOut.getGiroId() != null) {
                CashOut finalCashOut = cashOut;
                giroRepository.findById(cashOut.getGiroId()).ifPresent(giro -> {
                    giro.setEstadoId(finalCashOut.getStateCashOut().giroCode());
                    giroRepository.save(giro);
                });
            }

            // Genera la respuesta
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setCorrelationId(request.getCorrelationId());
            data.setTransactionId(cashOutDiscountBalance.getTxnId());
            data.setCashOutId(cashOut.getId().toString());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));

            cashOutRepository.save(cashOut);

        } catch (ServiceException | Exception throwable) {
            data = Utilidad.generateResponseError(throwable);
        }
        log.info("***********FIN SERVICIO COMPLETE***********");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data reverse(CashOutRequest request) {
        Data data = new Data();
        try {
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            Validator.validateFields(request, networkDetail, ProcessType.REVERSE_CASHOUT);
            Utilidad.infoRequestAndDataService(request, null);
            log.info("***********INICIANDO SERVICIO REVERSE***********");

            //valida correlationId
            validateCorrelationId(request.getCorrelationId(), request.getAgentCode(), networkDetail.getType());

            CashOut cashOut;
            if (request.getCashOutId() == null) {
                //Protocolo Servibanca : Busqueda por otp y cedula
                cashOut = cashOutRepository.findByOtpAndIdentificationNumber(request.getOtp(), request.getIdentificationNumber());
            } else {
                cashOut = cashOutRepository.findById(request.getCashOutId()).orElse(null);
            }

            if (cashOut == null) {
                throw new DataException(statusCodeConfig.of(Constant.CASH_OUT_NO_FOUND, StatusCode.Level.FAIL));
            }

            if (!cashOut.getAgentCodeCashOut().equals(request.getAgentCode())) {
                throw new DataException(statusCodeConfig.of(Constant.NO_AUTHORIZED_AGENT_CODE, StatusCode.Level.FAIL));
            }

            if (validateSignature(cashOut) != null) {
                cashOut.setStateCashOut(CashOutStatus.ALTERED);
                cashOutRepository.save(cashOut);
                return validateSignature(cashOut);
            }

            if ((!CashOutStatus.COMPLETED.equals(cashOut.getStateCashOut())) && (!CashOutStatus.TRANSACTION_COST.equals(cashOut.getStateCashOut()))) {
                throw new ProcessingException(statusCodeConfig.of(Constant.NO_AUTHORIZED_STATUS, StatusCode.Level.FAIL));
            }

            log.info("reverse el cashout {}", cashOut.getCorrelationId());
            cashOut.setStateCashOut(CashOutStatus.REVERSED);

            cashOut.setIssuerDateCashOut(new Date());
            cashOut.setProcessedCashOut(Boolean.FALSE);
            cashOut.setTakenCashOut(Boolean.FALSE);

            // Genera la respuesta
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setCorrelationId(cashOut.getCorrelationId());
            data.setCashOutId(cashOut.getId().toString());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));

            cashOutRepository.save(cashOut);

        } catch (ServiceException | Exception throwable) {
            data = Utilidad.generateResponseError(throwable);
        }
        log.info("***********FIN SERVICIO REVERSE***********");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data pendingBySubscriber(CashOutRequest request) {
        Data data;
        try {
            log.info("***********INICIANDO SERVICIO PENDING BY SUBSCRIBER***********");
            Validator.validateFields(request, null, ProcessType.PENDING_BY_SUBSCRIBER);
            Utilidad.infoRequestAndDataService(request, null);
            List<CashOutResponse> pendings = cashOutRepository.findByPhoneNumberCashOutAndStateCashOut(request.getUserLogin(), CashOutStatus.INITIALIZED)
                    .stream()
                    .map(cashOut -> CashOutResponse.builder()
                            .cashOutId(cashOut.getId().toString())
                            .state(cashOut.getStateCashOut().value())
                            .amount(cashOut.getAmountCashOut())
                            .phoneNumber(cashOut.getPhoneNumberCashOut())
                            .agentName(network.getOption(cashOut.getAgentCodeCashOut()).getName())
                            .time(cashOut.getCreationDateCashOut().toString().split(" ")[1])
                            .build()
                    ).collect(Collectors.toList());

            // Genera la respuesta
            data = new Data();
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));
            data.setLongListCashOut(pendings.size());
            data.setCashOutList(pendings);

        } catch (DataException | Exception throwable) {
            data = Utilidad.generateResponseError(throwable);
        }

        log.info("***********FIN SERVICIO PENDING BY SUBSCRIBER***********");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data pendingByMerchant(CashOutRequest request) {
        Data data;
        try {
            //Autentica con mahindra
            log.info("***********INICIO SERVICIO PENDING BY MERCHANT***********");
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            Validator.validateFields(request, networkDetail, ProcessType.PENDING_BY_MERCHANT);
            Utilidad.infoRequestAndDataService(request, null);
            List<CashOutResponse> pendingResponse;

            // Validacion para que red no pueda consultar los pendientes ni completed (ejemplo: Éxito)
            if (!networkDetail.isPendingByMerchant()) {
                throw new ProcessingException(statusCodeConfig.of(Constant.NETWORK_NOT_ENABLED_TO_CONSUME, StatusCode.Level.FAIL));
            }

            Response loginMh = this.sendLoginMahindra(request);

            //falta validación de que el merchantId debe ser el mismo con el que se inicio el cashOut

            if (networkDetail.isProcessCreateToken()) {
                //Crear Token
                pendingResponse = processCreateToken(request, networkDetail, loginMh);
            } else {
                pendingResponse = cashOutRepository.findByAgentCodeCashOutAndStateCashOutAndPhoneNumberCashOut(request.getAgentCode(), CashOutStatus.INITIALIZED, request.getPhoneNumber())
                        .stream()
                        .map(cashOut -> CashOutResponse.builder()
                                .cashOutId(cashOut.getId().toString())
                                .state(cashOut.getStateCashOut().value())
                                .amount(cashOut.getAmountCashOut())
                                .phoneNumber(cashOut.getPhoneNumberCashOut())
                                .agentName(network.getOption(cashOut.getAgentCodeCashOut()).getName())
                                .time(cashOut.getCreationDateCashOut().toString().split(" ")[1])
                                .build()
                        )
                        .collect(Collectors.toList());
            }

            // Genera la respuesta
            data = new Data();
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));
            data.setLongListCashOut(pendingResponse.size());
            data.setCashOutList(pendingResponse);

        } catch (Exception | ServiceException throwable) {
            data = Utilidad.generateResponseError(throwable);
        }
        log.info("***********FIN SERVICIO PENDING BY MERCHANT***********");
        Utilidad.infoRequestAndDataService(null, data);
        return data;
    }

    public final Data findCostByNetwork(String agentCode) {
        Data data;
        log.info("***********INICIANDO SERVICIO FIND COST BY NETWORK***********");
        try {
            Network.NetworkDetail networkDetail = network.getOption(agentCode);
            data = new Data();
            data.setAmount(networkDetail.getCostTransaction());
            data.setTransactionDate(Utilidad.dateFormat(new Date()));
            data.setCode(StatusCode.Level.SUCCESS.value());

            log.info("amount:{}, agentCode:{}, netName: {}", data.getAmount(), agentCode, networkDetail.getName());
        } catch (Exception throwable) {
            data = Utilidad.generateResponseError(throwable);
        }
        log.info("***********FIN SERVICIO FIND COST BY NETWORK***********");
        return data;
    }

    public final List<NetworkResponse> findNetworks(CashOutRequest request) {
        List<NetworkResponse> netList = new ArrayList<>();
        log.info("***********INICIANDO SERVICIO FIND NETWORK************");
        try {
            for (Map.Entry<String, Network.NetworkDetail> entry : network.getOptions().entrySet()) {
                Network.NetworkDetail option = entry.getValue();

                if (option.isAvailableDocumentType(request.getDocumentType())) {
                    netList.add(new NetworkResponse(entry.getKey(), option.getName(), option.getLogo(), option.getDescription(), option.getCarouselName()));
                }

            }
            log.info("Tipo documento {} Retornadas {} redes", request.getDocumentType(), netList.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("***********FIN SERVICIO FIND NETWORK************");
        return netList;
    }

    private void validatorInitialize(CashOutRequest request, Response loginMh) throws ProcessingException, ParsingException {

        Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());

        //VALIDACIONES INICIALES
        if ((request.getAmount() == null) || (request.getAmount() == 0) || (request.getAmount() < Constant.MIN_AMOUNT) || (!Utilidad.multiplo(request.getAmount()))) {
            throw new ProcessingException(statusCodeConfig.of(Constant.WRONG_AMOUNT, StatusCode.Level.FAIL));
        }

        String auth = request.getUserLogin() + ":" + cryptoHelperAuthorization.encoder(request.getPin());
        String decodeBase64Authorization = new String(Base64.encodeBase64(auth.getBytes()));

        ResponseEntity<ResponseConsultBalance> responseConsultBalance = consignmentService.consultBalance(decodeBase64Authorization, request.getCorrelationId());

        if ((null == Objects.requireNonNull(responseConsultBalance.getBody()).getErrorCode()) || (!statusCodeConfig.of(Constant.TRANSACTION_OK).getCode().equals(responseConsultBalance.getBody().getErrorCode()))) {
            throw new ProcessingException(statusCodeConfig.of(Constant.ERROR_BALANCE_SUBSCRIBER, StatusCode.Level.FAIL));
        }

        int valor = (int) Double.parseDouble(responseConsultBalance.getBody().getBalance());

        if (valor < request.getAmount()) {
            throw new ProcessingException(statusCodeConfig.of(Constant.INSUFFICIENT_BALANCE, StatusCode.Level.FAIL));
        }

        if (!UserType.SUBSCRIBER.name().equals(loginMh.getUserType())) {
            throw new ProcessingException(statusCodeConfig.of(Constant.USER_TYPE_NO_AUTHORIZED, StatusCode.Level.FAIL));
        }

        if (!networkDetail.isEnable()) {
            throw new ProcessingException(statusCodeConfig.of(Constant.NETWORK_DISABLED, StatusCode.Level.FAIL));
        }

        // Valida que no exista otro cashout pendiente para el suscriber
        Integer count = cashOutRepository.countByPhoneNumberCashOutAndStateCashOut(request.getUserLogin(), CashOutStatus.INITIALIZED);
        if (configurations.getSimultaneousCashout() <= count) {
            throw new ProcessingException(statusCodeConfig.of(Constant.LIMIT_INIT_CASHOUT, StatusCode.Level.FAIL));
        }
    }

    private void validatorComplete(CashOut cashOut, CashOutRequest request, Network.NetworkDetail networkDetail) throws ServiceException, ManagerException {

        if (cashOut == null) {
            throw new DataException(statusCodeConfig.of(Constant.CASH_OUT_NO_FOUND, StatusCode.Level.FAIL));
        }

        if (!CashOutStatus.INITIALIZED.equals(cashOut.getStateCashOut())) {
            throw new ProcessingException(statusCodeConfig.of(Constant.NO_AUTHORIZED_STATUS, StatusCode.Level.FAIL));
        }

        if (request.getAmount() != null && !cashOut.getAmountCashOut().equals(request.getAmount())) {
            throw new DataException(statusCodeConfig.of(Constant.AMOUNT_IS_INVALID, StatusCode.Level.FAIL));
        }

        if (!cashOut.getAgentCodeCashOut().equals(request.getAgentCode())) {
            throw new DataException(statusCodeConfig.of(Constant.NO_AUTHORIZED_AGENT_CODE, StatusCode.Level.FAIL));
        }

        if ((new Date()).after(cashOut.getExpirationDateCashOut())) {
            throw new ProcessingException(statusCodeConfig.of(Constant.CASH_OUT_EXPIRED, StatusCode.Level.FAIL));
        }
         /*
                1. cuando es completar en 1 paso se valida el otp con : phoneNumber del suscriber (el cual tiene asociado el otp) y el otp
                2. cuando es completar en 2 pasos se valida el token con : userLogin del merchant (el cual tiene asociado el token) y con el token
             */
        if (Network.Type.MOVII_POINTS.equals(networkDetail.getType()) || Network.Type.ALLIES.equals(networkDetail.getType())) {
            IOtpManager otpManager = this.otpManagerFactory.getOtpManager(networkDetail.getGeneratorOtp());

            com.moviired.client.supportotp.Response responseValidateOtp = otpManager.isValid(networkDetail.isProcessCreateToken() ? request.getUserLogin() : cashOut.getPhoneNumberCashOut(),
                    request.getOtp() != null ? request.getOtp() : request.getToken());

            if (!responseValidateOtp.getResponseCode().equals(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode())) {
                throw new ProcessingException(responseValidateOtp.getResponseCode(), responseValidateOtp.getResponseMessage());
            }
        }
    }

    //Metodos de comunicación con MH
    private Response sendLoginMahindra(CashOutRequest request) throws ServiceException {
        Request loginReq = new Request();
        loginReq.setType(this.configurations.getNameAuthpinreq());
        loginReq.setProvider(this.configurations.getProviderAuth());
        loginReq.setMsisdn(request.getUserLogin());
        loginReq.setMpin(request.getPin());
        loginReq.setSource(request.getSource());
        loginReq.setOtpreq(this.configurations.getOtpReq());
        loginReq.setIspincheckreq(this.configurations.getIsPinCheckReq());
        return this.cashRepository.sendMahindraRequest(loginReq);
    }

    private Response createHoldRequest(CashOutRequest request, Network.NetworkDetail networkDetail, boolean transactionCost) throws ServiceException {

        Integer transactionAmount = request.getAmount() + (transactionCost ? networkDetail.getCostTransaction() : 0);
        Request requestHold = new Request();
        requestHold.setType(this.configurations.getNameHoldMoney());
        requestHold.setFtxnId(String.valueOf(Generator.correlationId(dateFormatMedCorrelationId, Generator.PIN_2)));
        requestHold.setMsisdn(request.getUserLogin());
        requestHold.setAmount(String.valueOf(transactionAmount));
        requestHold.setUserType(this.configurations.getUserType());
        requestHold.setReleaseAfterDays(this.configurations.getReleaseAfterDays().toString());
        requestHold.setPayId(this.configurations.getPayId());
        requestHold.setLanguage1(this.configurations.getLanguage1());
        requestHold.setProviderId(this.configurations.getProviderAuth());
        requestHold.setRemarks("INITIALIZED|".concat(request.getCorrelationId()).concat("|API"));
        requestHold.setPriorityRequestType(this.configurations.getPriorityRequestType());
        requestHold.setBlockSms("BOTH");

        return this.cashRepository.sendMahindraRequest(requestHold);
    }

    private CashOut discountBalance(CashOut cashOut, CashOutRequest request, Network.NetworkDetail networkDetail, boolean transactionCost) throws ServiceException {

        if (null == cashOut.getIssuerCredCashOut() || cashOut.getIssuerCredCashOut().isEmpty()) {
            throw new ServiceException(ErrorType.PROCESSING, "-001", "Las credenciales de merchant que confirma no estan registrados");
        }

        Request requestDiscount = new Request();

        if (transactionCost) {
            requestDiscount.setAmount(networkDetail.getCostTransaction().toString());
        } else {
            requestDiscount.setAmount(cashOut.getAmountCashOut().toString());
        }

        requestDiscount.setMercode(cashOut.getAgentCodeCashOut());
        requestDiscount.setType(this.configurations.getNameCashoutCompleted());
        requestDiscount.setLanguage1(this.configurations.getLanguage1Complet());
        requestDiscount.setLanguage2(this.configurations.getLanguage2Complet());
        requestDiscount.setBlockSms(this.configurations.getBlockSmsComplet());
        requestDiscount.setSubtype(this.configurations.getSubtype());
        requestDiscount.setTxnMode(this.configurations.getTxnModeComplet());
        requestDiscount.setProvider(this.configurations.getProviderComplet());
        requestDiscount.setPayId2(this.configurations.getPayId2Complet());
        requestDiscount.setPayIdOne(this.configurations.getPayIdComplet());
        requestDiscount.setProvider2(this.configurations.getProvider2Complet());
        requestDiscount.setMsisdn(cashOut.getPhoneNumberCashOut());
        requestDiscount.setFtxnId(request.getCorrelationId() + "-" + (!transactionCost ? "1" : "2"));

        StringBuilder remark = new StringBuilder();
        remark.append("0");
        remark.append("|");
        remark.append(request.getIp());
        remark.append("|");
        remark.append(Constant.LBL_SRV_CASH);
        remark.append("|");
        remark.append(request.getCorrelationId());
        remark.append("|");
        remark.append(request.getIssueDate());
        remark.append("|");
        remark.append(request.getIssuerName()); // IssuerName
        remark.append("|");
        remark.append(request.getPosId());
        remark.append("|");
        remark.append(request.getAgentCode());
        remark.append("|");
        remark.append(request.getIssuerId()); // IssuerID
        remark.append("|");
        remark.append(cashOut.getPhoneNumberCashOut());
        remark.append("|");
        remark.append(request.getUserLogin());
        remark.trimToSize();
        requestDiscount.setRemarks(remark.toString());

        //PROTOCOL_THREE IGUAL (SERVIBANCA)
        if (networkDetail.getProtocol() == PROTOCOL_THREE) {

            StringBuilder cellId = new StringBuilder();
            cellId.append(request.getIdentificationNumber() != null ? request.getIdentificationNumber() : "");
            cellId.append("|");
            cellId.append(request.getTxnId(), FOUR, EIGHT);
            cellId.append("|");
            cellId.append(request.getTxnId(), EIGHT, TWELVE);
            cellId.append("|");
            cellId.append(request.getConciliationDate());
            cellId.trimToSize();
            requestDiscount.setCellId(cellId.toString());

        }

        return sendDiscountBalance(requestDiscount, cashOut, request, networkDetail, transactionCost);

    }

    private CashOut sendDiscountBalance(Request requestDiscount, CashOut cashOut, CashOutRequest request, Network.NetworkDetail networkDetail, boolean transactionCost) throws ServiceException {

        try {
            Response responseMahindra = this.cashRepository.sendMahindraRequest(requestDiscount);
            StatusCode status = statusCodeConfig.of(responseMahindra.getTxnstatus(), responseMahindra.getMessage());

            if (StatusCode.Level.FAIL.equals(status.getLevel())) {
                if (!transactionCost) {
                    throw new ProcessingException(status);
                }
                cashOut.setTransactionCostProcess(Boolean.FALSE);
                cashOut.setStateCashOut(CashOutStatus.TRANSACTION_COST);
            }

            if (!transactionCost) {
                cashOut.setTxnId(responseMahindra.getTxnid());
                cashOut.setStateCashOut(CashOutStatus.COMPLETED);
                if (cashOut.isTransactionCostProcess()) {
                    discountBalance(cashOut, request, networkDetail, true);
                }
            }
        } catch (ServiceException e) {
            if (transactionCost) {
                cashOut.setTransactionCostProcess(Boolean.FALSE);
                cashOut.setStateCashOut(CashOutStatus.TRANSACTION_COST);
            } else {
                throw new ServiceException(e.getErrorType(), e.getCode(), e.getMessage());
            }
        }


        return cashOut;
    }

    private boolean validateTransactionCost(CashOutRequest request, Response loginMh) {
        //excepción (Si el usuario de retiro, es un subsidiario)
        if (configurations.isExceptionA()) {
            Optional<PendingUser> pendingUser = pendingUserRepository.findByDocumentNumberAndProcessTypeAndStatusAndAltered(loginMh.getIdno(), PendingUser.ProcessType.SUBSIDY_REGISTRATION, true, false);
            if (pendingUser.isPresent()) {
                return Boolean.FALSE;
            }
        }

        //excepción (Si el usuario de retiro, es de una "RED N" y saca en la "RED N")
        if (configurations.isExceptionB()) {
            Optional<CashOutExemptUsers> exemptUsersOptional = cashOutExemptUsersRepository.findByPhoneNumber(request.getUserLogin());
            if (exemptUsersOptional.isPresent()) {
                CashOutExemptUsers cashOutExemptUsers = exemptUsersOptional.get();
                Optional<CashOutExemptedNetworks> exemptedNetworksOptional = cashOutExemptedNetworksRepository.findByOriginNetworkAndTargetNetwork(cashOutExemptUsers.getUserNetwork(), request.getAgentCode());
                if (exemptedNetworksOptional.isPresent()) {
                    return Boolean.FALSE;
                }
            }
        }

        return Boolean.TRUE;
    }

    private boolean unFreeze(String txnHold, String correlationId) {
        try {
            Request hr = new Request();
            hr.setType(this.configurations.getNameUnholdMoney());
            hr.setProvider(this.configurations.getProviderAuth());
            hr.setHoldTxnId(txnHold);
            hr.setLanguage1(this.configurations.getLanguage1());
            hr.setRemarks("UNHOLD|".concat(correlationId).concat("|JOB"));
            hr.setFtxnId(String.valueOf(Generator.correlationId(Generator.DATE_FORMAT_MED_CORRELATION_ID, Generator.PIN_2)));

            Response responseMahindra = this.cashRepository.sendMahindraRequest(hr);

            return responseMahindra.getTxnstatus().equals(TRANSACTION_OK) || responseMahindra.getTxnstatus().equals(TRANSACTION_ALREADY_RELEASED);

        } catch (ServiceException e) {
            log.error("Error unfreeze {}", e.getMessage());
        }
        return Boolean.FALSE;
    }

    //Metodos de ayuda
    private Giro registerGiro(CashOutRequest request, Network.NetworkDetail networkDetail, String holdId, Response loginMh) {
        Giro giro = new Giro();
        giro.setAmount(request.getAmount());
        giro.setRegistradoPor(request.getSource());
        giro.setFechaPago(networkDetail.getExpirationDate(networkDetail.getTimeOtpCashout()));
        giro.setFlete(networkDetail.getCostTransaction());
        giro.setSwitchIdEnvio(request.getCorrelationId());
        giro.setReferencia(holdId);
        giro.setEstadoId(CashOutStatus.INITIALIZED.giroCode());
        giro.setLoginOrigen(giroProperties.getLoginOrigen());
        giro.setOrigen(giroProperties.getOrigen());
        giro.setDistOrigen(giroProperties.getDistOrigen());
        giro.setCadenaOrigen(giroProperties.getCadenaOrigen());
        giro.setCadenaDestino(giroProperties.getCadenaDestino());
        giro.setTPidRem(giroProperties.getRemTpid());
        giro.setTPidNombreRem(giroProperties.getRemTpidName());
        giro.setNumIdentificacionRem(giroProperties.getRemNumIdent());
        giro.setNombreRem(giroProperties.getRemNombre());
        giro.setApellidoRem(giroProperties.getRemApellido());
        giro.setCelularRem(giroProperties.getRemCelular());
        giro.setPtoNombreOrg(giroProperties.getOrgNombre());
        giro.setPtoDeptoOrg(giroProperties.getOrgDepto());
        giro.setPtoCiudadOrg(giroProperties.getOrgCiudad());
        giro.setPtoDireccionOrg(giroProperties.getOrgDireccion());
        giro.setPtoBarrioOrg(giroProperties.getOrgBarrio());
        giro.setPtoTelefonoOrg(giroProperties.getOrgTelefono());
        giro.setGiroTpidNombreDest(loginMh.getIdtype());
        giro.setGiroNumeroIdentificacionDest(loginMh.getIdno());
        giro.setGiroNombreDest(loginMh.getFname());
        giro.setGiroApellidoDest(loginMh.getLname());
        giro.setGiroCelularDest(request.getUserLogin());
        giro = giroRepository.save(giro);
        return giro;
    }

    /*metodo que valida si un giro esta completado para que asi el usuario no lo pueda declinar*/
    private void giroValidationProcessComplete(CashOutRequest cashOutRequest) throws ProcessingException {
        log.info("revisando para declinar {}", cashOutRequest.getTransactionId());
        Integer estadoId = giroRepository.findByEnvio(String.valueOf(cashOutRequest.getTransactionId()));
        if (CashOutStatus.COMPLETED.giroCode().equals(estadoId)) {
            throw new ProcessingException(statusCodeConfig.of(Constant.CASHOUT_NOT_DECLINE_COMPLETED, StatusCode.Level.FAIL));
        }
    }

    private Data validateSignature(CashOut cashOut) {
        Data data = null;
        try {
            String signature = this.signaturaHelper.signCashOut(cashOut);
            if (!signature.equals(cashOut.getSignatureCashOut())) {
                throw new DataException(statusCodeConfig.of(Constant.BAD_SIGNATURE));
            }
        } catch (DataException | ParsingException e) {
            log.error("altered - cashOut declined- ERROR : {}", e.getMessage());
            data = new Data();
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.BAD_SIGNATURE, StatusCode.Level.FAIL).getCode());
            data.setErrorMessage(statusCodeConfig.of(Constant.BAD_SIGNATURE, StatusCode.Level.FAIL).getMessage());
        }
        return data;

    }

    private com.moviired.client.supportotp.Response createOtp(CashOutRequest request, Response loginMh, Network.NetworkDetail networkDetail, boolean sendSms) throws ServiceException, ManagerException {

        IOtpManager otpManager = this.otpManagerFactory.getOtpManager(networkDetail.getGeneratorOtp());
        OtpDTO receptor = OtpDTO.builder()
                .correlationId(request.getCorrelationId())
                .documentType(loginMh.getIdtype())
                .documentNumber(loginMh.getIdno())
                .phoneNumber(request.getUserLogin())
                .name(loginMh.getFname() + " " + loginMh.getLname())
                .email(loginMh.getEmail())
                .amount(request.getAmount())
                .otpLength(networkDetail.getLengthOTP())
                .timeExpired(networkDetail.getTimeOtpCashout())
                .sendSms(sendSms)
                .build();

        return otpManager.generate(receptor);
    }

    private List<CashOutResponse> processCreateToken(CashOutRequest request, Network.NetworkDetail networkDetail, Response loginMh) throws ServiceException, ManagerException {
        List<CashOutResponse> pendingResponse;

        // Verificar Cashout initialized
        CashOut cashOut = cashOutRepository.findByAgentCodeCashOutAndStateCashOutAndOtpAndPhoneNumberCashOut(request.getAgentCode(), CashOutStatus.INITIALIZED, request.getOtp(), request.getPhoneNumber());
        if (null == cashOut) {
            throw new ProcessingException(statusCodeConfig.of(Constant.CASH_OUT_NO_FOUND, StatusCode.Level.FAIL));
        }

        // Validar el otp
        IOtpManager otpManager = this.otpManagerFactory.getOtpManager(networkDetail.getGeneratorOtp());
        com.moviired.client.supportotp.Response responseValidateOtp = otpManager.isValid(request.getPhoneNumber(), request.getOtp());

        if (statusCodeConfig.of(Constant.OTP_NOT_FOUND, StatusCode.Level.FAIL).getCode().equals(responseValidateOtp.getResponseCode())) {
            throw new ProcessingException(statusCodeConfig.of(Constant.OTP_NOT_FOUND, StatusCode.Level.FAIL));
        }

        // Crear TOKEN de seguridad (OJO)
        com.moviired.client.supportotp.Response responseCreateOtp = createOtp(request, loginMh, networkDetail, false);
        if (responseCreateOtp.getOtp() == null || !responseCreateOtp.getResponseCode().equals(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode())) {
            throw new ProcessingException(statusCodeConfig.of(Constant.OTP_NOT_GENERATED, StatusCode.Level.FAIL));
        }

        // Actualizar nuevo TOKEN en CashOut
        cashOut.setOtp(responseCreateOtp.getOtp());
        cashOut.setExpirationDateCashOut(networkDetail.getExpirationDate(networkDetail.getTimeOtpComplete()));
        cashOut = this.cashOutRepository.save(cashOut);

        // Establecer la respuesta
        List<CashOut> pendings = new ArrayList<>();
        pendings.add(cashOut);
        pendingResponse = pendings.stream().map(cashOutResponse -> new CashOutResponse(
                cashOutResponse.getId().toString(),
                cashOutResponse.getStateCashOut().value(),
                cashOutResponse.getAmountCashOut(),
                responseCreateOtp.getOtp())).collect(Collectors.toList());


        return pendingResponse;
    }

    private void validateCorrelationId(String correlationId, String agentCode, Network.Type type) throws DataException {
        List<CashOut> cashOutCorrelationId = cashOutRepository.findByCorrelationIdOrCorrelationIdCompleteAndAgentCodeCashOut(correlationId, correlationId, agentCode);
        if (!cashOutCorrelationId.isEmpty()) {
            throw new DataException(statusCodeConfig.of(Constant.INVALID_CORRELATION_ID));
        }

        if (Network.Type.ALLIES.equals(type) && correlationId.matches(".*[a-z].*")) {
            throw new DataException(statusCodeConfig.of(Constant.INVALID_CORRELATION_ID));
        }
    }

}
