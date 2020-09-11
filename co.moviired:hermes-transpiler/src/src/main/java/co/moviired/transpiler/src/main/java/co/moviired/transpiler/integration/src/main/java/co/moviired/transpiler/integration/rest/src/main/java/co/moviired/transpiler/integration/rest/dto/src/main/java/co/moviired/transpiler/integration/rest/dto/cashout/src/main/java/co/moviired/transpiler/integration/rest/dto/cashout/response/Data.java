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
        "EANCode",
        "destinationNumber"
})
class Data implements Serializable {

    private static final long serialVersionUID = 4270476654419564794L;

    @JsonProperty("EANCode")
    private String eanCode;

    @JsonProperty("destinationNumber")
    private String destinationNumber;

}

