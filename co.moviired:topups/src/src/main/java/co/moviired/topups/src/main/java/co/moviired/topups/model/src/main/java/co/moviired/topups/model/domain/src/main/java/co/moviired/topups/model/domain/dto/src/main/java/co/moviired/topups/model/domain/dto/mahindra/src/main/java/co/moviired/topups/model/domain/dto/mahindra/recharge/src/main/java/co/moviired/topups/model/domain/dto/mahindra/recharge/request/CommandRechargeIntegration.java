package co.moviired.topups.model.domain.dto.mahindra.recharge.request;

import co.moviired.topups.model.domain.dto.mahindra.ICommandRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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
        "productid",
        "eancode",
        "mpin",
        "pin",
        "imei",
        "echodata",
        "remarks",
        "source",
        "ftxnid",
        "cellid"
})
@JsonRootName("command")
public class CommandRechargeIntegration implements ICommandRequest {
    private static final long serialVersionUID = 5241732459822225389L;

    private String type;
    private String payid2;
    private String paymenttype;
    private String provider2;
    private String msisdn2;
    private String mpin;
    private String pin;
    private String amount;
    private String msisdn;
    private String echodata;
    private String payid;
    private String provider;
    private String operatorid;
    private String operatorname;
    private String language1;
    private String productid;
    private String eancode;
    private String imei;
    private String remarks;
    private String source;
    private String ftxnid;
    private String cellid;
    private String name;

    @Override
    public final CommandRechargeIntegration clone() throws CloneNotSupportedException {
        return (CommandRechargeIntegration) super.clone();
    }

    @Override
    public String toString() {
        return String.format(
                "<COMMAND><TYPE>%s</TYPE><PAYID2>%s</PAYID2><PAYMENTTYPE>%s</PAYMENTTYPE><PAYID>%s</PAYID><PROVIDER2>%s</PROVIDER2><AMOUNT>%s</AMOUNT><MSISDN>%s</MSISDN><MSISDN2>%s</MSISDN2><PROVIDER>%s</PROVIDER><OPERATORID>%s</OPERATORID><OPERATORNAME>%s</OPERATORNAME><LANGUAGE1>%s</LANGUAGE1><PRODUCTID>%s</PRODUCTID><EANCODE>%s</EANCODE><MPIN>****</MPIN><PIN>****</PIN><IMEI>%s</IMEI><ECHODATA>%s</ECHODATA><REMARKS>%s</REMARKS><SOURCE>%s</SOURCE><FTXNID>%s</FTXNID><CELLID>%s</CELLID></COMMAND>",
                type, payid2, paymenttype, payid, provider2, amount, msisdn, msisdn2, provider,
                operatorid, operatorname, language1, productid, eancode, imei, echodata, remarks, source, ftxnid, cellid);
    }
}

