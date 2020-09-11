package co.moviired.microservice.provider.agrario;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.iso.GenericRequest;
import co.moviired.microservice.domain.jpa.entity.Biller;
import co.moviired.microservice.domain.jpa.repository.IBillerRepository;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class QueryParser implements IParser {

    private static final String SEPARATOR = "\\|";
    private static final String DATE_FORMAT = "YYYYMMdd";
    private static final String BARCODE_EXPIRATION_DATE = "96[0-9]+";
    private final BankProperties bankProducts;
    private final StatusCodeConfig statusCodeConfig;
    private final IBillerRepository iBillerRepository;
    private String numberReference;
    private Integer finalValuePosition;
    private Integer initialValuePosition;

    public QueryParser(BankProperties bankProducts, StatusCodeConfig statusCodeConfig, IBillerRepository iBillerRepository) {
        this.bankProducts = bankProducts;
        this.statusCodeConfig = statusCodeConfig;
        this.iBillerRepository = iBillerRepository;
    }

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException {
        String[] imei = params.getImei().split(SEPARATOR);
        String[] shortReference = params.getShortReferenceNumber().split(SEPARATOR);
        String referenceNumber = shortReference[ConstantNumbers.LENGTH_0];
        String internalCode = shortReference[ConstantNumbers.LENGTH_2];
        String opType = shortReference[ConstantNumbers.LENGTH_1];

        try {
            GenericRequest isoRequest = UtilHelper.generateBasicInfo(imei, bankProducts.getDeviceId());
            isoRequest.setProcessingCode(bankProducts.getProcessingCode());
            isoRequest.setAmount(UtilHelper.strPad("0", ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_1));
            isoRequest.setTraceAuditNumber(imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_6));
            isoRequest.setPointServiceEntryMode(bankProducts.getInputMode1());
            isoRequest.setAcquiringCode(bankProducts.getTradeCode());
            isoRequest.setRestrictionCode(bankProducts.getTrxId());
            isoRequest.setCardAcceptorCode(UtilHelper.strPad(imei[ConstantNumbers.LENGTH_11], ConstantNumbers.LENGTH_15, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setCardAcceptorName(((params.getLastName().length() > ConstantNumbers.LENGTH_22) ? params.getLastName().substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_22) : UtilHelper.strPad(params.getLastName(), ConstantNumbers.LENGTH_22, "", ConstantNumbers.LENGTH_1)) + bankProducts.getLocation() + bankProducts.getCountry());
            isoRequest.setCurrencyCode(bankProducts.getCurrencyCode());
            isoRequest.setMessageNumber((internalCode.length() > ConstantNumbers.LENGTH_6) ? internalCode.substring(internalCode.length() - ConstantNumbers.LENGTH_6) : UtilHelper.strPad(internalCode, ConstantNumbers.LENGTH_6, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setReceivingIdentCode(imei[ConstantNumbers.LENGTH_11]);
            isoRequest.setAccountIdentification(UtilHelper.strPad(bankProducts.getCorrespondentAccount(), ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setTransactionDescription(bankProducts.getAccountType() + isoRequest.getAccountIdentification());
            if (opType.equals(OperationType.MANUAL.name())) {
                isoRequest.setReservedIsoUse(UtilHelper.strPad(String.valueOf(referenceNumber.length()), ConstantNumbers.LENGTH_3, "0", ConstantNumbers.LENGTH_0) + referenceNumber + "000");
            } else {
                Biller biller = validateConfigurationBiller(referenceNumber);
                numberReference = referenceNumber.substring(biller.getReferencePosition1(), biller.getReferencePosition1() + biller.getReferenceLength1());
                isoRequest.setReservedIsoUse(UtilHelper.strPad(String.valueOf(numberReference.length()), ConstantNumbers.LENGTH_3, "0", ConstantNumbers.LENGTH_0) + numberReference + "000");
            }

            ISOMsg isoMsg = ISOMsgHelper.of("0200", isoRequest, packager);
            isoMsg.set(ConstantNumbers.POSITION_128, "00000000".getBytes());

            return isoMsg;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataException("300", e);
        }
    }

    @Override
    public Response parseResponseQuery(Input parameters, OperationType opType) {
        Outcome result;
        Response response;
        Data data = new Data();

        String referenceNumber = parameters.getShortReferenceNumber().split(SEPARATOR)[ConstantNumbers.LENGTH_0];
        data.setAuthorizationCode(parameters.getImei().split(SEPARATOR)[ConstantNumbers.LENGTH_3]);
        data.setBankId(bankProducts.getGestorId());
        data.setEchoData("");

        if (opType.equals(OperationType.MANUAL) || initialValuePosition == null || finalValuePosition == null) {
            if (opType.equals(OperationType.MANUAL)) {
                data.setShortReferenceNumber(referenceNumber);
            } else {
                data.setBillReferenceNumber(numberReference);
            }
            data.setDate(UtilHelper.getDateInformation(new Date(), DATE_FORMAT));
            data.setValueToPay("0");

        } else {
            String expirationDate = null;
            if (referenceNumber.length() < finalValuePosition) {
                finalValuePosition = referenceNumber.length();
            }
            String value = referenceNumber.substring(initialValuePosition, finalValuePosition);
            if (referenceNumber.substring(finalValuePosition - ConstantNumbers.LENGTH_1).matches(BARCODE_EXPIRATION_DATE)) {
                expirationDate = referenceNumber.substring(finalValuePosition + ConstantNumbers.LENGTH_1);
            }
            Integer valuePay = Integer.parseInt(value);
            expirationDate = (expirationDate != null) ? expirationDate : UtilHelper.getDateInformation(new Date(), DATE_FORMAT);
            data.setBillReferenceNumber(numberReference);
            data.setDate(expirationDate);
            data.setValueToPay(String.valueOf(valuePay));
        }

        StatusCode statusCode = statusCodeConfig.of("00", "Transaccion Exitosa");
        ErrorDetail e = new ErrorDetail(ConstantNumbers.LENGTH_0, statusCode.getCode(), statusCode.getMessage());
        result = new Outcome(HttpStatus.OK, e);
        response = new Response(result, data);
        return response;
    }

    private Biller validateConfigurationBiller(String referenceNumber) throws DataException {
        String eanCode = referenceNumber.substring(ConstantNumbers.LENGTH_3, ConstantNumbers.LENGTH_16);
        Biller biller = iBillerRepository.getByEanCode(eanCode);
        if (biller != null && biller.getReferencePosition1() != null && biller.getReferenceLength1() != null) {
            if (biller.getValuePosition1() != null && biller.getValueLength1() != null) {
                initialValuePosition = biller.getValuePosition1() - ConstantNumbers.LENGTH_1;
                finalValuePosition = initialValuePosition + biller.getValueLength1();
            }
            biller.setReferencePosition1(biller.getReferencePosition1() - ConstantNumbers.LENGTH_1);
            return biller;
        } else {
            StatusCode statusCode = statusCodeConfig.of("C08");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }
}

