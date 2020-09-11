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
public class ClientHermes implements Serializable {

    private static final long serialVersionUID = 8854316281038005207L;

    @NotBlank
    private String clientName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String timeZone;

}

