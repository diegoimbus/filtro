package co.moviired.digitalcontent.incomm.model.request;

import co.moviired.base.domain.exception.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-22
 * @since 1.0
 */

@Slf4j
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class Input implements Serializable {

    private static final int IMEI_PARTS = 10;
    private static final int PHONE_TERMINAL = 6;
    private static final long serialVersionUID = -5749485538108617362L;

    private String procesingCode;
    private String shortReferenceNumber;
    private String idTxn;
    private String eanCode;
    private String controlNumber;
    private String customerId;
    private String userName;
    private Integer amount;
    private String operation;
    private String correlationId;
    private String incommCode;

    private Input() {
        super();
    }

    public static Input parseInput(Map<String, Object> parameters, Map<String, Object> meta) throws DataException {
        Input request = new Input();

        try {
            // Transformar los parámetros al objeto entrada
            Object p = parameters.get("idTxn");
            if (p != null) {
                request.setIdTxn((String) p);
            }

            p = parameters.get("eanCode");
            if (p != null) {
                request.setEanCode((String) p);
            }

            p = parameters.get("shortReferenceNumber");
            if (p != null) {
                request.setShortReferenceNumber(((String) p).split("#")[0]);
                if (((String) p).split("#").length > 1)
                    request.setOperation(((String) p).split("#")[1]);
            }

            p = parameters.get("controlNumber");
            if (p != null) {
                request.setControlNumber((String) p);
            }

            p = parameters.get("amount");
            if (p != null) {
                request.setAmount(Integer.parseInt((String) p));
            }

            p = meta.get("imei");
            if (p == null) {
                throw new DataException("-2", "El campo IMEI es obligatorio y debe contener " + IMEI_PARTS + " posiciones o mas");
            }

            String[] param = StringUtils.splitPreserveAllTokens((String) p, '|');
            if (param.length < IMEI_PARTS) {
                throw new DataException("-2", "El campo IMEI debe contener " + IMEI_PARTS + " posiciones o mas");
            }

            String correlationId = param[3];
            String usuarioGetrax = param[5];
            String deviceId = param[6];
            String agentCode = param[7];
            String phoneNumber = param[8];
            String incommCode = param[9];

            usuarioGetrax = usuarioGetrax.length() > 4 ? usuarioGetrax.substring((usuarioGetrax.length() - 4)) : usuarioGetrax;
            deviceId = deviceId.length() > 4 ? deviceId.substring((deviceId.length() - 4)) : deviceId;

            request.setCustomerId(usuarioGetrax + deviceId);

            if (agentCode == null || agentCode.trim().isEmpty() || "NULL".equalsIgnoreCase(agentCode)) {
                agentCode = ((phoneNumber != null) && (phoneNumber.length() > PHONE_TERMINAL)) ? phoneNumber.substring(phoneNumber.length() - PHONE_TERMINAL) : phoneNumber;
            }
            request.setUserName("MV" + agentCode);
            if (request.getUserName().startsWith("MV000000")) {
                request.setUserName(request.getUserName().replace("MV000000", "MV00"));
            }

            request.setIncommCode(incommCode);
            request.setCorrelationId(correlationId);

        } catch (DataException e) {
            throw new DataException("-2", e.getMessage(), e);
        }

        return request;
    }

}

