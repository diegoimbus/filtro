package co.moviired.transpiler.integration.rest.dto.digitalcontent.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        "customerId",
        "sessionId",
        "channel",
        "systemId",
        "deviceId",
        "mac",
        "deviceCode",
        "requestReference",
        "originAddress",
        "requestDate",
        "userName",
        "passwordHash",
        "password"
})
public class Meta implements Serializable {

    private static final long serialVersionUID = 6298336218741426532L;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("systemId")
    private String systemId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("requestReference")
    private String requestReference;

    @JsonProperty("originAddress")
    private String originAddress;

    @JsonProperty("requestDate")
    private String requestDate;


    @JsonProperty("userName")
    private String userName;

    @JsonProperty("passwordHash")
    private String passwordHash;

    @JsonProperty("password")
    private String password;

}

