package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
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
        "remarks"
})
@JsonRootName("command")
public class CommandBillPay implements ICommandRequest {

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

    private String remarks;

    private String cellId;

}

