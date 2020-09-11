package co.moviired.digitalcontent.business.provider.mahindra.request;

import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName("COMMAND")
public class Command implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("SUBTYPE")
    private String subtype;

    @JsonProperty("MSISDN1")
    private String msisdn1;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("PHONENUMBER")
    private String phonenumber;

    @JsonProperty("MPIN")
    private String mpin;

    @JsonProperty("PRODUCTID")
    private String productid;

    @JsonProperty("EANCODE")
    private String eancode;

    @JsonProperty("CUSTOMERDATE")
    private String customerdate;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("PAYID")
    private String payid;

    @JsonProperty("PAYMENT_INSTRUMENT")
    private String paymentInstrument;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("BPROVIDER")
    private String bprovider;

    @JsonProperty("BLOCKSMS")
    private String blocksms;

    @JsonProperty("LANGUAGE")
    private String language;

    @JsonProperty("LANGUAGE1")
    private String language1;

    @JsonProperty("IMEI")
    private String imei;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("ISPINCHECKREQ")
    private String ispincheckreq;

    @JsonProperty("SOURCE")
    private String source;

    @JsonProperty("OTPREQ")
    private String otpreq;

    @JsonProperty("FTXNID")
    private String ftxnid;

    @JsonProperty("TXNID_ORG")
    private String txnidorg;

    @JsonProperty("FTXNID_ORG")
    private String ftxnidorg;

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("IS_TCP_CHECK_REQ")
    private String istcpcheckreq;

    public final IRequest parseRequestReverseTransactionMahindra(
            @NotNull DigitalContentRequest data,
            @NotNull MahindraProperties mahindraProperties
    ) {
        Command command = new Command();

        // Datos especificos de la transaccion
        command.setFtxnid(data.getCorrelationId());
        command.setTxnidorg(data.getCorrelationIdR());
        command.setAmount(data.getAmount());
        command.setRemarks(data.getSource());

        // Datos prestablecidos
        command.setType(mahindraProperties.getReversionType());
        command.setIspincheckreq(mahindraProperties.getReversionIsTcpCheckReq());

        return command;
    }
}
