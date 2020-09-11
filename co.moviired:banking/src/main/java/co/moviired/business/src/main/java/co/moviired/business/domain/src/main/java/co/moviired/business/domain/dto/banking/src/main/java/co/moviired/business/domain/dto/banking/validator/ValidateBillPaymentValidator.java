package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.IntegratorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidateBillPaymentValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;
    @Autowired
    private IntegratorProperties integratorProperties;

    protected ValidateBillPaymentValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        StatusCode statusCode;
        if (request.getRequestDate() == null) {
            statusCode = statusCodeConfig.of("25");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getCollectionMethod() == null) {
            statusCode = statusCodeConfig.of("26");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getCollectionMethod().equals(integratorProperties.getCollectionManualReference()) && request.getServiceCode() == null) {
            statusCode = statusCodeConfig.of("12");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateParameters(request);
    }

}
