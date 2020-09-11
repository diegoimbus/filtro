package co.moviired.register.domain.model.clevertap;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClevertapEventData extends BaseModel {

    private String origin;
    private String version;
    private Integer adoTransactionId;
    private Integer serviceRegistrationId;
    private String status;
    private Integer adoStatusId;
    private String adoStatusName;

}

