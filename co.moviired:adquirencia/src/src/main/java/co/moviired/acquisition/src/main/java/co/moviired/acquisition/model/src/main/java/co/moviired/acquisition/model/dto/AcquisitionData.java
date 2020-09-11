package co.moviired.acquisition.model.dto;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcquisitionData extends IModel {

    @JsonProperty("MsgType")
    private String msgType;
    @JsonProperty("DateTimeInfo")
    private DateTimeInfo dateTimeInfo;

    @JsonProperty("RespCode")
    private String respCode;
    @JsonProperty("RespMsg")
    private String respMsg;

    @JsonProperty("ServiceProviderRefNum")
    private String serviceProviderRefNum;

    @JsonProperty("IncommRefNum")
    private String incommRefNum;
    @JsonProperty("Origin")
    private Origin origin;
    @JsonProperty("Product")
    private Product product;

    @JsonProperty("OriginalRequest")
    private AcquisitionData originalRequest;
    @JsonProperty("Extension")
    private List<Extension> extension;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

