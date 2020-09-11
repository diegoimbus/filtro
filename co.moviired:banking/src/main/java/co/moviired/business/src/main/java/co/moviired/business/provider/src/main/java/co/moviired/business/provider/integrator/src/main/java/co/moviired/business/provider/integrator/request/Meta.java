package co.moviired.business.provider.integrator.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

@Data
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
