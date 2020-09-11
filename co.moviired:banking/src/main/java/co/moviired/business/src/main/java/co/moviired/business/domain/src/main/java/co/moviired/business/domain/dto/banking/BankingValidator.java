package co.moviired.business.domain.dto.banking;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;

import java.io.Serializable;

public abstract class BankingValidator implements Serializable {

    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;
    private transient StatusCode statusCode;

    protected BankingValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        this.statusCodeConfig = statusCodeConfig;
        this.bankingProperties = bankingProperties;
    }

    protected final void validateAuthorization(RequestFormatBanking requestFormat, String autorization) throws DataException {
        statusCode = statusCodeConfig.of("4");
        if (!autorization.trim().matches("")) {
            try {
                String[] vautorization = autorization.split(":");
                if (!vautorization[0].isBlank() && !vautorization[1].isBlank()) {
                    requestFormat.setMsisdn1(vautorization[0]);
                    requestFormat.setMpin(vautorization[1]);
                } else {
                    throw new DataException(statusCode.getCode(), statusCode.getMessage());
                }
            } catch (Exception e) {
                throw new DataException("-1", e.getMessage());
            }
        } else {
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    protected final void validatePosIdMerchant(RequestFormatBanking requestFormat, String merchantId, String posId) throws DataException {
        if (!merchantId.isBlank()) {
            requestFormat.setAgentCode(merchantId);
        } else {
            statusCode = statusCodeConfig.of("5");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (!posId.isBlank()) {
            requestFormat.setPosId(posId);
        } else {
            statusCode = statusCodeConfig.of("6");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    protected final void validateParametersBill(RequestFormatBanking requestFormat) throws DataException {
        if (requestFormat.getSource() == null) {
            statusCode = statusCodeConfig.of("7");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormat.getIp() == null) {
            statusCode = statusCodeConfig.of("8");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormat.getLastName() == null) {
            statusCode = statusCodeConfig.of("9");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    protected final void validateParameters(RequestFormatBanking requestFormat) throws DataException {
        if (requestFormat.getSource() == null) {
            statusCode = statusCodeConfig.of("7");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormat.getIp() == null) {
            statusCode = statusCodeConfig.of("8");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormat.getLastName() == null) {
            statusCode = statusCodeConfig.of("9");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormat.getGestorId() == null) {
            statusCode = statusCodeConfig.of("10");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    protected final void validateDepositOrPay(RequestFormatBanking request) throws DataException {
        if (request.getGestorId() == null) {
            statusCode = statusCodeConfig.of("10");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAmount() == null) {
            statusCode = statusCodeConfig.of("13");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getBillerName() == null) {
            statusCode = statusCodeConfig.of("14");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypeClient() == null) {
            statusCode = statusCodeConfig.of("19");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    protected final void validateQueryOrWithdrawal(RequestFormatBanking request) throws DataException {
        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAccountType() == null) {
            statusCode = statusCodeConfig.of("20");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypeDocument() == null) {
            statusCode = statusCodeConfig.of("21");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAccountOrdinal() == null && request.getGestorId().equalsIgnoreCase(bankingProperties.getGestorIdBBVA())) {
            statusCode = statusCodeConfig.of("22");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    public abstract void validationInput(RequestFormatBanking requestFormat, String merchantId, String posId, String userpass) throws DataException;

}
