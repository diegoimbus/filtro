package co.moviired.business.helper;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.Modality;

import javax.validation.constraints.NotNull;

public final class UtilHelper {

    private static final String SEPARATOR = "|";

    private UtilHelper() {
        super();
    }

    public static boolean stringNotNullOrNotEmpty(String textValidate) {
        return textValidate != null && !textValidate.isBlank();
    }

    public static boolean validateBooleanInString(String validateValue) {
        boolean newBoolean = false;
        if (validateValue != null) {
            if (validateValue.matches("\\d+")) {
                int value = Integer.parseInt(validateValue);
                newBoolean = (value == 1);
            } else {
                newBoolean = Boolean.parseBoolean(validateValue);
            }
        }
        return newBoolean;
    }

    public static String generateImei(@NotNull RequestFormatBanking bankingRequest) {
        StringBuilder imei = new StringBuilder();
        imei.append("0");
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getIp());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getSource());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getCorrelationId());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getComponentDate());
        imei.append(SEPARATOR);
        imei.append(SEPARATOR);
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getAgentCode());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getMsisdn1());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getHomologateIncom());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getTercId());
        imei.append(SEPARATOR);
        imei.append(bankingRequest.getHomologateBankId());
        if (bankingRequest.getModality().equals(Modality.BATCH)) {
            imei.append(SEPARATOR);
            imei.append(bankingRequest.getGestorId());
        }
        imei.trimToSize();
        return imei.toString();
    }

}

