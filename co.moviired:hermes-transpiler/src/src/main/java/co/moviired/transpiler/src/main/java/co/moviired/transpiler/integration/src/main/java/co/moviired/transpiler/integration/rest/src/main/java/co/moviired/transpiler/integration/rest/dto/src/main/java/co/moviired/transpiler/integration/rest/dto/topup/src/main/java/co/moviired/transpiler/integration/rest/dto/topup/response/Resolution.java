package co.moviired.transpiler.integration.rest.dto.topup.response;

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
        "customerId",
        "NUFA_ID",
        "expeditionDate",
        "resolutionNumber",
        "maxNumber",
        "minNumber"
})
public class Resolution implements Serializable {

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("NUFA_ID")
    private String nufaId;

    @JsonProperty("expeditionDate")
    private String expeditionDate;

    @JsonProperty("resolutionNumber")
    private String resolutionNumber;

    @JsonProperty("maxNumber")
    private String maxNumber;

    @JsonProperty("minNumber")
    private String minNumber;

}

