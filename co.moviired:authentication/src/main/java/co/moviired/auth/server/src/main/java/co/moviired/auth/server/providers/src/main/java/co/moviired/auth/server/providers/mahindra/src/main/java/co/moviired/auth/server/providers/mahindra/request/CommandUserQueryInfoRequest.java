package co.moviired.auth.server.providers.mahindra.request;

import co.moviired.auth.server.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
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
@JsonRootName("command")
public class CommandUserQueryInfoRequest implements IRequest {

    private String type;

    private String provider;

    private String msisdn;

    private String usertype;

    private String trid;

    public CommandUserQueryInfoRequest(CommandUserQueryInfoRequest pcommandUserQueryInfoRequest) {
        this.type = pcommandUserQueryInfoRequest.type;
        this.provider = pcommandUserQueryInfoRequest.provider;
        this.msisdn = pcommandUserQueryInfoRequest.msisdn;
        this.usertype = pcommandUserQueryInfoRequest.usertype;
        this.trid = pcommandUserQueryInfoRequest.trid;
    }
}

