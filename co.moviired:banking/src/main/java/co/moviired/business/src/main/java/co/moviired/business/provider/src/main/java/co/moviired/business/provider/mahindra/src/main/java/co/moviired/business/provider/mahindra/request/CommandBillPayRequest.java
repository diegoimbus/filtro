package co.moviired.business.provider.mahindra.request;

import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "subtype",
        "msisdn1",
        "msisdn2",
        "mpin",
        "pin",
        "ean13billercode",
        "billreferencenumber",
        "billercode",
        "shortreferencenumber",
        "amount",
        "paymentInstrument",
        "bname",
        "echodata",
        "payid",
        "provider",
        "bprovider",
        "language1",
        "imei",
        "source",
        "ftxn_id",
        "cellid",
        "remarks",
        "requestorid",
        "ispincheckreq"
})
@JsonRootName("command")
public class CommandBillPayRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;
    private String subtype;
    private String msisdn1;
    private String msisdn2;
    private String mpin;
    private String pin;
    private String ean13billercode;
    private String billreferencenumber;
    private String billercode;
    private String shortreferencenumber;
    private String amount;
    @JsonProperty("payment_instrument")
    private String paymentInstrument;
    private String bname;
    private String echodata;
    private String payid;
    private String provider;
    private String bprovider;
    private String language1;
    private String imei;
    private String source;
    @JsonProperty("ftxn_id")
    private String ftxnId;
    private String cellid;
    private String remarks;
    private String requestorId;
    private String ispincheckreq;

}

