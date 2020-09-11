package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class PayObligationValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;

    protected PayObligationValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        validateDepositOrPay(request);
        boolean validationCombinateParameters = false;

        if (request.getReferenceNumber() != null && request.getServiceCode() != null) {
            validationCombinateParameters = true;
            request.setTypePayBillDeposit("DEPOSIT");
        }
        if (!validationCombinateParameters) {
            StatusCode statusCode = statusCodeConfig.of("24");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateParameters(request);
    }

}

