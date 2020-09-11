package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EchoHermesResponse implements IHermesResponse {

    private static final long serialVersionUID = -7048747822681332726L;

    @NotNull
    private IHermesRequest request;

    private ResponseHermes response;

    @NotNull
    private Protocol protocol;

    @NotBlank
    private String clientTxnId;

    @NotNull
    private Date date;

    @NotBlank
    private String nit;

    @NotBlank
    private Integer red;

}

