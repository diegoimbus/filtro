package co.moviired.moneytransfer.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("COMMAND")
public class Response implements Serializable {

    private static final long serialVersionUID = 4512597599852333050L;

    @JsonProperty("TYPE")
    private String type;

    // Codigo Estado de la Transaccion
    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    //Tango BlackListResponse ID
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

    @JsonProperty("TXNCOUNT")
    private String txnCount;

    @JsonProperty("AMOUNT")
    private Double amount;

    @Builder.Default
    @JsonProperty("TXNLIST")
    private transient List<Object> txn = new ArrayList<>();

}

