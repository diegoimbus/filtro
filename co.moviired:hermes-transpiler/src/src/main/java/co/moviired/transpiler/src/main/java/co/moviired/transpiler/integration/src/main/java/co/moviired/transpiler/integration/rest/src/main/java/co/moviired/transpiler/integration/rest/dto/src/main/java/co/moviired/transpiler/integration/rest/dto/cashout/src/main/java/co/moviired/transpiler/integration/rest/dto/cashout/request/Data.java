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
        "EANCode",
        "destinationNumber",
        "ECHOTXND",
        "TXND",
        "STATUS"
})
public class Data implements Serializable {

    private static final long serialVersionUID = 744753271021788410L;

    @JsonProperty("EANCode")
    private String eancode;

    @JsonProperty("destinationNumber")
    private String destinationNumber;

    @JsonProperty("ECHOTXND")
    private String echoTxnd;

    @JsonProperty("TXND")
    private String txnd;

    @JsonProperty("STATUS")
    private String status;

}

