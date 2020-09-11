package co.moviired.topups.model.domain.dto.recharge.response;

import co.moviired.topups.model.domain.dto.recharge.IOperatorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OperatorIntegrationResponse implements IOperatorResponse {

    private static final long serialVersionUID = -8330173237133550789L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private List<OperatorResponse> operators;
}

