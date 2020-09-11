package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CashOutHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 3115875713104136536L;

    @JsonIgnore
    private String logId;

    @NotBlank
    private String originalRequest;

    @NotBlank
    private String clientTxnId;

    @NotNull
    private Protocol protocol;

    private String requestDate;

}

