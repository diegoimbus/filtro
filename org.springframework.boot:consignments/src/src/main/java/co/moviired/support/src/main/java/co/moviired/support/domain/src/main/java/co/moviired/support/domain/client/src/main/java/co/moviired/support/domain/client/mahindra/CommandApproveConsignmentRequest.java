package co.moviired.support.domain.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "msisdn",
        "amount",
        "bankid",
        "referenceid",
        "ftxnid"

})
@JsonRootName("command")
public class CommandApproveConsignmentRequest implements Serializable {

    private static final long serialVersionUID = 2030137178335114392L;

    private String type;

    private String msisdn;

    private String amount;

    private String bankid;

    private String referenceid;

    private String ftxnid;

}

