package co.moviired.digitalcontent.incomm.model.request;

import co.moviired.base.domain.exception.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
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
    private static final int PHONE_TERMINAL = 8;
    private static final long serialVersionUID = -5749485538108617362L;

    private static final int IMEI_CORRELATION_ID = 3;
    private static final int IMEI_AGENT_CODE = 7;
    private static final int IMEI_PHONE = 8;
    private static final int IMEI_INCOMM_CODE = 9;

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
                if (((String) p).split("#").length > 1) {
                    request.setOperation(((String) p).split("#")[1]);
                }
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

            processImei(request, (String) p);

        } catch (DataException e) {
            throw new DataException("-2", e.getMessage(), e);
        }

        return request;
    }


    private static void processImei(@NotNull Input request, @NotNull String imei) throws DataException {
        String[] param = StringUtils.splitPreserveAllTokens(imei, '|');
        if (param.length < IMEI_PARTS) {
            throw new DataException("-2", "El campo IMEI debe contener " + IMEI_PARTS + " posiciones o mas");
        }

        String correlationId = param[IMEI_CORRELATION_ID];
        String agentCode = param[IMEI_AGENT_CODE];
        String phoneNumber = param[IMEI_PHONE];
        String incommCode = param[IMEI_INCOMM_CODE];

        request.setIncommCode(incommCode);
        request.setCorrelationId(correlationId);

        // Field 41: PHONE_NUMBER(8)
        phoneNumber = ((phoneNumber != null) && (phoneNumber.length() > PHONE_TERMINAL)) ? phoneNumber.substring(phoneNumber.length() - PHONE_TERMINAL) : phoneNumber;
        request.setCustomerId(phoneNumber);

        // Field 42:
        // MERCHANT: 'MV' + AgentCode
        // SUBSCRIBER: 'MV' + PHONE_NUMBER(8)
        if (agentCode == null || agentCode.trim().isEmpty() || "NULL".equalsIgnoreCase(agentCode)) {
            agentCode = phoneNumber;
        }
        request.setUserName("MV" + agentCode);
        if (request.getUserName().startsWith("MV000000")) {
            request.setUserName(request.getUserName().replace("MV000000", "MV00"));
        }

    }

}

