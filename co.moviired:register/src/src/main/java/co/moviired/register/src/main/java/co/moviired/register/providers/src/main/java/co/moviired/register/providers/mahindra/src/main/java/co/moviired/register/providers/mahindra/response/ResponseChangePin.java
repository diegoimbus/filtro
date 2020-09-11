package co.moviired.register.providers.mahindra.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "txnStatus",
        "trid",
        "message",
        "ivrResponse"
})
@JsonRootName("COMMAND")
public class ResponseChangePin {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("TXNSTATUS")
    private String txnStatus;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("IVR-RESPONSE")
    private String ivrResponse;

}

