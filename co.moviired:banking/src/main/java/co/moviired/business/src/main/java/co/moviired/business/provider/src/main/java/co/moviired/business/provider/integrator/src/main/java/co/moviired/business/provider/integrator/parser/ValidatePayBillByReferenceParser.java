package co.moviired.business.provider.integrator.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.enums.WeftType;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.BankingProperties;
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
public class ValidatePayBillByReferenceParser implements IParser {

    private final BankingProperties bankingProperties;
    private final QueryBankingParser queryBankingParser;
    private final GenericValidatePayBill genericValidatePayBill;

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking bankingRequest) throws JsonProcessingException {
        if (bankingRequest.getWeftType().equals(WeftType.GENERIC)) { //CONSULTA GENERICA

            Data dataIntegrator = new Data();
            if (bankingRequest.getServiceCode().equals(bankingProperties.getPaynetBillerCode())) {
                dataIntegrator.setValueToPay("");
                bankingRequest.setSpecialFields(null);
            }
            dataIntegrator.setBillerCode(bankingRequest.getServiceCode());
            dataIntegrator.setShortReferenceNumber(bankingRequest.getReferenceNumber());
            if (UtilHelper.stringNotNullOrNotEmpty(bankingRequest.getSpecialFields())) {
                dataIntegrator.setEchoData(bankingRequest.getSpecialFields());
            }

            return genericValidatePayBill.generateRequest(bankingRequest, dataIntegrator);

        } else { //CONSULTA A CONECTOR
            return queryBankingParser.parseRequestGeneric(bankingRequest);
        }
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        final SimpleDateFormat fechaISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResponseIntegrator integratorByReference = (ResponseIntegrator) pcommand;
        Response responseByReference = new Response();

        String statusCodeQuery = integratorByReference.getOutcome().getStatusCode().toString();
        String errorCodeQuery = integratorByReference.getOutcome().getError().getErrorCode();

        if (("200").equals(statusCodeQuery) && ("00").equals(errorCodeQuery)) {
            responseByReference.setErrorType("");
            responseByReference.setErrorCode("00");
            responseByReference.setErrorMessage("OK");
            String value = integratorByReference.getData().getValueToPay().replace(".", "");
            responseByReference.setAmount(Integer.parseInt(value.substring(0, value.length() - 2)));
            responseByReference.setServiceCode(integratorByReference.getData().getBillerCode());
            responseByReference.setGestorId(integratorByReference.getData().getBankId());
            responseByReference.setTypePayBill(bankingRequest.getTypePayBill());
            responseByReference.setCorrelationId(bankingRequest.getCorrelationId());
            responseByReference.setTransactionDate(fechaISO.format(new Date()));
            responseByReference.setEan13BillerCode(integratorByReference.getData().getEanCode());
            responseByReference.setEchoData((integratorByReference.getData().getEchoData() == null) ? "" : integratorByReference.getData().getEchoData());

            Biller biller = genericValidatePayBill.validationAndFindBiller(integratorByReference.getData(), bankingRequest);
            responseByReference.setBillerName(biller.getName());
            responseByReference.setPartialPayment(biller.getPartialPayment());
            if (bankingRequest.getWeftType().equals(WeftType.CONNECTOR)) {
                responseByReference.setAuthorizationCode(integratorByReference.getData().getBankTransactionId());
                responseByReference.setReferenceNumber(integratorByReference.getData().getShortReferenceNumber().split("\\|")[0]);
            } else {
                responseByReference.setAuthorizationCode(integratorByReference.getData().getAuthorizationCode());
                responseByReference.setReferenceNumber(integratorByReference.getData().getShortReferenceNumber());
            }

        } else {
            String errorMessage = integratorByReference.getOutcome().getError().getErrorMessage();
            String errorType = integratorByReference.getOutcome().getError().getErrorType().toString();
            responseByReference.setErrorCode(errorCodeQuery);
            responseByReference.setErrorMessage(errorMessage);
            responseByReference.setErrorType(errorType);
        }
        return responseByReference;
    }

}
