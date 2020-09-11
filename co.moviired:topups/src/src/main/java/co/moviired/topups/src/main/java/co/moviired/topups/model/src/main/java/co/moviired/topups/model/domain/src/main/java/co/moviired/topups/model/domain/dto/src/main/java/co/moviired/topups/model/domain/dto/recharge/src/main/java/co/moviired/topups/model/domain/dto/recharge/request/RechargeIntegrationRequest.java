package co.moviired.topups.model.domain.dto.recharge.request;

import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationRequest;
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
public class RechargeIntegrationRequest implements IRechargeIntegrationRequest {

    private static final long serialVersionUID = -777988920030560046L;

    private String correlationId;
    private String issuerDate;
    private String issuerName;
    private String issuerId;
    private String amount;
    private String packageAmount;
    private String source;
    private String ip;
    private String productId;
    private String packageId;
    private String eanCode;

    // Optional
    private String echoData;
    private String imei;
}

