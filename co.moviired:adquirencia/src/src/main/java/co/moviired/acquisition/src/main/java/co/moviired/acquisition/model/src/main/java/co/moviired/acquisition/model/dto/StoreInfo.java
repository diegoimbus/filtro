package co.moviired.acquisition.model.dto;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreInfo extends IModel {

    @JsonProperty("StoreID")
    private String storeID;
    @JsonProperty("TermID")
    private String termID;
    @JsonProperty("StoreLoc")
    private StoreLoc storeLoc;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

