package co.moviired.transpiler.integration.rest.dto.validatebill.response;

import co.moviired.transpiler.common.response.DataValidateByReference;
import co.moviired.transpiler.integration.rest.dto.IRestResponse;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class ResponseValidateBillByReferenceDTO implements IRestResponse {

    private static final long serialVersionUID = -3313199501146740183L;

    private DataValidateByReference data;

    private Outcome outcome;

}

