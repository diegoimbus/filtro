package co.moviired.microservice.provider.agrario;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.iso.DepositResponse;
import co.moviired.microservice.domain.iso.GenericRequest;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@AllArgsConstructor
public class DepositParser implements IParser {

    private static final String SEPARATOR = "\\|";
    private final StatusCodeConfig statusCodeConfig;
    private final BankProperties bankProducts;

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException {
        String[] imei = params.getImei().split(SEPARATOR);
        String[] shortReference = params.getShortReferenceNumber().split(SEPARATOR);
        String referenceNumber = shortReference[ConstantNumbers.LENGTH_0];
        String internalCode = shortReference[ConstantNumbers.LENGTH_2];

        try {
            GenericRequest isoRequest = UtilHelper.generateBasicInfo(imei, bankProducts.getDeviceId());
            isoRequest.setProcessingCode(bankProducts.getProcessingCode());
            isoRequest.setAmount(UtilHelper.strPad(params.getValueToPay() + "00", ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setTraceAuditNumber(imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_6));
            isoRequest.setPointServiceEntryMode(bankProducts.getInputMode1());
            isoRequest.setAcquiringCode(bankProducts.getTradeCode());
            isoRequest.setRestrictionCode(bankProducts.getTrxId());
            isoRequest.setCardAcceptorCode(UtilHelper.strPad(imei[ConstantNumbers.LENGTH_11], ConstantNumbers.LENGTH_15, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setCardAcceptorName(((params.getLastName().length() > ConstantNumbers.LENGTH_22) ? params.getLastName().substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_22) : UtilHelper.strPad(params.getLastName(), ConstantNumbers.LENGTH_22, "", ConstantNumbers.LENGTH_1)) + bankProducts.getLocation() + bankProducts.getCountry());
            isoRequest.setCurrencyCode(bankProducts.getCurrencyCode());
            isoRequest.setMessageNumber(UtilHelper.strPad(internalCode, ConstantNumbers.LENGTH_6, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setReceivingIdentCode(imei[ConstantNumbers.LENGTH_11]);
            isoRequest.setAccountIdentification(UtilHelper.strPad(bankProducts.getCorrespondentAccount(), ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setTransactionDescription(bankProducts.getAccountType() + isoRequest.getAccountIdentification());
            isoRequest.setReservedIsoUse(UtilHelper.strPad(String.valueOf(referenceNumber.length()), ConstantNumbers.LENGTH_3, "0", ConstantNumbers.LENGTH_0) + referenceNumber + "000");
            ISOMsg isoMsg = ISOMsgHelper.of("0200", isoRequest, packager);
            isoMsg.set(ConstantNumbers.POSITION_128, "00000000".getBytes());

            return isoMsg;
        } catch (ISOException | IllegalAccessException e) {
            log.error(e.getMessage());
            throw new DataException("300", e);
        }
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input params) throws DataException {
        // Transformar la respuesta
        ErrorDetail e;
        Response response;
        StatusCode statusCode;
        Data data = new Data();
        try {
            DepositResponse depositResponse = ISOMsgHelper.resolve(respOper, DepositResponse.class);
            String respCode = depositResponse.getResponseCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                statusCode = statusCodeConfig.of("C09");
                throw new DataException(statusCode.getCode(), statusCode.getMessage());
            }

            String[] refNumber = params.getShortReferenceNumber().split(SEPARATOR);
            String referenceNumber = refNumber[ConstantNumbers.LENGTH_0];

            data.setShortReferenceNumber(referenceNumber);
            data.setBankId(bankProducts.getGestorId());
            data.setTransactionId(referenceNumber);

            // Respuesta existosa
            if ("00".equals(respCode)) {
                String cost = depositResponse.getAmount().substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_10);
                Integer valor = Integer.parseInt(cost);
                data.setAuthorizationCode(depositResponse.getAuthorizationResponse());
                data.setValueToPay(String.valueOf(valor));
                statusCode = statusCodeConfig.of(respCode, "Transacción Exitosa");
                e = new ErrorDetail(ConstantNumbers.LENGTH_0, statusCode.getCode(), statusCode.getMessage());

            } else {
                statusCode = statusCodeConfig.of(respCode, "Transacción Rechazada");
                e = new ErrorDetail(ErrorType.PROCESSING.ordinal(), statusCode.getCode(), statusCode.getMessage());
            }
            Outcome result = new Outcome(HttpStatus.OK, e);
            response = new Response(result, data);
            return response;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new DataException("-2", ex.getMessage());
        }
    }

}

