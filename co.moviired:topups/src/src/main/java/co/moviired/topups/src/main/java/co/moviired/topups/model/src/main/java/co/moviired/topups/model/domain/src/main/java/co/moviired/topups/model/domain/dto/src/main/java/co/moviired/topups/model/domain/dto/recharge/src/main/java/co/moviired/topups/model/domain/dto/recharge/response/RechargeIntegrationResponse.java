package co.moviired.topups.model.domain.dto.recharge.response;


import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RechargeIntegrationResponse implements IRechargeIntegrationResponse {

    private static final long serialVersionUID = -493986155012893249L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String transactionDate;

    private String transferId;

    private String correlationId;

    private String authorizationCode;

    private String invoiceNumber;

    private String amount;

    private String packageAmount;

    private String operatorName;

    private String productName;

    private String gestorId;

    private String expirationDate;

    private String customerBalance;


}

