package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class CashOutValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;

    public CashOutValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
        this.bankingProperties = bankingProperties;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        StatusCode statusCode;
        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateParameters(request);

        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getServiceCode() == null) {
            statusCode = statusCodeConfig.of("12");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAmount() == null) {
            statusCode = statusCodeConfig.of("13");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAccountType() == null) {
            statusCode = statusCodeConfig.of("20");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypeDocument() == null && request.getGestorId().equals(bankingProperties.getGestorIdBBVA())) {
            statusCode = statusCodeConfig.of("21");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getAccountOrdinal() == null && request.getGestorId().equals(bankingProperties.getGestorIdBBVA())) {
            statusCode = statusCodeConfig.of("22");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getUpcId() == null && request.getGestorId().equals(bankingProperties.getGestorIdBBVA())) {
            statusCode = statusCodeConfig.of("27");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getOtp() == null) {
            statusCode = statusCodeConfig.of("28");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

}
