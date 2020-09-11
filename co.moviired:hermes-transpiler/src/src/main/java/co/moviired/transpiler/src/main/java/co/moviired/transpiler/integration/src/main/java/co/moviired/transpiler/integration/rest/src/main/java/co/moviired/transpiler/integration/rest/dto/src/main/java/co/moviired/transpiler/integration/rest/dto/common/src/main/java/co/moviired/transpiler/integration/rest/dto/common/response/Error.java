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
        "errorType",
        "errorCode",
        "errorMessage"
})
public class Error implements Serializable {

    private static final long serialVersionUID = -1463612606363571078L;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorCode")
    private String errorCode;

}

