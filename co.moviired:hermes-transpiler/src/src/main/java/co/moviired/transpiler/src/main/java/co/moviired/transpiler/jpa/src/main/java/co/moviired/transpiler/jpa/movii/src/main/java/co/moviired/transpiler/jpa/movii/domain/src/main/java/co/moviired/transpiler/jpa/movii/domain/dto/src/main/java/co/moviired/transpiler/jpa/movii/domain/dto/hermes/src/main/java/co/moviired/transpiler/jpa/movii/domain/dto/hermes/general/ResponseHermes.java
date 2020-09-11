package co.moviired.transpiler.jpa.movii.domain.dto.hermes.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseHermes implements Serializable {

    private static final long serialVersionUID = 1819628723274870892L;

    @NotBlank
    private String statusCode;

    @NotBlank
    private String statusMessage;

    @NotBlank
    private String errorCode;

    @NotBlank
    private String errorMessage;

}

