package co.moviired.business.provider.mahindra.request;

import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "type",
        "provider",
        "msisdn",
        "mpin",
        "otpreq",
        "ispincheckreq",
        "source"
})
@JsonRootName("command")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommandLoginServiceRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;
    private String provider;
    private String msisdn;
    private String mpin;
    private String otpreq;
    private String ispincheckreq;
    private String source;

}

