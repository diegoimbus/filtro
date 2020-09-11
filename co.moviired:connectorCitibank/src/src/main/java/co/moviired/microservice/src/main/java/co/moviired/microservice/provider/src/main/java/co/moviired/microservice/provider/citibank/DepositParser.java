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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DepositParser implements IParser {

    private String correlationReference;
    private BankProperties bankProperties;
    private StatusCodeConfig statusCodeConfig;

    private static final String SEPARATOR = "\\|";
    private static final String DATE_FORMAT = "yyMMdd";
    private static final String HOUR_FORMAT = "HHMMss";
    private static final String DATE_PAY_FORMAT = "yyyyMMdd";

    public DepositParser(BankProperties bankProperties, StatusCodeConfig statusCodeConfig){
     this.bankProperties = bankProperties;
     this.statusCodeConfig = statusCodeConfig;
    }

    @Override
    public Debtpayment parseRequest(Input parameters) throws DataException {
        Debtpayment debtpayment = new Debtpayment();
        DebtpaymentT debtpaymentT = new DebtpaymentT();

        Date date = new Date();
        String transactionType;
        String[] imei = parameters.getImei().split(SEPARATOR);
        int lenCorrelation = imei[ConstantNumbers.LENGTH_3].length();
        String[] echoData = parameters.getEchoData().split(SEPARATOR);
        String valueToPay = parameters.getValueToPay().concat(".00");
        String[] shortReference = parameters.getShortReferenceNumber().split(SEPARATOR);
        correlationReference = imei[ConstantNumbers.LENGTH_3].substring(lenCorrelation - ConstantNumbers.LENGTH_15, lenCorrelation);
        if (echoData[ConstantNumbers.LENGTH_1].equals("Y")) {
            transactionType = "A";
        } else if (echoData[ConstantNumbers.LENGTH_3].equals("Y")) {
            transactionType = "P";
        } else {
            StatusCode statusCode = statusCodeConfig.of("C09");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        debtpaymentT.setMessageID(bankProperties.getDebtPayment().concat(getDateInformation(date, DATE_FORMAT)).concat(correlationReference));
        debtpaymentT.setDateTime(getDateInformation(date, HOUR_FORMAT));
        debtpaymentT.setCountryCode(bankProperties.getCountryCode());
        debtpaymentT.setCollectorAccount(shortReference[ConstantNumbers.LENGTH_2]);
        debtpaymentT.setNetworkExtension(parameters.getNetworkExtension());
        debtpaymentT.setTransactionType(transactionType);
        debtpaymentT.setUseAdditionalText(bankProperties.getUseAdditionalText());
        debtpaymentT.setPaymentReference(correlationReference);
        debtpaymentT.setPaymentCurrency(bankProperties.getPaymentCurrency());
        debtpaymentT.setPaymentDate(getDateInformation(date, DATE_PAY_FORMAT));
        debtpaymentT.setTotalPaymentAmount(valueToPay);
        debtpaymentT.setBranch(bankProperties.getBranch());
        debtpaymentT.setCollectionitemsQuantity(bankProperties.getCollectionItemsQuantity());
        debtpaymentT.setPaymentinstrumentsQuantity(bankProperties.getPaymentInstrumentsQuantity());

        CollectionitempaymentT collectionitempaymentT = new CollectionitempaymentT();
        collectionitempaymentT.setItemNumber(shortReference[ConstantNumbers.LENGTH_0]);
        collectionitempaymentT.setAmount(valueToPay);
        collectionitempaymentT.setPayerID(echoData[ConstantNumbers.LENGTH_0]);
        collectionitempaymentT.setDueDate(echoData[ConstantNumbers.LENGTH_2].isBlank() ? null : echoData[ConstantNumbers.LENGTH_2]);

        PaymentinstrumentsdpT paymentinstrumentsdpT = new PaymentinstrumentsdpT();
        paymentinstrumentsdpT.setPaymentInstrumentType(bankProperties.getPaymentInstrumentType());
        paymentinstrumentsdpT.setPaymentInstrumentAmount(valueToPay);

        debtpaymentT.getPaymentInstrument().add(paymentinstrumentsdpT);
        debtpaymentT.getCollectionItem().add(collectionitempaymentT);

        debtpayment.setDebtpayment(debtpaymentT);

        return debtpayment;
    }

    @Override
    public Response parseResponse(Object debtinquireResponse, Input params) throws DataException, ProcessingException {
        ErrorDetail e;
        Response response;
        Data data = new Data();
        try {
            DebtpaymentresponseT responseBank = ((DebtpaymentResponse) debtinquireResponse).getDebtpaymentresponse();
            if ((responseBank.getStatus() == null) || (responseBank.getStatus().isBlank())) {
                StatusCode statusCode = statusCodeConfig.of("C08");
                throw new DataException(statusCode.getCode(), statusCode.getMessage());
            }

            String referenceNumber = params.getShortReferenceNumber().split(SEPARATOR)[ConstantNumbers.LENGTH_0];
            data.setShortReferenceNumber(referenceNumber);
            data.setBankId(bankProperties.getGestorId());

            if (responseBank.getStatus().equals("00")) {
                data.setDate(responseBank.getDateTime());
                data.setAuthorizationCode(correlationReference);
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

    public static String getDateInformation(Date currentDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentDate);
    }

}

