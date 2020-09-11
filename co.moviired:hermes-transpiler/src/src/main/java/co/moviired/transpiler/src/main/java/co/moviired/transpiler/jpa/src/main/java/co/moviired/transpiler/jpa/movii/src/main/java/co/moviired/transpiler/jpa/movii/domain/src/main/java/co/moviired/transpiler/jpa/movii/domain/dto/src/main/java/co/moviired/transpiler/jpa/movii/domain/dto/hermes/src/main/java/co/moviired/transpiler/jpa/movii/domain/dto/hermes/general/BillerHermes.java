package co.moviired.transpiler.jpa.movii.domain.dto.hermes.general;

import co.moviired.transpiler.jpa.movii.domain.BillerCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BillerHermes implements Serializable {

    private static final long serialVersionUID = 8841451016144286981L;

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    private BillerCategory category;

    private String billerCode;

    private String eanBillerCode;

    private String productCode;

    private String productDescription;

}

