package co.moviired.topups.model.domain.dto.recharge.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "name",
        "status",
        "product_image",
        "products"
})
public class OperatorResponse implements Serializable {
    private String id;

    private String name;

    private String status;

    private String product_image;

    private List<ProductResponse> products;
}

