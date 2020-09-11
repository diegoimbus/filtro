package co.moviired.business.provider.bankingswitch.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.bankingswitch.request.QueryBankingRequest;
import co.moviired.business.provider.bankingswitch.response.CommandQueryBankingResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
@AllArgsConstructor
public class QueryBankingParser implements IParser {

    private final BankingProperties bankingProperties;

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking query) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("referenceNumber", query.getReferenceNumber());
        parameters.put("accountType", query.getAccountType());
        parameters.put("typeDocument", query.getTypeDocument());
        parameters.put("accountOrdinal", query.getAccountOrdinal());
        parameters.put("valueToPay", query.getAmount());
        parameters.put("tercId", query.getTercId());
        parameters.put("issuerName", query.getIssuerName());
        parameters.put("issuerId", query.getIssuerId());
        parameters.put("correlationId", query.getCorrelationId());
        parameters.put("issueDate", query.getIssueDate());
        parameters.put("serviceCode", query.getServiceCode());
        parameters.put("imei", UtilHelper.generateImei(query));
        parameters.put("lastName", query.getLastName());
        parameters.put("numberDocument", query.getNumberDocument());
        parameters.put("internalCode", query.getInternalCode());

        QueryBankingRequest request = new QueryBankingRequest();
        request.setData(parameters);

        return request;
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        final SimpleDateFormat fechaIso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Transformar al command específico
        CommandQueryBankingResponse command = (CommandQueryBankingResponse) pcommand;
        // Armar el objeto respuesta
        Response response = new Response();

        String statusCodeQuery = command.getOutcome().getStatusCode().toString();
        String errorCodeQuery = command.getOutcome().getError().getErrorCode();

        if (("200").equals(statusCodeQuery) && ("00").equals(errorCodeQuery)) {

            response.setErrorCode("00");
            response.setErrorType("");
            response.setErrorMessage("OK");

            if (bankingProperties.getGestorIdBBVA().equals(bankingRequest.getGestorId())) {
                response.setAuthorizationCode(command.getData().getAuthorizationCode());
                response.setAmount(Integer.parseInt(command.getData().getValueToPay().replace(".", "")));
                response.setReferenceNumber(command.getData().getShortReferenceNumber());
                response.setBalance(command.getData().getBalance());
                response.setComission(command.getData().getComission());
                response.setUpcId(command.getData().getUpcID());
            } else if (bankingProperties.getGestorIdAgrario().equals(bankingRequest.getGestorId())) {
                response.setObligations(command.getData().getListObligations());
            } else if (bankingProperties.getGestorIdBogota().equals(bankingRequest.getGestorId())) {
                response.setAuthorizationCode(command.getData().getAuthorizationCode());
                String value = command.getData().getValueToPay().replace(".", "");
                response.setAmount(Integer.parseInt(value.substring(0, value.length() - 2)));
                response.setReferenceNumber(command.getData().getShortReferenceNumber());
                response.setGestorId(bankingRequest.getGestorId());
                response.setCorrelationId(bankingRequest.getCorrelationId());
                response.setBillerName(bankingRequest.getBillerName());
                response.setEan13BillerCode(bankingRequest.getEan13BillerCode());
                response.setTypePayBill(bankingRequest.getTypePayBill());
                response.setMaxPaymentValue(String.valueOf(bankingRequest.getMaxValue()));
                response.setMinPaymentValue(String.valueOf(bankingRequest.getMinValue()));
                response.setEchoData(command.getData().getEchoData());
                response.setTransactionDate(fechaIso.format(new Date()));
            }

        } else {

            String errorMessage = command.getOutcome().getError().getErrorMessage();
            String errorType = command.getOutcome().getError().getErrorType().toString();
            response.setErrorCode(errorCodeQuery);
            response.setErrorMessage(errorMessage);
            response.setErrorType(errorType);
            response.setCorrelationId(bankingRequest.getCorrelationId());
        }

        return response;
    }

    public IRequest parseRequestGeneric(RequestFormatBanking requestFormat) {
        QueryBankingRequest request = new QueryBankingRequest();

        HashMap<String, Object> parametersMeta = new HashMap<>();
        String dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
        parametersMeta.put("customerId", "");
        parametersMeta.put("deviceCode", "");
        parametersMeta.put("requestSource", UtilHelper.generateImei(requestFormat));
        parametersMeta.put("requestDate", dateFormat);
        parametersMeta.put("requestReference", dateFormat.replace(".", ""));
        request.setMeta(parametersMeta);

        HashMap<String, Object> parameters = new HashMap<>();
        if (CollectionType.MANUAL.equals(requestFormat.getTypePayBill())) {
            if (UtilHelper.stringNotNullOrNotEmpty(requestFormat.getSpecialFields())) {
                parameters.put("echoData", requestFormat.getSpecialFields());
            }
            parameters.put("billerCode", requestFormat.getServiceCode());
            parameters.put("shortReferenceNumber", requestFormat.getReferenceNumber() + "|" + requestFormat.getTypePayBill() + "|" + requestFormat.getInternalCode() + "|" + requestFormat.getSource());
        } else {
            parameters.put("EAN128FullCode", requestFormat.getReferenceNumber() + "|" + requestFormat.getTypePayBill() + "|" + requestFormat.getInternalCode() + "|" + requestFormat.getSource());
        }
        request.setData(parameters);

        HashMap<String, Object> parametersSignature = new HashMap<>();
        parametersSignature.put("systemSignature", requestFormat.getLastName());
        request.setRequestSignature(parametersSignature);

        return request;
    }

}













