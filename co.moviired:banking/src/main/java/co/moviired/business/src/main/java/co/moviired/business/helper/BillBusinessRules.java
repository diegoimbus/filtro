package co.moviired.business.helper;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BillBusinessRules {

    private final StatusCodeConfig statusCodeConfig;

    public void executeRuleCollectionType(RequestFormatBanking bankingRequest, Biller biller) throws ProcessingException {
        StatusCode statusCode;
        if ((CollectionType.MANUAL.equals(bankingRequest.getTypePayBill())) && (CollectionType.AUTOMATIC == biller.getCollectionType())) {
            statusCode = statusCodeConfig.of("36");
            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
        if ((CollectionType.AUTOMATIC.equals(bankingRequest.getTypePayBill())) && (CollectionType.MANUAL == biller.getCollectionType())) {
            statusCode = statusCodeConfig.of("37");
            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    public void executeRuleMinMaxValue(RequestFormatBanking request, Biller biller) throws ProcessingException {
        StatusCode statusCode;
        if (Integer.parseInt(request.getAmount()) > biller.getMaxValue()) {
            statusCode = statusCodeConfig.of("38");
            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
        if (Integer.parseInt(request.getAmount()) < biller.getMinValue()) {
            statusCode = statusCodeConfig.of("39");
            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    public void executeRuleMultiple(RequestFormatBanking request, Biller biller) throws ProcessingException {
        StatusCode statusCode;
        if (Integer.parseInt(request.getAmount()) % biller.getMultiple() != 0) {
            statusCode = statusCodeConfig.of("40");
            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage() + " " + biller.getMultiple());
        }
    }

}

