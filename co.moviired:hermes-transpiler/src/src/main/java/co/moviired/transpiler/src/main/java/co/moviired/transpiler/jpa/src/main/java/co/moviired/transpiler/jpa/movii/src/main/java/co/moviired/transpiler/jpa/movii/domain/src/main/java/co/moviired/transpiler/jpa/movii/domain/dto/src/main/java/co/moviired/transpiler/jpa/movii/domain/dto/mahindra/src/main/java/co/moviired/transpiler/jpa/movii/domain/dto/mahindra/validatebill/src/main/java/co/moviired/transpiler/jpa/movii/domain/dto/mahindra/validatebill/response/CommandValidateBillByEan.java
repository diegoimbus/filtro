package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response;

import co.moviired.transpiler.common.response.DataValidateByEan;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "outcome",
        "data"
})
public class CommandValidateBillByEan implements ICommandResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    private Outcome outcome;
    private DataValidateByEan data;

}

