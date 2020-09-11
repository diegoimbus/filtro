package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "statusCode",
        "message",
        "error"
})
public class Outcome implements Serializable {

    private static final long serialVersionUID = -446677803855067553L;

    private String statusCode;

    private String message;

    private Error error;

}

