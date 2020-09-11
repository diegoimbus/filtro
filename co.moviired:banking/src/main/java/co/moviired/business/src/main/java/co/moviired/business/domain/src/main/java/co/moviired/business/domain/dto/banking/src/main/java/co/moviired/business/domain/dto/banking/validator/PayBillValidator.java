package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class PayBillValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;

    protected PayBillValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        StatusCode statusCode;
        if (request.getAmount() == null) {
            statusCode = statusCodeConfig.of("13");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getBillerName() == null) {
            statusCode = statusCodeConfig.of("14");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypePayBill() == null) {
            statusCode = statusCodeConfig.of("15");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getEchoData() == null) {
            statusCode = statusCodeConfig.of("16");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        } else if (request.getEchoData().isEmpty()) {
            request.setEchoData("vacio");
        }

        if (CollectionType.MANUAL.equals(request.getTypePayBill()) && request.getServiceCode() == null) {
            statusCode = statusCodeConfig.of("17");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());

        } else if (CollectionType.AUTOMATIC.equals(request.getTypePayBill()) && request.getEan13BillerCode() == null) {
            statusCode = statusCodeConfig.of("18");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateParameters(request);
    }

}
