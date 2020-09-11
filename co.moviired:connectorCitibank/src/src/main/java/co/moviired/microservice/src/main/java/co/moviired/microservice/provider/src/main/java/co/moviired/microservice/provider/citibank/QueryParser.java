package co.moviired.microservice.provider.citibank;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.constants.UtilHelper;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.provider.IParser;
import co.moviired.microservice.soap.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class QueryParser implements IParser {

    private final BankProperties bankProperties;
    private final StatusCodeConfig statusCodeConfig;

    private static final String SEPARATOR = "\\|";
    private static final String DATE_FORMAT = "yyMMdd";
    private static final String HOUR_FORMAT = "HHMMss";

    @Override
    public Debtinquire parseRequest(Input parameters) {
        Debtinquire debtinquire = new Debtinquire();
        DebtinquireT debtinquiret = new DebtinquireT();

        Date date = new Date();
        String[] imei = parameters.getImei().split(SEPARATOR);
        int lenCorrelation = imei[ConstantNumbers.LENGTH_3].length();
        String[] shortReference = parameters.getShortReferenceNumber().split(SEPARATOR);
        String correlationReference = imei[ConstantNumbers.LENGTH_3].substring(lenCorrelation - ConstantNumbers.LENGTH_15, lenCorrelation);

        debtinquiret.setMessageID(bankProperties.getDebtRequest().concat(getDateInformation(date, DATE_FORMAT)).concat(correlationReference));
        debtinquiret.setDateTime(getDateInformation(date, HOUR_FORMAT));
        debtinquiret.setNetworkExtension(parameters.getNetworkExtension());
        debtinquiret.setCountryCode(bankProperties.getCountryCode());
        debtinquiret.setPaymentCurrency(bankProperties.getPaymentCurrency());
        debtinquiret.setCollectorAccount(shortReference[ConstantNumbers.LENGTH_2]);
        debtinquiret.setPayerID(parameters.getEchoData());
        debtinquiret.setServiceID(shortReference[ConstantNumbers.LENGTH_0]);
        debtinquire.setDebtinquire(debtinquiret);

        return debtinquire;
    }

    @Override
    public Response parseResponse(Object debtinquireResponse, Input params) throws DataException, ProcessingException {
        ErrorDetail e;
        Response response;
        Data data = new Data();
        try {
            DebtinquireresponseT responseBank = ((DebtinquireResponse) debtinquireResponse).getDebtinquireresponse();
            if ((responseBank.getStatus() == null) || (responseBank.getStatus().isBlank())) {
                StatusCode statusCode = statusCodeConfig.of("C08");
                throw new DataException(statusCode.getCode(), statusCode.getMessage());
            }

            String referenceNumber = params.getShortReferenceNumber().split(SEPARATOR)[ConstantNumbers.LENGTH_0];
            data.setShortReferenceNumber(referenceNumber);
            data.setBankId(bankProperties.getGestorId());

            if (responseBank.getStatus().equals("00") || responseBank.getStatus().equals("01")) {
                CollectioniteminquireT collectionItem = null;
                double valueToPay = ConstantNumbers.LENGTH_0;
                String date = getDateInformation(new Date(), DATE_FORMAT);
                if (responseBank.getCollectionitemsQuantity() != null && !responseBank.getCollectionitemsQuantity().equals("0")) {
                    collectionItem = responseBank.getCollectionItem().get(ConstantNumbers.LENGTH_0);
                    valueToPay = Double.parseDouble(collectionItem.getAmount());
                    date = collectionItem.getDueDate();
                }
                data.setDate(date);
                data.setValueToPay(String.valueOf((int) valueToPay));
                data.setAuthorizationCode(responseBank.getMessageID());
                data.setEchoData(generateEchoData(params, responseBank, collectionItem));
                data.setPartialPayment(transformPartialPayment(responseBank.getPartialpaymentAllowed()));
                StatusCode statusCode = statusCodeConfig.of(responseBank.getStatus(), "Transacción Exitosa");
                e = new ErrorDetail(ConstantNumbers.LENGTH_0, statusCode.getCode(), statusCode.getMessage());

            } else {
                e = UtilHelper.parseError(responseBank.getErrorCode(), statusCodeConfig);
            }

            Outcome result = new Outcome(HttpStatus.OK, e);
            response = new Response(result, data);
            return response;

        } catch (Exception ex) {
            throw new ProcessingException("-1", ex.getMessage());
        }
    }

    private String generateEchoData(Input params, DebtinquireresponseT responseBank, CollectioniteminquireT collectionItem) {
        return ((collectionItem == null) ? params.getEchoData() : collectionItem.getPayerID())
                .concat("|")
                .concat(collectionItem == null ? "N" : responseBank.getActivepaymentAllowed())
                .concat("|")
                .concat(collectionItem == null ? "" : collectionItem.getDueDate())
                .concat("|")
                .concat(responseBank.getPassivepaymentAllowed())
                .concat("|")
                .concat(params.getNetworkExtension());
    }

    private String transformPartialPayment(String partialPayment) {
        if (partialPayment != null && !partialPayment.isBlank()) {
            boolean value = partialPayment.equals("Y");
            return Boolean.toString(value);
        } else {
            return null;
        }
    }

    private String getDateInformation(Date currentDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentDate);
    }

}

