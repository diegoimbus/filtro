package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class QueryWithdrawalValidator extends BankingValidator {

    public QueryWithdrawalValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateQueryOrWithdrawal(request);
        validateParameters(request);
    }

}
