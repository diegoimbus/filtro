package co.moviired.register.providers.mahindra.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Sánchez, Manuel
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.register.providers.IResponse;
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
        "type",
        "msisdn",
        "txnstatus",
        "message",
        "fname",
        "lname",
        "emailid",
        "dob",
        "city",
        "gender",
        "status",
        "barredtype",
        "preflanguage",
        "trid"
})

@JsonRootName("command")
public class CommandUserQueryInfoResponse implements IResponse {

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("PREFLANGUAGE")
    private String preflanguage;

    @JsonProperty("BARREDTYPE")
    private String barredtype;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("CITY")
    private String city;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("EMAILID")
    private String emailid;

    @JsonProperty("LNAME")
    private String lname;

    @JsonProperty("FNAME")
    private String fname;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("TYPE")
    private String type;

}



