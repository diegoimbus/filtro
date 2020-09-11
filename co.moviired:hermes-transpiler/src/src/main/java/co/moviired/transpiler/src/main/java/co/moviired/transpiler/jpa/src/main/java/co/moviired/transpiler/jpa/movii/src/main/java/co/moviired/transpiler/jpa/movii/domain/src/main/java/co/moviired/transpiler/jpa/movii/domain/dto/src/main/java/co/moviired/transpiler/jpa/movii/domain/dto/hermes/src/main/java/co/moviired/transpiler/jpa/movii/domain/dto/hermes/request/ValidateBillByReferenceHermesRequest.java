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
public class ValidateBillByReferenceHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    @JsonIgnore
    private String logId;

    @NotBlank
    private String originalRequest;

    @NotNull
    private Protocol protocol;

    private String clientTxnId;

    @NotNull
    private ClientHermes client;

    @NotNull
    private BillerHermes biller;

    // META
    private String requestDate;

    private String customerId;

    private String deviceCode;

    private String requestReference;

    private String channel;

    private String systemId;

    private String originAddress;

    private String imei;

    private String requestSource;

    private String posId;

    // DATA
    private String billerCode;

    private String shortReferenceNumber;

    private String valueToPay;

    // SIGNATURE
    private String systemSignature;

}

