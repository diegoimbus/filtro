package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidateBillByEanHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    @NotBlank
    private String originalRequest;

    @JsonIgnore
    private String logId;

    private String clientTxnId;

    @NotNull
    private Protocol protocol;

    @NotNull
    private BillerHermes biller;

    @NotNull
    private ClientHermes client;

    // META
    private String posId;

    private String requestDate;

    private String customerId;

    private String deviceCode;

    private String requestReference;

    private String channel;

    private String systemId;

    private String originAddress;

    private String requestSource;

    // DATA

    @JsonProperty("EAN128FullCode")
    private String ean128FullCode;

    // SIGNATURE
    private String systemSignature;

}

