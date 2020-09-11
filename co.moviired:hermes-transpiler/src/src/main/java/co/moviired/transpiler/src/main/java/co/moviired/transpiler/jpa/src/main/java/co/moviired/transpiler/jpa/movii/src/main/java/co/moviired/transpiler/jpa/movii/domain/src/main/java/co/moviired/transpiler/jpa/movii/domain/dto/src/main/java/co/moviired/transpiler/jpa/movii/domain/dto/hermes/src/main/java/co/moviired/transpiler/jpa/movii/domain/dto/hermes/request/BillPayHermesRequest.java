package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BillPayHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    @JsonIgnore
    private String logId;

    @NotBlank
    private String originalRequest;

    @NotNull
    private Protocol protocol;

    @NotNull
    private ClientHermes client;

    @NotNull
    private BillerHermes biller;

    @NotNull
    private Integer amount;

    @NotBlank
    private String deviceCode;

    @NotBlank
    private String echoData;

    @NotBlank
    private String customerId;

    private String shortReferenceNumber;

    private String billReferenceNumber;

    private String clientTxnId;

    private String requestDate;

}

