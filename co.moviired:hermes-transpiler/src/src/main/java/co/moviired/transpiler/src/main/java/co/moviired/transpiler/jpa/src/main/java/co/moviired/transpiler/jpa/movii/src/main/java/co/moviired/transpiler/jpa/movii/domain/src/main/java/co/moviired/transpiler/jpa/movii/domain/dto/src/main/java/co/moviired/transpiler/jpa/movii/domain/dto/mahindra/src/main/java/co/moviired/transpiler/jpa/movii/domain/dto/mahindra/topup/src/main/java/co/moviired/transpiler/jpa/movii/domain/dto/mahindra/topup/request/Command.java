package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
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
        "payid2",
        "paymenttype",
        "payid",
        "provider2",
        "amount",
        "msisdn",
        "msisdn2",
        "provider",
        "operatorid",
        "operatorname",
        "language1",
        "language2",
        "productid",
        "eancode",
        "mpin",
        "pin",
        "ispincheckreq",
        "imei",
        "txnid",
        "blocksms",
        "service",
        "txnmode",
        "nooftxnreq",
        "cellid",
        "ftxnid",
        "action",
        "remarks"
})
@JsonRootName("command")
public class Command implements ICommandRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String payid2;

    private String paymenttype;

    private String payid;

    private String provider2;

    private String amount;

    private String msisdn;

    private String msisdn2;

    private String provider;

    private String operatorid;

    private String operatorname;

    private String language1;

    private String language2;

    private String productid;

    private String eancode;

    private String mpin;

    private String pin;

    private String ispinchecker;

    private String imei;

    private String source;

    private String txnid;

    private String blocksms;

    private String service;

    private String txnmode;

    private String nooftxnreq;

    private String cellid;

    private String ftxnid;

    private String action;

    private String remarks;

}
