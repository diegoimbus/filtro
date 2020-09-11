package co.moviired.support.domain.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @author carlossaul.ramirez
 * @version 1.0.2
 * @category BarUnBar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "action",
        "userType",
        "msisdn",
        "provider",
        "barType",
        "reason",
        "remark"
})
@JsonRootName("command")
public class CommandBarUnbarRequest implements Serializable {

    private static final long serialVersionUID = -7016805472898431721L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("ACTION")
    private String action;

    @JsonProperty("USERTYPE")
    private String userType;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("BARTYPE")
    private String barType;

    @JsonProperty("REASON")
    private String reason;

    @JsonProperty("REMARK")
    private String remark;

}

