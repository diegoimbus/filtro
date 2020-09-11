package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.CashOutHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CashOutHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = 7369151665987177382L;

    @NotNull
    private CashOutHermesRequest request;

    @NotNull
    private ResponseHermes response;

    @Override
    public void setRequest(IHermesRequest request) {
        this.request = (CashOutHermesRequest) request;
    }
}

