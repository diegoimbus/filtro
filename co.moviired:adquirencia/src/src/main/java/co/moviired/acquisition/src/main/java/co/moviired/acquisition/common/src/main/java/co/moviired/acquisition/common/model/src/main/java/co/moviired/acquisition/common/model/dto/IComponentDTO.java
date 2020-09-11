package co.moviired.acquisition.common.model.dto;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static co.moviired.acquisition.common.util.StatusCodesHelper.SUCCESS_CODE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IComponentDTO extends IModel {

    private Boolean executeScheduler;
    private ResponseStatus status;

    private String source;
    private String ip;
    private String channel;
    private String version;
    private String operatingSystem;
    private String browser;

    /**
     * This method return string to show in logs with sensible fields protected
     *
     * @return protected to String of object in json format
     */
    @Override
    public String protectedToString() {
        return this.toJson();
    }

    @JsonIgnore
    public final boolean isSuccessResponse() {
        return status != null && SUCCESS_CODE.equals(status.getCode());
    }
}

