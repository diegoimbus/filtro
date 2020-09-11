package co.moviired.supportp2pvalidatortransaction.service;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraConnector;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraDTO;
import co.moviired.supportp2pvalidatortransaction.common.service.IService;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import co.moviired.supportp2pvalidatortransaction.config.ComponentProperties;
import co.moviired.supportp2pvalidatortransaction.model.entity.FrozenBalance;
import co.moviired.supportp2pvalidatortransaction.model.entity.MoneyRequest;
import co.moviired.supportp2pvalidatortransaction.provider.supportsms.SupportSMSConnector;
import co.moviired.supportp2pvalidatortransaction.repository.IFrozenBalanceRepository;
import co.moviired.supportp2pvalidatortransaction.repository.IMoneyRequestRepository;
import co.moviired.supportp2pvalidatortransaction.util.Constants;
import co.moviired.supportp2pvalidatortransaction.util.CypherHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.MAHINDRA_API;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.INITIALIZED;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.SUPPORT_SMS_API;

@Slf4j
@Service
public class SupportP2pTransactionsService extends IService {

    private final MahindraConnector mahindraConnector;
    private final SupportSMSConnector supportSMSConnector;

    private final IFrozenBalanceRepository frozenBalanceRepository;
    private final IMoneyRequestRepository moneyRequestRepository;

    private final ComponentProperties componentProperties;

    private static final Locale locale = new Locale("es");
    private static final NumberFormat formatter = NumberFormat.getInstance(locale);

    public SupportP2pTransactionsService(@NotNull GlobalProperties globalProperties,
                                         @NotNull StatusCodeConfig statusCodeConfig,
                                         @NotNull @Qualifier(MAHINDRA_API) MahindraConnector mahindraConnector,
                                         @NotNull @Qualifier(SUPPORT_SMS_API) SupportSMSConnector supportSMSConnector,
                                         @NotNull IFrozenBalanceRepository frozenBalanceRepository,
                                         @NotNull IMoneyRequestRepository moneyRequestRepository,
                                         @NotNull ComponentProperties componentProperties) {
        super(globalProperties, statusCodeConfig);
        this.mahindraConnector = mahindraConnector;
        this.supportSMSConnector = supportSMSConnector;
        this.frozenBalanceRepository = frozenBalanceRepository;
        this.moneyRequestRepository = moneyRequestRepository;

        this.componentProperties = componentProperties;
    }

    synchronized Mono<IComponentDTO> validateP2pTransactions(String correlative) {
        return Mono.just(correlative)
                .flatMap(c -> {
                    cancelSendMoneyTransactions(correlative);
                    cancelRequestMoneyTransactionsForTimeOut(correlative);
                    retryConfirmationOrCancelRequestMoneyTransaction(correlative);
                    return Mono.just(new IComponentDTO());
                });
    }

    // CANCEL SEND MONEY ***********************************************************************************************

    /**
     * Cancel send money transactions with fails for time out
     * This method cancel transactions with state different to 200, 201 (Failed transactions and Initialized transactions)
     */
    private void cancelSendMoneyTransactions(String correlative) {
        Utils.assignCorrelative(globalProperties, correlative);

        List<FrozenBalance> frozenBalanceList = this.frozenBalanceRepository.getTransactionsWithTimeOut("200", "201",
                PageRequest.of(0, componentProperties.getQueryLimitResults()));

        if (frozenBalanceList.isEmpty()) {
            log.info(Constants.LBL_NO_EXISTEN_TRANSACCIONES);
            return;
        }

        List<Mono<Boolean>> cancelSendMoneyTransactions = frozenBalanceList.stream()
                .map(this::cancelSendMoneyTransaction).collect(Collectors.toList());
        Mono.zip(cancelSendMoneyTransactions, Arrays::asList).flatMapIterable(objects -> objects).subscribe();
    }

    private Mono<Boolean> cancelSendMoneyTransaction(FrozenBalance frozenBalance) {
        AtomicLong startTime = new AtomicLong();
        AtomicReference<String> correlative = new AtomicReference<>();
        return Mono.just(frozenBalance)
                .flatMap(fb -> {
                    correlative.set(Utils.assignCorrelative(globalProperties, null));
                    log.info("Start cancel of send money transaction with id: {}", frozenBalance.getId());
                    startTime.set(System.currentTimeMillis());
                    if (frozenBalance.getRetries() == null) frozenBalance.setRetries(0);
                    if ("FrozenBalance".equals(frozenBalance.getTxId()) && frozenBalance.getResponseUHMoney() == null)
                        return callMahindraDefrost(frozenBalance, correlative.get());
                    else return Mono.just(true);
                })
                .flatMap(isSuccess -> {
                    frozenBalance.setTimeResponseHMoney(System.currentTimeMillis() - startTime.get());
                    frozenBalance.setRetries(frozenBalance.getRetries() + 1);
                    if (isSuccess || frozenBalance.getRetries() >= 3) {
                        frozenBalance.setState("201");
                        frozenBalance.setStatusRetries("00");
                        log.info("Cancel successful for send money transaction with id: {}", frozenBalance.getId());
                    } else
                        log.info("Cancel fail for send money transaction with id: {}, retries: {}", frozenBalance.getId(), frozenBalance.getRetries());
                    this.frozenBalanceRepository.save(frozenBalance);
                    log.info("End cancel of send money transaction with id: {}", frozenBalance.getId());
                    return Mono.just(true);
                });
    }

    // CANCEL REQUEST MONEY ********************************************************************************************

    /**
     * Cancel request money transactions for time out of 24 hours
     * these transactions were neither accepted nor rejected for the final user or is failed
     * state 200 = accepted or rejected for the final user
     * state 201 = cancel for the system
     */
    private void cancelRequestMoneyTransactionsForTimeOut(String correlative) {
        Utils.assignCorrelative(globalProperties, correlative);

        List<MoneyRequest> moneyRequestList = this.moneyRequestRepository.getFailsMoneyRequest("200", "201",
                PageRequest.of(0, componentProperties.getQueryLimitResults()));

        if (moneyRequestList.isEmpty()) {
            log.info(Constants.LBL_NO_EXISTEN_TRANSACCIONES);
            return;
        }

        for (MoneyRequest moneyRequest : moneyRequestList) {
            try {
                cancelMoneyRequest(moneyRequest, moneyRequest.getMessage());
            } catch (Exception e) {
                log.error(Constants.ERROR_EXCEPTION + e.getMessage());
            }
        }
    }

    /**
     *
     */
    private void retryConfirmationOrCancelRequestMoneyTransaction(String correlative) {
        Utils.assignCorrelative(globalProperties, correlative);

        List<MoneyRequest> moneyRequestList = this.moneyRequestRepository.getFailsMoneyRequest("200", INITIALIZED, "201",
                PageRequest.of(0, componentProperties.getQueryLimitResults()));

        if (moneyRequestList.isEmpty()) {
            log.info(Constants.LBL_NO_EXISTEN_TRANSACCIONES);
            return;
        }

        List<Mono<Boolean>> cancelRequestMoneyTransactions = moneyRequestList.stream()
                .map(this::retryConfirmationOrCancelRequestMoneyTransaction).collect(Collectors.toList());
        Mono.zip(cancelRequestMoneyTransactions, Arrays::asList).flatMapIterable(objects -> objects).subscribe();
    }

    private Mono<Boolean> retryConfirmationOrCancelRequestMoneyTransaction(MoneyRequest moneyRequest) {
        AtomicReference<String> correlative = new AtomicReference<>();
        AtomicReference<MahindraDTO> usrQueryInfoResponse = new AtomicReference<>();
        return Mono.just(moneyRequest)
                .flatMap(mr -> {
                    correlative.set(Utils.assignCorrelative(globalProperties, null));
                    return callMahindraUserQueryInfo(moneyRequest.getMsisdn2(), correlative.get());
                }).flatMap(response -> {
                    usrQueryInfoResponse.set(response);
                    if (moneyRequest.getRetries() == null || moneyRequest.getRetries() == 0) {
                        moneyRequest.setRetries(0);
                        moneyRequest.setState("99");
                        moneyRequest.setMessage("NO se ha realizado la transaccion en el core.");
                        this.moneyRequestRepository.save(moneyRequest);
                    }
                    return callMahindraAskForMoney(moneyRequest, correlative.get());
                }).flatMap(askForMoneyResponse -> handleAskFormMoneyResponse(moneyRequest, usrQueryInfoResponse.get(), askForMoneyResponse, correlative.get()))
                .onErrorResume(e -> {
                    log.error("Error occurred: {}", e.getMessage());
                    moneyRequest.setRetries(moneyRequest.getRetries() + 1);
                    if (moneyRequest.getRetries() >= 3) {
                        moneyRequest.setStatusRetries("00");
                        moneyRequest.setState("201");
                        sendSMSError(moneyRequest, correlative.get());
                    }
                    this.moneyRequestRepository.save(moneyRequest);
                    return Mono.just(false);
                });
    }

    private Mono<Boolean> handleAskFormMoneyResponse(MoneyRequest moneyRequest, MahindraDTO usrQueryInfoResponse, MahindraDTO askForMoneyResponse, String correlative) {
        return Mono.just(moneyRequest)
                .flatMap(mr -> {
                    // Handle success response
                    if (askForMoneyResponse.getTxnStatus().matches(Constants.TRANSACTION_OK))
                        return handleSuccessAskForMoneyResponse(moneyRequest, usrQueryInfoResponse, askForMoneyResponse, correlative);

                    // Handle error response
                    if (askForMoneyResponse.getTxnStatus().matches("000680")) {
                        sendSMSError(moneyRequest, correlative);
                        cancelMoneyRequest(moneyRequest, askForMoneyResponse.getMessage());
                    } else {
                        moneyRequest.setRequestMoney(getAskForMoneyRequest(moneyRequest).protectedToString());
                        moneyRequest.setResponseMoney(askForMoneyResponse.protectedToString());
                        moneyRequest.setTxId(askForMoneyResponse.getTxnId());
                        if (moneyRequest.getProcessType().equals("1")) {
                            cancelMoneyRequest(moneyRequest, askForMoneyResponse.getMessage());
                        } else {
                            moneyRequest.setState(askForMoneyResponse.getTxnStatus());
                            moneyRequest.setMessage(askForMoneyResponse.getMessage());
                            moneyRequest.setDateUpdate(new Date());
                            if (moneyRequest.getRetries() >= 3) {
                                moneyRequest.setStatusRetries(null);
                                moneyRequest.setState(INITIALIZED);
                                sendSMSError(moneyRequest, correlative);
                            }
                            moneyRequest.setRetries(moneyRequest.getRetries() + 1);
                            this.moneyRequestRepository.save(moneyRequest);
                        }
                    }
                    return Mono.just(true);
                });
    }

    private Mono<Boolean> handleSuccessAskForMoneyResponse(MoneyRequest moneyRequest, MahindraDTO usrQueryInfoResponse, MahindraDTO askForMoneyResponse, String correlative) {
        return Mono.just(moneyRequest)
                .flatMap(mr -> {
                    moneyRequest.setState(askForMoneyResponse.getTxnStatus());
                    moneyRequest.setStatusRetries("00");
                    moneyRequest.setDateUpdate(new Date());
                    moneyRequest.setTxId(askForMoneyResponse.getTxnId());
                    moneyRequest.setRequestMoney(getAskForMoneyRequest(moneyRequest).protectedToString());
                    moneyRequest.setResponseMoney(askForMoneyResponse.protectedToString());
                    moneyRequest.setMessage(askForMoneyResponse.getMessage());
                    moneyRequest.setState(INITIALIZED);
                    moneyRequest.setStatusRetries(null);
                    moneyRequest.setStateInitJob(null);
                    this.moneyRequestRepository.save(moneyRequest);
                    return Mono.just(moneyRequest);
                })
                .flatMap(mr -> {
                    if (moneyRequest.getTransactionCodeConfirm() == null) return Mono.just(true);
                    else {
                        if (moneyRequest.getProcessType().equals("1")) {
                            cancelMoneyRequest(moneyRequest, askForMoneyResponse.getMessage());
                            return Mono.just(true);
                        }
                        moneyRequest.setState("99");
                        moneyRequest.setProcessType("0");
                        moneyRequest.setMessage("NO se ha realizado la transaccion en el core.");

                        this.moneyRequestRepository.save(moneyRequest);
                        return invokeRequestMoneyComplete(moneyRequest, correlative)
                                .flatMap(responseRequestMoneyComplete -> handleRequestMoneyCompleteResponse(moneyRequest, usrQueryInfoResponse, askForMoneyResponse, responseRequestMoneyComplete, correlative))
                                .onErrorResume(e -> {
                                    log.error(Constants.ERROR_EXCEPTION + e.getMessage());
                                    if (e.getCause().getClass().equals(IOException.class)) {
                                        moneyRequest.setState("201");
                                    } else {
                                        moneyRequest.setState(askForMoneyResponse.getTxnStatus());
                                        if (moneyRequest.getRetries() >= 3) {
                                            moneyRequest.setStatusRetries(null);
                                            moneyRequest.setState(INITIALIZED);
                                            sendSMSError(moneyRequest, correlative);
                                        }
                                        moneyRequest.setRetries(moneyRequest.getRetries() + 1);
                                        moneyRequest.setStateInitJob("Fail");
                                    }
                                    this.moneyRequestRepository.save(moneyRequest);
                                    return Mono.just(false);
                                });
                    }
                });
    }

    private Mono<Boolean> handleRequestMoneyCompleteResponse(MoneyRequest moneyRequest, MahindraDTO responseValidateUser, MahindraDTO responseAskForMoney, MahindraDTO responseSendMoneyCompleted, String correlative) {
        return Mono.just(moneyRequest)
                .flatMap(mr -> {
                    if (!responseSendMoneyCompleted.getTxnStatus().matches(Constants.TRANSACTION_OK)) {
                        if (responseAskForMoney.getTxnStatus().matches("000680")) {
                            cancelMoneyRequest(moneyRequest, responseAskForMoney.getMessage());
                            sendSMSError(moneyRequest, correlative);
                        } else {
                            moneyRequest.setMessage(responseSendMoneyCompleted.getMessage());
                            moneyRequest.setDateUpdate(new Date());
                            moneyRequest.setRequestMoneyConfirmation(getRequestMoneyComplete(moneyRequest).protectedToString());
                            moneyRequest.setResponseMoneyConfirmation(responseSendMoneyCompleted.protectedToString());
                            moneyRequest.setTxIdConfirmation(responseSendMoneyCompleted.getTxnId());
                            moneyRequest.setStateInitJob("Fail");
                            moneyRequest.setState(responseAskForMoney.getTxnStatus());
                            if (moneyRequest.getRetries() >= 3) {
                                moneyRequest.setStatusRetries(null);
                                moneyRequest.setState(INITIALIZED);
                                sendSMSError(moneyRequest, correlative);
                            }
                            moneyRequest.setRetries(moneyRequest.getRetries() + 1);

                        }
                        this.moneyRequestRepository.save(moneyRequest);
                    } else {
                        moneyRequest.setState(responseSendMoneyCompleted.getTxnStatus());
                        moneyRequest.setStatusRetries("00");
                        moneyRequest.setDateUpdate(new Date());
                        moneyRequest.setTxIdConfirmation(responseSendMoneyCompleted.getTxnId());
                        moneyRequest.setRequestMoneyConfirmation(getRequestMoneyComplete(moneyRequest).protectedToString());
                        moneyRequest.setResponseMoneyConfirmation(responseSendMoneyCompleted.protectedToString());
                        moneyRequest.setMessage(responseSendMoneyCompleted.getMessage());
                        try {
                            String message = messageSmsAskForMoneyMovii(responseValidateUser.getFName(), "$" + formatter.format(moneyRequest.getAmount()));
                            moneyRequest.setMessageText2(message);
                            sendSms(moneyRequest.getMsisdn(), message, correlative);
                            moneyRequest.setStatusSms2("true");
                        } catch (Exception e) {
                            moneyRequest.setStatusSms2("false");
                            log.error(Constants.ERROR_EXCEPTION + e.getMessage());
                        }
                        this.moneyRequestRepository.save(moneyRequest);

                        try {
                            String message = supportSMSConnector.properties.getSuccessSendMoneySMS().replace("$MONTO", "$" + formatter.format(moneyRequest.getAmount()));
                            sendSms(moneyRequest.getMsisdn2(), message, correlative);
                        } catch (Exception e) {
                            log.error(Constants.ERROR_EXCEPTION + e.getMessage());
                        }
                    }

                    return Mono.just(true);
                });
    }

    private void cancelMoneyRequest(MoneyRequest moneyRequest, String message) {
        moneyRequest.setState("201");
        moneyRequest.setStatusRetries("00");
        moneyRequest.setDateUpdate(new Date());
        moneyRequest.setMessage(message);
        this.moneyRequestRepository.save(moneyRequest);
    }

    // INVOKE MAHINDRA *************************************************************************************************

    private Mono<Boolean> callMahindraDefrost(FrozenBalance frozenBalance, String correlative) {
        return mahindraConnector.invoke(mahindraConnector.factory.requestUFrozenBalance(frozenBalance.getTxidHmoney()), correlative, "DEFROST BALANCE")
                .flatMap(mahindraDTO -> {
                    if (mahindraDTO.getMessage() != null) frozenBalance.setMessage(mahindraDTO.getMessage());
                    return onMahindraResponse(mahindraDTO, correlative);
                })
                .flatMap(mahindraDTO -> Mono.just(true))
                .onErrorResume(e -> Mono.just(false));
    }

    private Mono<MahindraDTO> callMahindraUserQueryInfo(String msisdn, String correlative) {
        return mahindraConnector.invoke(mahindraConnector.factory.createValidateUser(msisdn), correlative, "USER QUERY INFO")
                .flatMap(mahindraDTO -> onMahindraResponse(mahindraDTO, correlative));
    }

    private Mono<MahindraDTO> callMahindraAskForMoney(MoneyRequest moneyRequest, String correlative) {
        return mahindraConnector.invoke(getAskForMoneyRequest(moneyRequest), correlative, "ASK FOR MONEY");
    }

    private Mono<MahindraDTO> invokeRequestMoneyComplete(MoneyRequest moneyRequest, String correlative) {
        return mahindraConnector.invoke(getRequestMoneyComplete(moneyRequest), correlative, "SEND MONEY COMPLETE");
    }

    private MahindraDTO getRequestMoneyComplete(MoneyRequest moneyRequest) {
        return mahindraConnector.factory.getRequestMoneyConfirmation(
                moneyRequest.getMsisdn2(), moneyRequest.getMsisdn2(), "0", moneyRequest.getTxId(),
                CypherHelper.decrypt(moneyRequest.getTransactionCodeConfirm(), this.componentProperties.getSecurityPassword()));
    }

    private MahindraDTO getAskForMoneyRequest(MoneyRequest moneyRequest) {
        return mahindraConnector.factory.requestAskForMoney(
                moneyRequest.getAmount(), moneyRequest.getMsisdn(), moneyRequest.getMsisdn2(),
                CypherHelper.decrypt(moneyRequest.getTransactionCode(), this.componentProperties.getSecurityPassword()));
    }

    // INVOKE SUPPORT SMS **********************************************************************************************

    private void sendSMSError(MoneyRequest moneyRequest, String correlative) {
        try {
            String message = supportSMSConnector.properties.getErrorSendMoneySMS().replace("$MONTO", "$" + formatter.format(moneyRequest.getAmount()));
            sendSms(moneyRequest.getMsisdn2(), message, correlative);
        } catch (Exception e) {
            log.error(Constants.ERROR_EXCEPTION + e.getMessage());
        }
    }

    private void sendSms(String msisdn, String body, String correlative) {
        supportSMSConnector.invoke(this.supportSMSConnector.properties.getCellPrefix() + msisdn, body, correlative, "SEND SMS")
                .subscribe();
    }

    private String messageSmsAskForMoneyMovii(String name, String amount) {
        return supportSMSConnector.properties.getGenericMessage()
                .replace("{NAME}", name)
                .replace("{STATUS_MESSAGE}", "te acaba de enviar")
                .replace("{AMOUNT}", amount)
                .replace("{FACE}", " =).");
    }
}

