package co.moviired.support.domain.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TXNSTATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EXTREFNUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TXNID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQSTATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@Data
@JsonRootName("COMMAND")
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class Response implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    // Codigo Estado de la Transaccion
    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    //Tango Request ID
    @JsonProperty("TRID")
    private String trid;

    // ID de Transacción
    @JsonProperty("TXNID")
    private String txnid;

    // Mensaje
    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("RESPONSE")
    private String respuesta;

    @JsonProperty("TRANSDETAILS")
    private String transdetails;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("BARREDTYPE")
    private String barredtype;

    @JsonProperty("FIRSTNAME")
    private String fname;

    @JsonProperty("LASTNAME")
    private String lname;

    @JsonProperty("STATUSVALUE")
    private String statusvalue;

    @JsonProperty("TRANSID")
    private String transid;

    @JsonProperty("TXNAMT")
    private String txnamt;

    @JsonProperty("USERID")
    private String userid;

    @JsonProperty("USERTYPE")
    private String userType;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("CITY")
    private String city;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("PREFLANGUAGE")
    private String preflanguage;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("IDNO")
    private String idno;

    @JsonProperty("IDTYPE")
    private String idtype;

}

