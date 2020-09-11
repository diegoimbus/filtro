package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.BillPayHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BillPayHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = 7369151665987177382L;

    @NotNull
    private BillPayHermesRequest request;

    @NotNull
    private ResponseHermes response;

    // Data

    private String device;

    private String commisionvalue;

    private String chargevalue;

    private String valuetopay;

    private String shortreferencenumber;

    private String billercode;

    private String banktransactionid;

    private String bankid;

    private String transactionid;

    private String newbalance;

    private String commission;

    private String txnid;

    private String txnstatus;

    private String message;

    private String trid;

    @Override
    public void setRequest(IHermesRequest request) {
        this.request = (BillPayHermesRequest) request;
    }
}

