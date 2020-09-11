package co.moviired.topups.model.domain.dto.recharge.request;

import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationHeaderRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RechargeIntegrationHeaderRequest implements IRechargeIntegrationHeaderRequest {

    private static final long serialVersionUID = -1697255819293076181L;

    private String referenceNumber;
    private String authorization;
    private String contentType;
    private String merchantId;
    private String posId;
    private long componentDate;
}

