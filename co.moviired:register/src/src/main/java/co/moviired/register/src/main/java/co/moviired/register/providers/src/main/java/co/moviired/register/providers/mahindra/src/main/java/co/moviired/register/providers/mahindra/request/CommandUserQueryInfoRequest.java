package co.moviired.register.providers.mahindra.request;

import co.moviired.register.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Sánchez, Manuel
 * @version 1, 2019
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "provider",
        "msisdn",
        "usertype",
        "trid"
})
@JsonRootName("COMMAND")
public class CommandUserQueryInfoRequest implements IRequest {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("USERTYPE")
    private String usertype;

    @JsonProperty("TRID")
    private String trid;
}

