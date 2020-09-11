package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
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
public class CommandValidateBillByEan implements ICommandRequest {

    private static final long serialVersionUID = -6672790264787292336L;

    private Meta meta;
    private DataValidateBillByEan data;
    private RequestSignature requestSignature;

}

