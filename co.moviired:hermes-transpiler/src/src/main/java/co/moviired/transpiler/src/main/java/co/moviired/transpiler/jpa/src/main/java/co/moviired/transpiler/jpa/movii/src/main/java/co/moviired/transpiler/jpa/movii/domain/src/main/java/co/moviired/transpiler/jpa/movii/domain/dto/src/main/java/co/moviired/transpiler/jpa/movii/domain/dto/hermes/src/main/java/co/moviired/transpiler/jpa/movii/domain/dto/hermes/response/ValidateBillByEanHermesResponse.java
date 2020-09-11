package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByEanHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "billReferenceNumber",
        "billDueDate",
        "EAN13BillerCode",
        "minPartialPayment",
        "maxPaymentValue",
        "billerCode",
        "hashEchoData",
        "payAfterDueDate",
        "billerName",
        "bankId",
        "valueToPay",
        "partialPayment",
        "LabelRef",
        "helpOnline",
        "multiple",
        "echoData",
        "minPaymentValue"
})
public class ValidateBillByEanHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = 7369151665987177382L;

    @NotNull
    private ValidateBillByEanHermesRequest request;

    @NotNull
    private ResponseHermes response;

    // DATA
    private String billReferenceNumber;
    private String billDueDate;
    private String minPartialPayment;
    private String maxPaymentValue;
    private String billerCode;
    private String hashEchoData;
    private String payAfterDueDate;
    private String billerName;
    private String bankId;
    private String valueToPay;
    private String partialPayment;
    private String helpOnline;
    private String multiple;
    private String echoData;
    private String minPaymentValue;

    @JsonProperty("EAN13BillerCode")
    private String ean13BillerCode;

    @JsonProperty("LabelRef")
    private String labelRef;

    @Override
    public void setRequest(IHermesRequest request) {
        this.request = (ValidateBillByEanHermesRequest) request;
    }

}

