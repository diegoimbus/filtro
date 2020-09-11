package co.moviired.register.domain.model.clevertap;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClevertapEvent extends BaseModel {

    private String identity;
    private String evtName;
    private String type;
    private String ts;
    private String objectId;
    private ClevertapEventData evtData;

}

