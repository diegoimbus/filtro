package co.moviired.register.domain.model.register;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStatus extends BaseModel {

    private String code;
    private String message;

}

