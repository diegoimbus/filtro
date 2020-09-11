package co.moviired.auth.server.providers.mahindra.request;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.auth.server.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
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
        "type",
        "provider",
        "msisdn",
        "srvreqtype",
        "usertype",
        "language1"
})

@JsonRootName("command")
public class CommandGenerateOTPRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String provider;

    private String msisdn;

    private String srvreqtype;

    private String usertype;

    private String language1;


}


