package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ProductHermes;
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
public class TopUpHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    @JsonIgnore
    private String logId;

    @NotBlank
    private String originalRequest;

    @NotBlank
    private String clientTxnId;

    @NotNull
    private Protocol protocol;

    @NotNull
    private ClientHermes client;

    @NotNull
    private ProductHermes product;

    @NotNull
    private String date;

    @NotBlank
    private String rechargeNumber;

    @NotNull
    private Integer amount;

    @NotNull
    private String merchantId;

    @NotNull
    private String deviceId;

    @NotNull
    private String requestDate;

    @NotNull
    private String cashierId;

}

