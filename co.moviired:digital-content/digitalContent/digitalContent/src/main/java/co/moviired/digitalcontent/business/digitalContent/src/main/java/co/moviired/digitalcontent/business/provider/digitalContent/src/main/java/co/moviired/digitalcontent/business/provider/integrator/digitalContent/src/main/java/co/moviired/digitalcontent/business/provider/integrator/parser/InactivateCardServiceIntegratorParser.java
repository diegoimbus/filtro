package co.moviired.digitalcontent.business.provider.integrator.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.digitalcontent.business.domain.StatusCode;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.provider.IParser;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.IResponse;
import co.moviired.digitalcontent.business.provider.integrator.request.Request;
import co.moviired.digitalcontent.business.provider.integrator.response.ResponseIntegrator;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandLoginServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class InactivateCardServiceIntegratorParser implements IParser {

    private static final long serialVersionUID = 8488946390611766154L;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String LBL_DIGITAL_CONTENT = "DIGITAL-CONTENT|";

    private final GlobalProperties config;

    public InactivateCardServiceIntegratorParser(@NotNull GlobalProperties config) {
        super();
        this.config = config;
    }

    @Override
    public final IRequest parseRequest(
            @NotNull DigitalContentRequest data,
            @NotNull CommandLoginServiceResponse mhResponseAutenticacion) {

        final SimpleDateFormat formatDateOrigen = new SimpleDateFormat("yyyyMMddHHmmss");
        final SimpleDateFormat formatDateDestino = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        final SimpleDateFormat formatDateDestino2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String ip = (data.getIp() == null) ? LOCAL_ADDRESS : data.getIp();

        Request request = new Request();
        HashMap<String, Object> parametersData = new HashMap<>();
        HashMap<String, Object> parametersRequestSignature = new HashMap<>();
        HashMap<String, Object> parametersMeta = new HashMap<>();

        // ID de reversión
        parametersData.put("customerTxReference", data.getCorrelationIdR());

        // DATA
        parametersData.put("EANCode", data.getEanCode() + "|" + data.getCardSerialNumber() + "#" + config.getProcessInactivate());
        parametersData.put("amount", data.getAmount());
        parametersData.put("phoneNumber", data.getPhoneNumber());
        parametersData.put("productId", data.getProductId());
        parametersData.put("customerDate", data.getIssueDate());
        if (data.getEmail() != null) {
            parametersData.put("email", data.getEmail());
        }
        parametersMeta.put("originAddress", ip);

        // META
        try {
            parametersMeta.put("requestDate", formatDateDestino.format(formatDateOrigen.parse(data.getIssueDate())));
        } catch (ParseException e) {
            parametersMeta.put("requestDate", formatDateDestino.format(new Date()));
        }

        try {
            parametersMeta.put("requestReference", formatDateDestino2.format(formatDateOrigen.parse(data.getIssueDate())));
        } catch (ParseException e) {
            parametersMeta.put("requestReference", formatDateDestino2.format(new Date()));
        }

        parametersMeta.put("systemId", "9006");
        parametersMeta.put("customerId", "174484");
        parametersMeta.put("channel", "1");

        // Se agrega en el IMEI el número de transacción del cliente
        parametersMeta.put("requestSource",
                "0|" + ip + "|" +
                        LBL_DIGITAL_CONTENT +
                        data.getCorrelationIdR() + "|" +
                        data.getIssueDate() + "|" +
                        data.getIssuerLogin() + "|" +
                        data.getPosId() + "|" +
                        mhResponseAutenticacion.getAgentcode() + "|" +
                        data.getMsisdn1() + "|" +
                        data.getIncommCode());

        parametersMeta.put("deviceCode", data.getSource());
        parametersMeta.put("userName", data.getIssuerLogin());
        parametersMeta.put("passwordHash", "6ce73de59cac75e95d67b79555b2b6f6");

        // REQUESTSIGNATURE
        parametersRequestSignature.put("systemSignature", "db8a0abc9f4c9d79225c373dae700b6d");

        request.setData(parametersData);
        request.setMeta(parametersMeta);
        request.setRequestSignature(parametersRequestSignature);

        return request;
    }

    @Override
    public final DigitalContentResponse parseResponse(@NotNull DigitalContentRequest request, IResponse pcommandResponse) {
        SimpleDateFormat simpleDateFormatOrigen = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        ResponseIntegrator commandResponse = (ResponseIntegrator) pcommandResponse;
        // Código y mensaje de respuesta
        DigitalContentResponse response = new DigitalContentResponse();

        // Armar el objeto respuesta

        if ("200".equalsIgnoreCase(commandResponse.getOutcome().getStatusCode().toString())) {
            response.setErrorCode("00");
            try {
                response.setTransactionDate(simpleDateFormatOrigen.parse(commandResponse.getData().getTransactionDate()));
            } catch (ParseException e) {
                response.setTransactionDate(new Date());
            }
            response.setAmount(request.getAmount());
            response.setAuthorizationCode(commandResponse.getData().getAuthorizationCode());
            response.setErrorMessage("éxito");
            response.setCorrelationId(request.getCorrelationId());
            response.setErrorCode(StatusCode.Level.SUCCESS.value());

        } else {
            response.setErrorCode(commandResponse.getOutcome().getStatusCode().toString());
            try {
                response.setTransactionDate(simpleDateFormatOrigen.parse(commandResponse.getData().getTransactionDate()));
            } catch (Exception e) {
                response.setTransactionDate(new Date());
            }
            response.setAmount(request.getAmount());
            response.setErrorMessage((commandResponse.getOutcome().getError().getErrorMessage().toUpperCase()));
            response.setErrorCode(commandResponse.getOutcome().getStatusCode().toString());
            response.setErrorType(ErrorType.PROCESSING.name());
        }

        return response;
    }


}

