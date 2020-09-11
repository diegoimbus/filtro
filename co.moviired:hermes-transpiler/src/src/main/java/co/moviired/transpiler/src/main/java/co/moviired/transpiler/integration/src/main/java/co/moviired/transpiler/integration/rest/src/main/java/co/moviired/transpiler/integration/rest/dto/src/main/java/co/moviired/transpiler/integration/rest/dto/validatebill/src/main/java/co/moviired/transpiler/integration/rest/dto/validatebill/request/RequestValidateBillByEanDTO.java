package co.moviired.transpiler.integration.rest.dto.validatebill.request;

import co.moviired.transpiler.integration.rest.dto.IRestRequest;
import co.moviired.transpiler.integration.rest.dto.common.request.Meta;
import co.moviired.transpiler.integration.rest.dto.common.request.RequestSignature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "meta",
        "data",
        "requestSignature"
})
public class RequestValidateBillByEanDTO implements IRestRequest {

    private static final long serialVersionUID = -6672790264787292336L;

    private Meta meta;

    private DataValidateBillByEan data;

    private RequestSignature requestSignature;

}

