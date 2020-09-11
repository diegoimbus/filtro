package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class QueryObligationsValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;

    public QueryObligationsValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        StatusCode statusCode;
        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypeDocument() == null) {
            statusCode = statusCodeConfig.of("21");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getNumberDocument() == null) {
            statusCode = statusCodeConfig.of("29");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        validatePosIdMerchant(request, merchantId, posId);
        validateAuthorization(request, userpass);
        validateParameters(request);
    }

}
