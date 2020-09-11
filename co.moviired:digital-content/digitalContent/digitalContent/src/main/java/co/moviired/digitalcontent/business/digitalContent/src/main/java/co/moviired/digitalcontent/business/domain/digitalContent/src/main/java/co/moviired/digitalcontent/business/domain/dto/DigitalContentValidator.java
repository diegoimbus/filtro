package co.moviired.digitalcontent.business.domain.dto;


import co.moviired.base.domain.exception.DataException;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.helper.Utilidades;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public abstract class DigitalContentValidator implements Serializable {

    private static final String ERRORPARAMETROS = "Error de parámetros AUTHORIZATION.";
    private static final String LBLAMOUNT = "No se encontró el campo amount.";
    private static final String LBLAMOUNTVALIDO = "Debe ingresar un amount válido.";
    private static final String LBLPHONENUMBER = "El phoneNumber debe ser numérico.";
    private static final String LBLPHONENUMBERVALIDO = "Debe ingresar un phoneNumber válido.";

    public DigitalContentValidator() {
        super();
    }

    protected static void validateAuthorization(DigitalContentRequest requestFormat, String autorization) throws DataException {
        if (!autorization.trim().matches("")) {
            try {
                String[] vautorization = autorization.split(":");
                if (!vautorization[0].trim().matches("")) {
                    if (!vautorization[1].trim().matches("")) {
                        requestFormat.setMsisdn1(vautorization[0]);
                        requestFormat.setMpin(vautorization[1]);
                    } else {
                        throw new DataException("-2", ERRORPARAMETROS);
                    }
                } else {
                    throw new DataException("-3", ERRORPARAMETROS);
                }

            } catch (Exception e) {
                throw new DataException("-4", ERRORPARAMETROS, e);
            }

        } else {
            throw new DataException("-5", ERRORPARAMETROS);
        }
    }

    protected static void validateParameters(DigitalContentRequest requestFormat) throws DataException {

        if (requestFormat.getCorrelationId() == null) {
            throw new DataException("-2", "correlationId es un parámetro obligatorio");
        }

        if (requestFormat.getIssueDate() == null) {
            throw new DataException("-2", "issueData es un parámetro obligatorio");
        } else if (!requestFormat.getIssueDate().trim().matches("[0-9]{14}.?[0-9]{3}")) {
            throw new DataException("-2", "El campo issuerDate no cumple con el formato yyyymmddhhmiss.SSS");
        }

        if (requestFormat.getIssuerLogin() == null || requestFormat.getIssuerLogin().isEmpty()) {
            throw new DataException("-2", "issuerLogin es un parámetro obligatorio");
        }
        requestFormat.setIssuerLogin(StringUtils.leftPad(requestFormat.getIssuerLogin().trim(), 4, "0"));

        if (requestFormat.getSource() == null || requestFormat.getSource().isEmpty()) {
            throw new DataException("-2", "source es un parámetro obligatorio");
        }

    }

    protected static void validatePostMerch(DigitalContentRequest requestFormat, String merchantId, String posId) throws DataException {
        if (!merchantId.trim().matches("")) {
            requestFormat.setAgentCode(merchantId);
        } else {
            throw new DataException("-2", "El merchantId es un parametro obligatorio");
        }

        if (posId.trim().matches("[0-9]+")) {
            requestFormat.setPosId(StringUtils.leftPad(posId, 4, "0"));
        } else if (posId.trim().matches(".+")) {
            throw new DataException("-2", "El postId solo puede contener valores númericos");
        } else {
            throw new DataException("-2", "El postId es un parametro obligatorio");
        }
    }

    protected static void validateAmount(DigitalContentRequest requestFormat) throws DataException {
        if ((requestFormat.getAmount() == null) || requestFormat.getAmount().trim().matches("")) {
            throw new DataException("-2", LBLAMOUNT);
        }

        if (!Utilidades.isInteger(requestFormat.getAmount())) {
            throw new DataException("-2", LBLAMOUNTVALIDO);
        }

        if (Long.parseLong(requestFormat.getAmount()) <= 0) {
            throw new DataException("-2", LBLAMOUNTVALIDO);
        }
    }

    protected static void validatePhoneNumber(DigitalContentRequest requestFormat, String phoneNumber) throws DataException {
        if (Utilidades.isInteger(phoneNumber)) {
            if (Long.parseLong(phoneNumber) <= 0) {
                throw new DataException("-2", LBLPHONENUMBER);
            } else {
                requestFormat.setPhoneNumber(phoneNumber);
            }
        } else {
            throw new DataException("-2", LBLPHONENUMBERVALIDO);
        }
    }

    public abstract void validationInput(DigitalContentRequest requestFormat, String merchantId, String posId, String userpass) throws DataException;


}

