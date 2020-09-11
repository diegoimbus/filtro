package co.moviired.business.provider.integrator.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "systemSignature"
})
public class RequestSignature implements Serializable {

    private static final long serialVersionUID = -6347030810341790265L;

    private String systemSignature;

}

