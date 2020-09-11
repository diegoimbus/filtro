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
public class Origin extends IModel {

    @JsonProperty("MerchName")
    private String merchName;
    @JsonProperty("StoreInfo")
    private StoreInfo storeInfo;
    @JsonProperty("MerchRefNum")
    private String merchRefNum;
    @JsonProperty("LocaleInfo")
    private LocaleInfo localeInfo;
    @JsonProperty("POSInfo")
    private POSInfo posInfo;
    @JsonProperty("SourceInfo")
    private String sourceInfo;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

