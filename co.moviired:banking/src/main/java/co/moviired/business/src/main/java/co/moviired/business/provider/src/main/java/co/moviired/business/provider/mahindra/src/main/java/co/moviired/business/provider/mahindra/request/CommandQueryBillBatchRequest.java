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
        "provider",
        "mpin",
        "pin",
        "billccode",
        "billano",
        "billno",
        "blocksms",
        "language1",
        "cellid",
        "ftxnid",
        "paymentInstrument",
        "echodata",
        "imei",
        "source",
        "bname"
})
@JsonRootName("command")
public class CommandQueryBillBatchRequest implements IRequest {

    private String type;
    private String msisdn;
    private String provider;
    private String mpin;
    private String pin;
    private String billccode;
    private String billano;
    private String billno;
    private String blocksms;
    private String language1;
    private String cellid;
    private String ftxnid;
    @JsonProperty("payment_Instrument")
    private String paymentInstrument;
    private String echodata;
    private String imei;
    private String source;

}

