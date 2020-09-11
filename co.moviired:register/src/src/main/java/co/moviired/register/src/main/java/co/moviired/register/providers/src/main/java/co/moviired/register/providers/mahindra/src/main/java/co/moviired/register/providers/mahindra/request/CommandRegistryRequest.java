package co.moviired.register.providers.mahindra.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.register.providers.IRequest;
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
        "provider",
        "payid",
        "msisdn",
        "npref",
        "fname",
        "lname",
        "idtype",
        "idnumber",
        "email",
        "dob",
        "gender",
        "loginid",
        "language1",
        "doc1name",
        "doc2name",
        "doc3name",
        "prooftypeid1",
        "prooftypeid2",
        "prooftypeid3",
        "ispincheckreq",
        "imei",
        "source"
})


@JsonRootName("COMMAND")
public class CommandRegistryRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    @JsonProperty(value = "TYPE")
    private String type;

    @JsonProperty(value = "PROVIDER")
    private String provider;

    @JsonProperty(value = "PAYID")
    private String payid;

    @JsonProperty(value = "MSISDN")
    private String msisdn;

    @JsonProperty(value = "NPREF")
    private String npref;

    @JsonProperty(value = "FNAME")
    private String fname;

    @JsonProperty(value = "LNAME")
    private String lname;

    @JsonProperty(value = "IDTYPE")
    private String idtype;

    @JsonProperty(value = "IDNUMBER")
    private String idnumber;

    @JsonProperty(value = "EMAIL")
    private String email;

    @JsonProperty(value = "DOB")
    private String dob;

    @JsonProperty(value = "GENDER")
    private String gender;

    @JsonProperty(value = "ADDRESS")
    private String address;

    @JsonProperty(value = "DISTRICT")
    private String district;

    @JsonProperty(value = "CITY")
    private String city;

    @JsonProperty(value = "LOGINID")
    private String loginid;

    @JsonProperty(value = "LANGUAGE1")
    private String language1;

    @JsonProperty(value = "DOC1NAME")
    private String doc1name;

    @JsonProperty(value = "DOC2NAME")
    private String doc2name;

    @JsonProperty(value = "DOC3NAME")
    private String doc3name;

    @JsonProperty(value = "PROOFTYPEID1")
    private String prooftypeid1;

    @JsonProperty(value = "PROOFTYPEID2")
    private String prooftypeid2;

    @JsonProperty(value = "PROOFTYPEID3")
    private String prooftypeid3;

    @JsonProperty(value = "ISPINCHECKREQ")
    private String ispincheckreq;

    @JsonProperty(value = "IMEI")
    private String imei;

    @JsonProperty(value = "SOURCE")
    private String source;

}


