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
public class ServiceProviderTxn extends IModel {

    @JsonProperty("Version")
    private String version;

    @JsonProperty("Request")
    private AcquisitionData request;
    @JsonProperty("Response")
    private AcquisitionData response;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

