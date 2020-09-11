package co.moviired.register.providers.mahindra.request;


import co.moviired.register.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
        "type",
        "misdn",
        "mpin",
        "newmpin",
        "confirmmpin",
        "language1"
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName("COMMAND")
public class RequestChangePin implements IRequest {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("MSISDN")
    private String misdn;

    @JsonProperty("MPIN")
    private String mpin;

    @JsonProperty("NEWMPIN")
    private String newmpin;

    @JsonProperty("CONFIRMMPIN")
    private String confirmmpin;

    @JsonProperty("LANGUAGE1")
    private String language1;

}

