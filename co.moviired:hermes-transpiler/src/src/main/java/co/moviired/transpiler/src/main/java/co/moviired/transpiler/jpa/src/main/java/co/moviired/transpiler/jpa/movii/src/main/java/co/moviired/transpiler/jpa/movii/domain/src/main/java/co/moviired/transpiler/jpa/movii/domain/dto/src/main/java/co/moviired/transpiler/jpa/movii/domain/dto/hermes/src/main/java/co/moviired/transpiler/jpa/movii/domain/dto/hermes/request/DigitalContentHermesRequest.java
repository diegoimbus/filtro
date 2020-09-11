package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ProductHermes;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
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
public class DigitalContentHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    private String cardSerialNumber;

    private String operation;

    private String customerId;

    private String usename;

    private String email;

    private Integer amount;

    private String eanCode;

    private String productId;

    private String ip;

    private String source;

    private String phoneNumber;

    private String issuerName;

    private String issueDate;

    private String correlationId;

    @NotNull
    private String merchantId;

    @NotNull
    private String deviceId;

    @NotNull
    private ClientHermes client;

    @NotNull
    private ProductHermes product;

    @NotBlank
    private String originalRequest;
    @NotBlank
    private String correlationIdR;

    private String logId;

    private String requestDate;


    @Override
    public Protocol getProtocol() {
        return null;
    }

    @Override
    public void setProtocol(Protocol protocol) {
        // No se implementa
    }

    @Override
    public String getClientTxnId() {
        return null;
    }
}

