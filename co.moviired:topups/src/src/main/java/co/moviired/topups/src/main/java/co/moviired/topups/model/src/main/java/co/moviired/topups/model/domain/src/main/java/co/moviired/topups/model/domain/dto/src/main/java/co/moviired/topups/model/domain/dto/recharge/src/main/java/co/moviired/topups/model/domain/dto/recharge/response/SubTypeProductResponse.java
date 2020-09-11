package co.moviired.topups.model.domain.dto.recharge.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "eanCode",
        "name",
        "minValue",
        "maxValue",
        "multiple",
        "regExp",
        "status",
        "productCode",
        "detailsExpiration"
})

public class SubTypeProductResponse implements Serializable {

    private static final long serialVersionUID = -3213799558263573453L;

    private String id;

    private String eanCode;

    private String name;

    private String minValue;

    private String maxValue;

    private String multiple;

    private String regExp;

    private String status;

    private String productCode;

    private String detailsExpiration;

}

