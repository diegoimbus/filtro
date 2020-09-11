package co.moviired.digitalcontent.business.provider.integrator.request;

import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-01-28
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Request implements IRequest {

    private static final long serialVersionUID = 233299637669231777L;
    private transient Map<String, Object> meta;
    private transient Map<String, Object> data;
    private transient Map<String, Object> requestSignature;

    public final Request parseRequestCardInactivate(DigitalContentRequest pdata,
                                                    CommandResponse mhResponseAutenticacion
    ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat formatDateDestino = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        SimpleDateFormat formatDateDestino2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        Request request = new Request();


        HashMap<String, Object> parametersData = new HashMap<>();
        HashMap<String, Object> parametersRequestSignature = new HashMap<>();
        HashMap<String, Object> parametersMeta = new HashMap<>();

        // DATA
        parametersData.put("amount", pdata.getAmount());
        parametersData.put("customerTxReference", pdata.getCorrelationId());
        parametersData.put("phoneNumber", pdata.getPhoneNumber());
        parametersData.put("productId", pdata.getProductId());
        parametersData.put("EANCode", pdata.getEanCode() + "|" + pdata.getCardSerialNumber() + "#" + pdata.getOperation());
        parametersData.put("customerDate", pdata.getIssueDate());
        if (pdata.getEmail() != null) {
            parametersData.put("email", pdata.getEmail());
        }


        // META
        String ip = (pdata.getIp() == null) ? "127.0.0.1" : pdata.getIp();

        parametersMeta.put("systemId", "9006");
        parametersMeta.put("originAddress", ip);

        try {
            parametersMeta.put("requestDate", formatDateDestino.format(dateFormat.parse(pdata.getIssueDate())));
        } catch (ParseException e) {
            parametersMeta.put("requestDate", formatDateDestino.format(new Date()));
        }
        parametersMeta.put("customerId", "174484");
        try {
            parametersMeta.put("requestReference", formatDateDestino2.format(dateFormat.parse(pdata.getIssueDate())));
        } catch (ParseException e) {
            parametersMeta.put("requestReference", formatDateDestino2.format(new Date()));
        }
        parametersMeta.put("channel", "1");
        parametersMeta.put("deviceCode", "9003926116");
        parametersMeta.put("requestSource", "0|" +
                ip + "|" +
                "DIGITAL-CONTENT|" +
                pdata.getCorrelationId() + "|" +
                pdata.getIssueDate() + "|" +
                pdata.getIssuerLogin() + "|" +
                pdata.getPosId() + "|" +
                mhResponseAutenticacion.getAgentcode() + "|" +
                pdata.getMsisdn1() + "|" +
                pdata.getIncommCode());
        parametersMeta.put("userName", "9003926116");
        parametersMeta.put("passwordHash", "6ce73de59cac75e95d67b79555b2b6f6");

        // REQUESTSIGNATURE
        parametersRequestSignature.put("systemSignature", "db8a0abc9f4c9d79225c373dae700b6d");

        request.setData(parametersData);
        request.setMeta(parametersMeta);
        request.setRequestSignature(parametersRequestSignature);

        return request;
    }
}

