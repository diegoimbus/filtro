package co.moviired.transpiler.jpa.movii.domain.dto.hermes.general;

import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductHermes implements Serializable {

    private static final long serialVersionUID = 8841451016144286981L;

    private String id;

    private String productCode;

    private String eanCode;

    private String name;

    private ProductType type;

}

