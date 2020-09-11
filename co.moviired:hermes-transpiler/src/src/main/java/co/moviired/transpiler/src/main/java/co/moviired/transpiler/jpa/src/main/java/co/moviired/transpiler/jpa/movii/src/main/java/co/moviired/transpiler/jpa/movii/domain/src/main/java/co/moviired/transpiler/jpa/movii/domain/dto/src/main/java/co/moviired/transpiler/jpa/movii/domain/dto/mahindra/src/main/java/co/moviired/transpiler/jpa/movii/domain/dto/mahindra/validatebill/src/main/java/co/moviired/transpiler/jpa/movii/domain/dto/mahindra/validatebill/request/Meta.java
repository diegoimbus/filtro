package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "requestDate",
        "customerId",
        "userName",
        "deviceCode",
        "requestReference",
        "passwordHash",
        "channel",
        "requestSource",
        "systemId",
        "originAddress"
})
public class Meta implements Serializable {

    private static final long serialVersionUID = 6298336218741426532L;

    private String systemId;
    private String originAddress;
    private String requestDate;
    private String customerId;
    private String requestReference;
    private String channel;
    private String deviceCode;
    private String requestSource;
    private String userName;
    private String passwordHash;

}

