package co.moviired.transpiler.integration.rest.dto.cashout.request;

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
        "systemSignature"
})
public class RequestSignature implements Serializable {

    private static final long serialVersionUID = -6347030810341790265L;

    @JsonProperty("systemSignature")
    private String systemSignature;

}

