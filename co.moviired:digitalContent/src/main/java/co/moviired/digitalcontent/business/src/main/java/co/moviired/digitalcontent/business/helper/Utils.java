package co.moviired.digitalcontent.business.helper;

import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.mahindra.request.Command;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
public final class Utils implements Serializable {

    private static final long serialVersionUID = 9142537210745235360L;

    private static final String LBL_DIGITAL_CONTENT = "DIGITAL-CONTENT";
    private static final int MAX_CORRELATION_LENGTH = 20;

    private Utils() {
        super();
    }

    public static boolean isInteger(String numero) {
        try {
            Long.parseLong(numero);
            return true;
        } catch (NumberFormatException e) {
            log.error("Ocurrio un error al intentar formatear el numero: " + e.getMessage());
            return false;
        }
    }

    public static String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result = result.replace("-", "");
        result = result.substring(0, MAX_CORRELATION_LENGTH);
        return result;
    }

    // Generar el IMEI el número de transacción del cliente
    public static String generateIMEI(@NotNull DigitalContentRequest data, @NotNull String ip, String pagentCode) {
        String agentCode = pagentCode;
        if (agentCode == null || agentCode.trim().isEmpty() || "NULL".equalsIgnoreCase(agentCode)) {
            agentCode = "";
        }

        StringBuilder imei = new StringBuilder();
        imei.append("0");
        imei.append("|");
        imei.append(ip);
        imei.append("|");
        imei.append(LBL_DIGITAL_CONTENT);
        imei.append("|");
        imei.append(data.getCorrelationId());
        imei.append("|");
        imei.append(data.getIssueDate());
        imei.append("|");
        imei.append(data.getIssuerLogin());
        imei.append("|");
        imei.append(data.getPosId());
        imei.append("|");
        imei.append(agentCode);
        imei.append("|");
        imei.append(data.getMsisdn1());
        imei.append("|");
        imei.append(data.getIncommCode());
        imei.trimToSize();

        return imei.toString();
    }

    public static void setTypeAndSubtype(
            @NotNull Command request,
            @NotNull MahindraProperties mahindraProperties,
            @NotNull CommandResponse mhResponseAutenticacion,
            @NotNull String operacion,
            @NotNull GlobalProperties config) {

        if ((operacion.equalsIgnoreCase(config.getProcessActivate())) ||
                (operacion.equalsIgnoreCase(config.getProcessInactivate()))) {
            if (mhResponseAutenticacion.getUsertype().equalsIgnoreCase("CHANNEL")) {
                request.setType(mahindraProperties.getTypeCardMerchant());
                request.setSubtype(mahindraProperties.getSubTypeCardMerchant());
            } else {
                request.setType(mahindraProperties.getTypeCardSubcriber());
                request.setSubtype(mahindraProperties.getSubTypeCardSubcriber());
            }
        } else {
            if (operacion.equalsIgnoreCase(config.getProcessPines())) {
                if (mhResponseAutenticacion.getUsertype().equalsIgnoreCase("CHANNEL")) {
                    request.setType(mahindraProperties.getTypePinMerchant());
                    request.setSubtype(mahindraProperties.getSubTypePinMerchant());
                } else {
                    request.setType(mahindraProperties.getTypePinSubcriber());
                    request.setSubtype(mahindraProperties.getSubTypePinSubcriber());
                }
            }
        }
    }
}

