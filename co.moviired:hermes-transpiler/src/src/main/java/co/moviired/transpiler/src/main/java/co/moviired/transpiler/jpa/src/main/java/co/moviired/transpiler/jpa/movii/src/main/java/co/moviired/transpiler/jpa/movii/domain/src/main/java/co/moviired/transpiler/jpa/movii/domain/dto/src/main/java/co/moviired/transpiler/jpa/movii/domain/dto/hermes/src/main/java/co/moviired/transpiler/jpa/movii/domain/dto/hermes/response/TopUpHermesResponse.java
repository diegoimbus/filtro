package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.TopUpHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TopUpHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = -7048747822681332726L;

    @NotNull
    private TopUpHermesRequest request;

    @NotNull
    private ResponseHermes response;

    @NotBlank
    private String authorizationNumber;

    @NotNull
    private String newBalance;

    @NotBlank
    private String txnId;

    private String transactionCode;

    private String transactionDate;

    private String customerDate;

    private String subProductCode;

    @Override
    public void setRequest(IHermesRequest request) {
        this.request = (TopUpHermesRequest) request;
    }

}

