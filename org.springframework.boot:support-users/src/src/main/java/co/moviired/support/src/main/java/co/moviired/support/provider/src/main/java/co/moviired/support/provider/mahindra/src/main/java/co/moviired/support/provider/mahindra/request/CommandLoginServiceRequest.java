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
        "mpin",
        "otpreq",
        "ispincheckreq",
        "source"
})

@JsonRootName("command")
public class CommandLoginServiceRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String provider;

    private String userLogin;

    private String pin;

    private String otpreq;

    private String ispincheckreq;

    private String source;

    private String imei;

}


