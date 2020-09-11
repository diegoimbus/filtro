package co.moviired.business.provider.integrator.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.enums.WeftType;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.bankingswitch.parser.QueryBankingParser;
import co.moviired.business.provider.integrator.request.Data;
import co.moviired.business.provider.integrator.response.ResponseIntegrator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class ValidatePayBillByEANCodeParser implements IParser {

    private final QueryBankingParser queryBankingParser;
    private final GenericValidatePayBill genericValidatePayBill;

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking bankingRequest) throws JsonProcessingException {
        if (bankingRequest.getWeftType().equals(WeftType.GENERIC)) { //CONSULTA GENERICA

            Data dataIntegrator = new Data();
            dataIntegrator.setEan128FullCode(bankingRequest.getReferenceNumber());
            dataIntegrator.setValueToPay("0");

            return genericValidatePayBill.generateRequest(bankingRequest, dataIntegrator);

        } else { //CONSULTA A CONECTOR
            return queryBankingParser.parseRequestGeneric(bankingRequest);
        }
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        final SimpleDateFormat fechaISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResponseIntegrator integratorByEan = (ResponseIntegrator) pcommand;
        Response responseByEan = new Response();

        String statusCodeQuery = integratorByEan.getOutcome().getStatusCode().toString();
        String errorCodeQuery = integratorByEan.getOutcome().getError().getErrorCode();

        if (("200").equals(statusCodeQuery) && ("00").equals(errorCodeQuery)) {
            responseByEan.setErrorType("");
            responseByEan.setErrorCode("00");
            responseByEan.setErrorMessage("OK");
            String value = integratorByEan.getData().getValueToPay().replace(".", "");
            responseByEan.setAmount(Integer.parseInt(value.substring(0, value.length() - 2)));
            responseByEan.setReferenceNumber(integratorByEan.getData().getBillReferenceNumber());
            responseByEan.setMinPartialPayment(integratorByEan.getData().getMinPartialPayment());
            responseByEan.setGestorId(integratorByEan.getData().getBankId());
            responseByEan.setTypePayBill(bankingRequest.getTypePayBill());
            responseByEan.setCorrelationId(bankingRequest.getCorrelationId());
            responseByEan.setTransactionDate(fechaISO.format(new Date()));
            responseByEan.setEchoData((integratorByEan.getData().getEchoData() == null) ? "" : integratorByEan.getData().getEchoData());
            if (bankingRequest.getWeftType().equals(WeftType.CONNECTOR)) {
                responseByEan.setServiceCode(null);
                responseByEan.setBillerName(bankingRequest.getBillerName());
                responseByEan.setEan13BillerCode(integratorByEan.getData().getEanCode());
                responseByEan.setPartialPayment(bankingRequest.getPartialPayment());
                responseByEan.setAuthorizationCode(integratorByEan.getData().getBankTransactionId());
            } else {
                Biller biller = genericValidatePayBill.validationAndFindBiller(integratorByEan.getData(), null);
                responseByEan.setBillerName(biller.getName());
                responseByEan.setPartialPayment(biller.getPartialPayment());
                responseByEan.setServiceCode(integratorByEan.getData().getBillerCode());
                responseByEan.setEan13BillerCode(integratorByEan.getData().getEan13Billercode());
            }
        } else {
            String errorMessage = integratorByEan.getOutcome().getError().getErrorMessage();
            String errorType = integratorByEan.getOutcome().getError().getErrorType().toString();
            responseByEan.setErrorCode(errorCodeQuery);
            responseByEan.setErrorMessage(errorMessage);
            responseByEan.setErrorType(errorType);
        }
        return responseByEan;
    }

}

