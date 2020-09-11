package co.moviired.support.provider.mahindra.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "TYPE",
        "PROVIDER",
        "MSISDN",
        "USERTYPE"
})

@JsonRootName("COMMAND")
public class CommandUserServiceRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    @JsonProperty("TYPE")
    private String type;
    @JsonProperty("PROVIDER")
    private String provider;
    @JsonProperty("MSISDN")
    private String msisdn;
    @JsonProperty("USERTYPE")
    private String usertype;


}


