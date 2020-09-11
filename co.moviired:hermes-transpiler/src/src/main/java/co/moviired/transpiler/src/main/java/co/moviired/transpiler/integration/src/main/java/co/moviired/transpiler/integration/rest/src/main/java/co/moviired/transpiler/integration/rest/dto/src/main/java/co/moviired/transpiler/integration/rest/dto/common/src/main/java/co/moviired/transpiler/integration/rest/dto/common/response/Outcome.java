package co.moviired.transpiler.integration.rest.dto.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("error")
    private Error error;

}

