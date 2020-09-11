package co.moviired.business.provider.mahindra.request;

import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "msisdn",
        "mpin",
        "pin",
        "provider",
        "bprovider",
        "paymentInstrument",
        "payId",
        "billccode",
        "billano",
        "billno",
        "amount",
        "language1",
        "refNo",
        "bname",
        "billerCode",
        "remarks",
        "shortReferenceNumber"
})
@JsonRootName("command")
public class CommandPayBillBatchRequest implements IRequest {

    private String type;
    private String msisdn;
    private String mpin;
    private String pin;
    private String provider;
    private String bprovider;
    @JsonProperty("payment_Instrument")
    private String paymentInstrument;
    private String payId;
    private String billccode;
    private String billano;
    private String billno;
    private String amount;
    private String language1;
    private String refNo;
    private String bname;
    private String billerCode;
    private String remarks;
    private String shortReferenceNumber;

}

