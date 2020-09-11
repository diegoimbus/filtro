package co.moviired.transpiler.integration.rest.dto.cashout.response;

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
class Outcome implements Serializable {

    private static final long serialVersionUID = -7559943806409685834L;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private Error error;

}

