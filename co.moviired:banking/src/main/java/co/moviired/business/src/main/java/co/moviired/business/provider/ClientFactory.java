package co.moviired.business.provider;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.enums.Modality;
import co.moviired.business.domain.enums.OperationType;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.IntegratorProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.connector.connector.ReactiveConnector;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
@AllArgsConstructor
public class ClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;
    private final MahindraProperties mahindraProperties;
    private final ReactiveConnector mhTransactionalClient;
    private final IntegratorProperties integratorProperties;
    private final ReactiveConnector queryBankingSwitchClient;
    private final ReactiveConnector cashOutBankingSwitchClient;
    private final ReactiveConnector queryByEanCodeIntegratorClient;
    private final ReactiveConnector queryByReferenceIntegratorClient;

    public final ReactiveConnector getClient(@NotNull OperationType operationType,
                                             @NotNull RequestFormatBanking bankingRequest) throws ParsingException, ProcessingException {
        ReactiveConnector client;
        switch (operationType) {
            case CASH_IN:
            case LOGIN_USER:
            case DEPOSIT:
            case PAY_BILL:
            case PAY_OBLIGATION:
                client = this.mhTransactionalClient;
                bankingRequest.setUrl(" Mahindra " + mahindraProperties.getUrlTransactional());
                break;

            case WITHDRAWAL:
                client = this.cashOutBankingSwitchClient;
                bankingRequest.setUrl(" BankingSwitch " + bankingProperties.getUrlBankingCashOut());
                break;

            case QUERY_BILL:
                if (Modality.ONLINE.equals(bankingRequest.getModality())) {
                    if (CollectionType.MANUAL.equals(bankingRequest.getTypePayBill())) {
                        client = this.queryByReferenceIntegratorClient;
                        bankingRequest.setUrl(" Integrador " + integratorProperties.getUrlIntegratorValidateByReference());
                    } else {
                        client = this.queryByEanCodeIntegratorClient;
                        bankingRequest.setUrl(" Integrador " + integratorProperties.getUrlIntegratorValidateByEANCode());
                    }
                } else if (Modality.BATCH.equals(bankingRequest.getModality())) {
                    client = this.mhTransactionalClient;
                    bankingRequest.setUrl(" Mahindra " + mahindraProperties.getUrlTransactional());
                } else {
                    StatusCode statusCode = statusCodeConfig.of("2");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
                break;

            case QUERY:
            case QUERY_OBLIGATION:
            case QUERY_WITHDRAWAL:
                client = this.queryBankingSwitchClient;
                bankingRequest.setUrl(" BankingSwitch " + bankingProperties.getUrlBankingQuery());
                break;

            case VALIDATE_BILLPAYMENT_EANCODE:
                client = this.queryByEanCodeIntegratorClient;
                break;

            case VALIDATE_BILLPAYMENT_REFERENCE:
                client = this.queryByReferenceIntegratorClient;
                break;

            default:
                StatusCode statusCode = statusCodeConfig.of("1");
                throw new ParsingException(statusCode.getCode(), statusCode.getMessage());
        }

        return client;
    }

}

