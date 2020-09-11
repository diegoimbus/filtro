package co.moviired.business.provider;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.enums.Modality;
import co.moviired.business.domain.enums.OperationType;
import co.moviired.business.provider.bankingswitch.parser.CashOutBankingParser;
import co.moviired.business.provider.bankingswitch.parser.QueryBankingParser;
import co.moviired.business.provider.integrator.parser.ValidatePayBillByEANCodeParser;
import co.moviired.business.provider.integrator.parser.ValidatePayBillByReferenceParser;
import co.moviired.business.provider.mahindra.parser.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
@AllArgsConstructor
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final StatusCodeConfig statusCodeConfig;
    private final QueryBankingParser queryBankingParser;
    private final CashInMahindraParser cashInMahindraParser;
    private final CashOutBankingParser cashOutBankingParser;
    private final BillPayMahindraParser billPayMahindraParser;
    private final LoginServiceMahindraParser loginServiceMahindraParser;
    private final PayBillBatchMahindraParser payBillBatchMahindraParser;
    private final ValidatePayBillByEANCodeParser validateBillEANCodeParser;
    private final QueryBillBatchMahindraParser queryBillBatchMahindraParser;
    private final ValidatePayBillByReferenceParser validateBillReferenceParser;

    public final IParser getParser(@NotNull OperationType operationType,
                                   @NotNull RequestFormatBanking bankingRequest) throws ProcessingException {
        IParser parser;
        switch (operationType) {
            case CASH_IN:
                parser = this.cashInMahindraParser;
                break;

            case PAY_BILL:
                if (Modality.ONLINE.equals(bankingRequest.getModality())) {
                    parser = this.billPayMahindraParser;
                } else if (Modality.BATCH.equals(bankingRequest.getModality())) {
                    parser = this.payBillBatchMahindraParser;
                } else {
                    StatusCode statusCode = statusCodeConfig.of("2");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
                break;

            case DEPOSIT:
            case PAY_OBLIGATION:
                parser = this.billPayMahindraParser;
                break;

            case LOGIN_USER:
                parser = this.loginServiceMahindraParser;
                break;

            case WITHDRAWAL:
                parser = this.cashOutBankingParser;
                break;

            case QUERY_BILL:
                if (Modality.ONLINE.equals(bankingRequest.getModality())) {
                    if (CollectionType.MANUAL.equals(bankingRequest.getTypePayBill())) {
                        parser = this.validateBillReferenceParser;
                    } else {
                        parser = this.validateBillEANCodeParser;
                    }
                } else if (Modality.BATCH.equals(bankingRequest.getModality())) {
                    parser = this.queryBillBatchMahindraParser;
                } else {
                    StatusCode statusCode = statusCodeConfig.of("2");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
                break;

            case QUERY:
            case QUERY_OBLIGATION:
            case QUERY_WITHDRAWAL:
                parser = this.queryBankingParser;
                break;

            case VALIDATE_BILLPAYMENT_EANCODE:
                parser = this.validateBillEANCodeParser;
                break;

            case VALIDATE_BILLPAYMENT_REFERENCE:
                parser = this.validateBillReferenceParser;
                break;

            default:
                StatusCode statusCode = statusCodeConfig.of("1");
                throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
        return parser;
    }

}

