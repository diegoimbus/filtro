package co.moviired.supportp2pvalidatortransaction.common.model.dto;

import co.moviired.supportp2pvalidatortransaction.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static co.moviired.supportp2pvalidatortransaction.common.util.StatusCodes.SUCCESS_CODE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IComponentDTO extends IModel {

    private Boolean executeScheduler;
    private String correlative;
    private ResponseStatus status;

    @Override
    public String protectedToString() {
        return this.toJson();
    }

    @JsonIgnore
    public boolean isSuccessResponse() {
        return status != null && SUCCESS_CODE.equals(status.getCode());
    }
}

