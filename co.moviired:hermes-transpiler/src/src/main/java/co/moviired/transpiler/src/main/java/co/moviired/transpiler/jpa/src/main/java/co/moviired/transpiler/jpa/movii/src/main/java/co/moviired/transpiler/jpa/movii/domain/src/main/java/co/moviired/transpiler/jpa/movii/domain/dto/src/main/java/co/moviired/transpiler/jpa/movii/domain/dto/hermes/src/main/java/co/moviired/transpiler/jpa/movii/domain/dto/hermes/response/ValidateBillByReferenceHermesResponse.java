package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByReferenceHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidateBillByReferenceHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = 7369151665987177382L;

    @NotNull
    private ValidateBillByReferenceHermesRequest request;

    @NotNull
    private ResponseHermes response;

    // DATA
    private String billDueDate;
    private String transactionCode;
    private String eanCode;
    private String shortReferenceNumber;
    private String billerCode;
    private String date;
    private String hashEchoData;
    private String authorizationCode;
    private String minValueToPay;
    private String userId;
    private String posId;
    private String multiple;
    private String echoData;
    private String minPaymentValue;
    private String responseCode;
    private String authExternalCode;
    private String productCode;
    private String maxPaymentValue;
    private String bankId;
    private String valueToPay;
    private String partialPayment;
    private String transactionType;
    private String processCode;
    private String device;
    private String labelRef;

    @Override
    public void setRequest(IHermesRequest request) {
        this.request = (ValidateBillByReferenceHermesRequest) request;
    }

}

