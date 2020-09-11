package co.moviired.business.domain.dto.banking;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.validator.*;
import co.moviired.business.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@AllArgsConstructor
public class ValidatorFactory implements Serializable {

    private final QueryValidator queryValidator;
    private final StatusCodeConfig statusCodeConfig;
    private final CashOutValidator cashOutValidator;
    private final DepositValidator depositValidator;
    private final PayBillValidator ppayBillValidator;
    private final QueryBillValidator queryBillValidator;
    private final PayObligationValidator obligationValidator;
    private final QueryWithdrawalValidator queryWithdrawalValidator;
    private final QueryObligationsValidator queryObligationsValidator;
    private final ValidateBillPaymentValidator validateBillPayValidator;

    public final BankingValidator getValidator(OperationType operationType) throws ParsingException {
        BankingValidator validator;
        switch (operationType) {

            case WITHDRAWAL:
                validator = this.cashOutValidator;
                break;

            case DEPOSIT:
                validator = this.depositValidator;
                break;

            case QUERY:
                validator = this.queryValidator;
                break;

            case QUERY_WITHDRAWAL:
                validator = this.queryWithdrawalValidator;
                break;

            case QUERY_OBLIGATION:
                validator = this.queryObligationsValidator;
                break;

            case QUERY_BILL:
                validator = this.queryBillValidator;
                break;

            case PAY_BILL:
                validator = this.ppayBillValidator;
                break;

            case PAY_OBLIGATION:
                validator = this.obligationValidator;
                break;

            case VALIDATE_BILLPAYMENT:
                validator = this.validateBillPayValidator;
                break;

            default:
                StatusCode statusCode = statusCodeConfig.of("1");
                throw new ParsingException(statusCode.getCode(), statusCode.getMessage());
        }
        return validator;
    }

}

