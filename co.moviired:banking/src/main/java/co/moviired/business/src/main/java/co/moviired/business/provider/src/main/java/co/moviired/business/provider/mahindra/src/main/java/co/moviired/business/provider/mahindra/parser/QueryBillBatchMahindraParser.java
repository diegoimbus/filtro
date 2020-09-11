package co.moviired.business.provider.mahindra.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.mahindra.request.CommandQueryBillBatchRequest;
import co.moviired.business.provider.mahindra.response.CommandQueryBillBatchResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class QueryBillBatchMahindraParser implements IParser {

    private final MahindraProperties mahindraProperties;
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public final IRequest parseRequest(RequestFormatBanking requestFormatBanking) {
        CommandQueryBillBatchRequest commandQuery = new CommandQueryBillBatchRequest();
        commandQuery.setType(mahindraProperties.getQbbType());
        commandQuery.setMsisdn(requestFormatBanking.getMsisdn1());
        commandQuery.setProvider(mahindraProperties.getQbbProvider());
        commandQuery.setMpin(requestFormatBanking.getMpin());
        commandQuery.setPin(requestFormatBanking.getMpin());
        commandQuery.setBillccode(requestFormatBanking.getBillerName().replace("-", " "));
        commandQuery.setBillano(requestFormatBanking.getReferenceNumber());
        commandQuery.setBillno(requestFormatBanking.getReferenceNumber());
        commandQuery.setBlocksms(mahindraProperties.getQbbBlockSms());
        commandQuery.setLanguage1(mahindraProperties.getQbbLanguage1());
        commandQuery.setCellid(mahindraProperties.getQbbCellId());
        commandQuery.setFtxnid(requestFormatBanking.getComponentDate());
        commandQuery.setPaymentInstrument(mahindraProperties.getQbbPaymentInstrument());
        commandQuery.setEchodata((requestFormatBanking.getEchoData() == null) ? "" : requestFormatBanking.getEchoData());
        commandQuery.setImei(UtilHelper.generateImei(requestFormatBanking));
        commandQuery.setSource(mahindraProperties.getQbbSource());
        return commandQuery;
    }

    @Override
    public final Response parseResponse(RequestFormatBanking requestFormatBanking, IResponse command) {
        Response response = new Response();
        CommandQueryBillBatchResponse commandResponse = (CommandQueryBillBatchResponse) command;
        if (commandResponse.getTxnStatus().equals("200")) {
            response.setErrorType("");
            response.setErrorCode("00");
            response.setErrorMessage("OK");
            String value = commandResponse.getAmount().replace(".", "");
            response.setAuthorizationCode(commandResponse.getTrid());
            response.setReferenceNumber(commandResponse.getBillno());
            response.setAmount(Integer.parseInt(value.substring(0, value.length() - 2)));
            response.setCorrelationId(requestFormatBanking.getCorrelationId());
            response.setTypePayBill(requestFormatBanking.getTypePayBill());
            response.setServiceCode(requestFormatBanking.getServiceCode());
            response.setEchoData((requestFormatBanking.getEchoData() == null) ? "" : requestFormatBanking.getEchoData());
            response.setTransactionDate(fechaISO.format(new Date()));
            response.setBillerName(commandResponse.getBillccode());
            response.setPartialPayment(requestFormatBanking.getPartialPayment() != null && requestFormatBanking.getPartialPayment());
            response.setGestorId("");
        } else {
            String errorMessage = commandResponse.getMessage();
            response.setErrorCode(commandResponse.getTxnStatus());
            response.setErrorMessage(errorMessage);
        }
        return response;
    }

}

