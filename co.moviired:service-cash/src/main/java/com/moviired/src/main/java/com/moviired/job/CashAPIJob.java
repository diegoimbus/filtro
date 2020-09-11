package com.moviired.job;

import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.util.Generator;
import com.moviired.client.mahindra.command.Request;
import com.moviired.client.mahindra.command.Response;
import com.moviired.excepciones.ManagerException;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import com.moviired.manager.IOtpManager;
import com.moviired.manager.OtpManagerFactory;
import com.moviired.model.Configurations;
import com.moviired.model.Network;
import com.moviired.model.entities.cashservice.CashOut;
import com.moviired.model.entities.giros.Giro;
import com.moviired.model.enums.CashOutStatus;
import com.moviired.model.request.RequestFormat;
import com.moviired.model.response.Data;
import com.moviired.properties.GiroProperties;
import com.moviired.repository.ICashRepository;
import com.moviired.repository.jpa.cashservice.ICashOutRepository;
import com.moviired.repository.jpa.giros.IGiroRepository;
import com.moviired.service.MoviiredCashApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class CashAPIJob implements Serializable {

    private static final long serialVersionUID = -2588474642455532479L;
    private static final String TRANSACTION_OK = "200";
    private final ICashOutRepository cashOutRepository;
    private final ICashRepository cashRepository;
    private final Configurations configurations;
    private final Network network;
    private final IGiroRepository giroRepository;
    private final int ipAddress;
    private final GiroProperties giroProperties;
    private final OtpManagerFactory otpManagerFactory;
    private final MoviiredCashApi moviiredCashApi;

    /**
     * CRON que busca los cashout en estado iniciados que han superado su tiempo de caducidad y finaliza los cashout
     */

    @Scheduled(fixedRateString = "${properties.job.timeValidateTxExpiredInMillis}")
    public void cronTxExpired() {
        if (Utilidad.validateShift(ipAddress)) {
            return;
        }

        try {

            Set<CashOut> txExpiredList = cashOutRepository.findExpired(new Date());
            Set<Integer> cashOutsId = txExpiredList.stream().map(CashOut::getId).collect(Collectors.toSet());
            cashOutRepository.updateAll(cashOutsId, true);
            log.info("Transacciones vencidas, reversadas o declinadas {}", txExpiredList.size());

            txExpiredList.forEach(cashOut -> {
                log.info("Transaccion : {} Estado : {}", cashOut.getId(), cashOut.getStateCashOut());
                Utilidad.assignCorrelative(cashOut.getCorrelationId());

                if (CashOutStatus.REVERSED.equals(cashOut.getStateCashOut())) {

                    cashOut = processTransactionReverse(cashOut);

                } else if (CashOutStatus.REVERSED_TRANSACTION_COST.equals(cashOut.getStateCashOut())) {
                    if (reverseTransactionCost(cashOut)) {
                        cashOut.setProcessedCashOut(Boolean.TRUE);
                        cashOut.setStateCashOut(CashOutStatus.REVERSED);
                    }
                } else {
                    cashOut = processTransactionUnfrezze(cashOut);

                }

                updateGiro(cashOut);
                cashOutRepository.save(cashOut);
            });
            cashOutRepository.updateAll(cashOutsId, false);

            assignCorrelative(null);
        } catch (JDBCConnectionException e) {
            log.info("Error job conTxExpired {}", e.getMessage());
        }
    }

    private CashOut processTransactionReverse(CashOut cashOut){
        if (refund(cashOut)) {
            if (cashOut.isTransactionCostProcess()) {
                if (reverseTransactionCost(cashOut)) {
                    cashOut.setProcessedCashOut(Boolean.TRUE);
                } else {
                    cashOut.setStateCashOut(CashOutStatus.REVERSED_TRANSACTION_COST);
                }
            } else {
                cashOut.setProcessedCashOut(Boolean.TRUE);
            }
        }
        return cashOut;
    }

    /**
     * Este es el job que se utiliza para completar un cashOut via giro (Ejemplo : EXITO).
     *
     * @param
     * @return Nada.
     */
    @Scheduled(fixedRateString = "${properties.job.timeGiroComplete}")
    public void cronGiroComplete() {

        //Buscar giros completados
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - configurations.getTimeFindCronGiroComplete());
        Date dateFilter = date.getTime();

        log.info("Buscando cashOut completados via giro ...");

        Set<Giro> txGiroAliado = giroRepository.findByFechaRegistroGreaterThanAndOrigenAndEstadoId(dateFilter, giroProperties.getOrigen(), CashOutStatus.COMPLETED.giroCode());
        txGiroAliado.forEach(giro -> {
            Optional<CashOut> ocashOut = cashOutRepository.findByGiroIdAndStateCashOutAndTakenCashOutAndProcessedCashOut(giro.getGiroId(), CashOutStatus.INITIALIZED, false, false);
            Set<Integer> cashOutsId = ocashOut.stream().map(CashOut::getId).collect(Collectors.toSet());
            cashOutRepository.updateAll(cashOutsId, true);
            if (ocashOut.isPresent()) {
                CashOut cashOut = ocashOut.get();
                Utilidad.assignCorrelative(cashOut.getCorrelationId());
                log.info("completar cashOut via giro : " + cashOut.getId());
                if (CashOutStatus.INITIALIZED.equals(cashOut.getStateCashOut())) {
                    cashOut.setStateCashOut(CashOutStatus.COMPLETED);
                }

                try {
                    Network.NetworkDetail networkDetail = network.getOption(cashOut.getAgentCodeCashOut());
                    IOtpManager otpManager = this.otpManagerFactory.getOtpManager(networkDetail.getGeneratorOtp());
                    otpManager.isValid(cashOut.getPhoneNumberCashOut(), cashOut.getOtp());

                    cashOut.setProcessedCashOut(Boolean.TRUE);
                    cashOut.setTakenCashOut(Boolean.TRUE);
                    cashOut.setIssuerDateCashOut(new Date());
                    cashOutRepository.save(cashOut);

                } catch (ManagerException | ServiceException e) {
                    log.error("Error CashApi :{}", e.getMessage());
                }
            }
            cashOutRepository.updateAll(cashOutsId, false);
        });
        assignCorrelative(null);
    }

    /**
     * Job que se utiliza para devolver el costo de la transaccion si hubo algun fallo en la devolución.
     *
     * @param
     * @return Nada.
     */
    @Scheduled(fixedRateString = "${properties.job.timeTransactionCost}")
    public void cronTransactionCost() {
        //Buscar giros completados
        log.info("Buscando cashOut por pendiente cobro de transacción ");

        List<CashOut> listCashOuts = cashOutRepository.findByStateCashOut(CashOutStatus.TRANSACTION_COST);
        Set<Integer> cashOutsId = listCashOuts.stream().map(CashOut::getId).collect(Collectors.toSet());
        cashOutRepository.updateAll(cashOutsId, true);
        listCashOuts.forEach(cashOut -> {
            log.info("Procesando costo de transacción [ cashOut : " + cashOut.getId() + " ]");
            try {
                if (discountTransactionCost(cashOut)) {
                    cashOut.setStateCashOut(CashOutStatus.COMPLETED);
                    cashOut.setTransactionCostProcess(Boolean.TRUE);
                    cashOutRepository.save(cashOut);
                }
            } catch (Exception e) {
                log.error("Error CashApiJob cronTransactionCost:{}", e.getMessage());
            }
        });
        cashOutRepository.updateAll(cashOutsId, false);
    }

    private CashOut processTransactionUnfrezze(CashOut cashOut) {

        Response responseMahindra = unFreeze(cashOut.getTxnHold(), cashOut.getCorrelationId());

        if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {
            if (CashOutStatus.INITIALIZED.equals(cashOut.getStateCashOut())) {
                cashOut.setStateCashOut(CashOutStatus.EXPIRED);
            }
            cashOut.setProcessedCashOut(Boolean.TRUE);
            cashOut.setTakenCashOut(Boolean.TRUE);
        }

        if ((!responseMahindra.getTxnstatus().equals(Constant.TRANSACTION_ERROR_500)) && (!responseMahindra.getTxnstatus().equals(TRANSACTION_OK))) {

            cashOut.setStateCashOut(CashOutStatus.UNFREEZE_WRONG);
            cashOut.setProcessedCashOut(Boolean.TRUE);
            cashOut.setTakenCashOut(Boolean.TRUE);
        }

        return cashOut;
    }

    private boolean reverseTransactionCost(CashOut cashOut) {

        //coment
        Network.NetworkDetail networkDetail = network.getOption(cashOut.getAgentCodeCashOut());

        RequestFormat request = new RequestFormat();
        request.setCorrelationId(cashOut.getCorrelationIdComplete().concat("-2R"));
        request.setIssueDate(Utilidad.dateFormat(new Date()));
        request.setIssuerId(cashOut.getTxnId());
        request.setIssuerName("REVERSE|JOB|TRANSACTION COST");
        request.setSource("'CHANNEL BY SUBSCRIBER");
        request.setAmount(cashOut.getTransactionCost());
        request.setUsuario(networkDetail.getMsisdn());
        request.setMpin(networkDetail.getPin());
        request.setPhoneNumber(cashOut.getPhoneNumberCashOut());
        request.setMerchantId(cashOut.getAgentCodeCashOut());
        request.setPosId("REVERSO " + networkDetail.getName());

        Data data = moviiredCashApi.cashIn(request);

        if (data.getErrorCode().equals(Constant.TRANSACTION_OK_00)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private void updateGiro(CashOut cashOut) {
        Giro giro = null;
        // Actualiza el estado del registro de giro
        Network.NetworkDetail networkDetail = network.getOption(cashOut.getAgentCodeCashOut());
        if ((null != cashOut.getGiroId()) && (Network.Type.ALLIES.equals(networkDetail.getType()))) {
            giro = giroRepository.findById(cashOut.getGiroId()).orElse(null);
        }

        if (null != giro) {
            // Validacion de estados
            if (CashOutStatus.COMPLETED.giroCode().equals(giro.getEstadoId()) && !CashOutStatus.REVERSED.equals(cashOut.getStateCashOut())) {
                cashOut.setStateCashOut(CashOutStatus.COMPLETED);
                cashOutRepository.save(cashOut);
            } else {
                giro.setEstadoId(cashOut.getStateCashOut().giroCode());
                giroRepository.save(giro);
            }
        }
    }

    /**
     * Metodo de descongelamiento de dinero.
     *
     * @param txnHold,correlationId
     * @return Nada.
     */
    public Response unFreeze(String txnHold, String correlationId) {
        Response responseMahindra = new Response();
        try {
            Request hr = new Request();
            hr.setType(this.configurations.getNameUnholdMoney());
            hr.setProvider(this.configurations.getProviderAuth());
            hr.setHoldTxnId(txnHold);
            hr.setLanguage1(this.configurations.getLanguage1());
            hr.setRemarks("UNHOLD|".concat(correlationId).concat("|JOB"));
            hr.setFtxnId(String.valueOf(Generator.correlationId(Generator.DATE_FORMAT_MED_CORRELATION_ID, Generator.PIN_2)));

            return this.cashRepository.sendMahindraRequest(hr);

        } catch (ServiceException e) {
            responseMahindra.setTxnstatus(e.getCode());
            responseMahindra.setMessage(e.getMessage());
            return responseMahindra;
        }
    }

    private boolean discountTransactionCost(CashOut cashOut) {
        try {
            Request requestDiscount = new Request();
            requestDiscount.setAmount(String.valueOf(cashOut.getTransactionCost()));
            requestDiscount.setType(this.configurations.getNameCashoutCompleted());
            requestDiscount.setLanguage1(this.configurations.getLanguage1Complet());
            requestDiscount.setLanguage2(this.configurations.getLanguage2Complet());
            requestDiscount.setBlockSms(this.configurations.getBlockSmsComplet());
            requestDiscount.setSubtype(this.configurations.getSubtype());
            requestDiscount.setTxnMode(this.configurations.getTxnModeComplet());
            requestDiscount.setMercode(cashOut.getAgentCodeCashOut());
            requestDiscount.setProvider(this.configurations.getProviderComplet());
            requestDiscount.setPayId2(this.configurations.getPayId2Complet());
            requestDiscount.setPayIdOne(this.configurations.getPayIdComplet());
            requestDiscount.setProvider2(this.configurations.getProvider2Complet());
            requestDiscount.setMsisdn(cashOut.getPhoneNumberCashOut());
            requestDiscount.setFtxnId(cashOut.getCorrelationIdComplete().concat("-2-JOB"));
            StringBuilder remark = new StringBuilder();
            remark.append("0");
            remark.append("|");
            remark.append("JOB TRANSACTION COST");
            remark.append("|");
            remark.append(cashOut.getCorrelationId());

            Response responseMahindra = this.cashRepository.sendMahindraRequest(requestDiscount);

            return responseMahindra.getTxnstatus().equals(TRANSACTION_OK) || responseMahindra.getTxnstatus().equals(Constant.TRANSACTION_ALREADY_COMPLETED);

        } catch (ServiceException e) {
            log.error("Error discountTransactionCost", e.getMessage());
        }
        return Boolean.FALSE;
    }

    private boolean refund(CashOut cashOut) {
        try {
            Request rr = new Request();
            rr.setType(this.configurations.getNameReverso());
            rr.setFtxnId(cashOut.getCorrelationIdComplete() != null ? cashOut.getCorrelationIdComplete().concat("-1").concat("R") : "0");
            rr.setFtxnIdOrg(cashOut.getCorrelationIdComplete() != null ? cashOut.getCorrelationIdComplete().concat("-1") : "0");
            rr.setAmount(cashOut.getAmountCashOut().toString());
            rr.setIsTCPCheckReq(this.configurations.getIsTcpCheckReq());

            Response responseMahindra = this.cashRepository.sendMahindraRequest(rr);
            return responseMahindra.getTxnstatus().equals(TRANSACTION_OK);

        } catch (ServiceException e) {
            log.error("Error refund cashOut {}", e.getMessage());
        }

        return Boolean.FALSE;

    }

    private void assignCorrelative(String correlative) {
        MDC.putCloseable("correlation-id", correlative);
        MDC.putCloseable("component", "cash-out");
    }

}

