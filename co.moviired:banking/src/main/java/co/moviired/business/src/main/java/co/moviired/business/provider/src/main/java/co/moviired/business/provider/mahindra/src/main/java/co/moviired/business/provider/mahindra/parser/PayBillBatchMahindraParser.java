package co.moviired.business.provider.mahindra.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.mahindra.request.CommandPayBillBatchRequest;
import co.moviired.business.provider.mahindra.response.CommandPayBillBatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class PayBillBatchMahindraParser implements IParser {

    private final MahindraProperties mahindraProperties;
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PayBillBatchMahindraParser(@NotNull MahindraProperties pmahindraProperties) {
        this.mahindraProperties = pmahindraProperties;
    }

    @Override
    public final IRequest parseRequest(RequestFormatBanking requestFormatBanking) {
        CommandPayBillBatchRequest commandBatchPay = new CommandPayBillBatchRequest();
        commandBatchPay.setType(mahindraProperties.getPbbType());
        commandBatchPay.setMsisdn(requestFormatBanking.getMsisdn1());
        commandBatchPay.setMpin(requestFormatBanking.getMpin());
        commandBatchPay.setPin(requestFormatBanking.getMpin());
        commandBatchPay.setProvider(mahindraProperties.getPbbProvider());
        commandBatchPay.setBprovider(mahindraProperties.getPbbProvider());
        commandBatchPay.setPaymentInstrument(mahindraProperties.getPbbPaymentInstrument());
        commandBatchPay.setPayId(mahindraProperties.getPbbPayId());
        commandBatchPay.setBillccode(requestFormatBanking.getBillerName());
        commandBatchPay.setBillano(requestFormatBanking.getReferenceNumber());
        commandBatchPay.setBillno(requestFormatBanking.getReferenceNumber());
        commandBatchPay.setAmount(requestFormatBanking.getAmount());
        commandBatchPay.setLanguage1(mahindraProperties.getPbbLanguage1());
        commandBatchPay.setRefNo(mahindraProperties.getPbbRefno());
        commandBatchPay.setBname(requestFormatBanking.getLastName());
        commandBatchPay.setBillerCode(requestFormatBanking.getBillerName());
        commandBatchPay.setShortReferenceNumber(requestFormatBanking.getReferenceNumber());
        commandBatchPay.setRemarks(requestFormatBanking.getBillerName());

        return commandBatchPay;
    }

    @Override
    public final Response parseResponse(RequestFormatBanking requestFormatBanking, IResponse command) {
        Response response = new Response();
        CommandPayBillBatchResponse commandResponse = (CommandPayBillBatchResponse) command;
        if (commandResponse.getTxnStatus().equals("200")) {
            response.setErrorCode("00");
            response.setErrorType("");
            response.setErrorMessage("OK");
            String value = commandResponse.getAmount().replace(".", "");
            response.setAuthorizationCode(commandResponse.getTrid());
            response.setReferenceNumber(requestFormatBanking.getReferenceNumber());
            response.setAmount(Integer.parseInt(value.substring(0, value.length() - 2)));
            response.setCorrelationId(requestFormatBanking.getCorrelationId());
            response.setTransferId(commandResponse.getTxnid());
            response.setBillerName(requestFormatBanking.getBillerName());
            response.setTransactionDate(fechaISO.format(new Date()));
            response.setCorrelationId("");
        } else {
            String errorMessage = commandResponse.getMessage();
            response.setErrorCode(commandResponse.getTxnStatus());
            response.setErrorMessage(errorMessage);
        }
        return response;
    }

}

