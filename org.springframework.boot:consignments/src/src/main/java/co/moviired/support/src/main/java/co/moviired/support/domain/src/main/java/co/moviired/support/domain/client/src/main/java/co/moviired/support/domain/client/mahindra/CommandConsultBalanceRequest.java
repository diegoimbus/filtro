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
        "provider",
        "payid",
        "mpin"

})
@JsonRootName("command")
public class CommandConsultBalanceRequest implements Serializable {


    private static final long serialVersionUID = -8623333790830215955L;

    private String type;

    private String msisdn;

    private String provider;

    private String payid;

    private String mpin;

    @Override
    public String toString() {
        return String.format(
                "<COMMAND><TYPE>%s</TYPE><MSISDN>%s</MSISDN><PROVIDER>%s</PROVIDER><PAYID>%s</PAYID><MPIN>****</MPIN></COMMAND>",
                type, msisdn, provider, payid);
    }
}

