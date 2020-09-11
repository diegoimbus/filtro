package co.moviired.acquisition.common.model.dto;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStatus extends IModel {

    private String code;
    private String message;
    private String component;

    @Override
    public final String protectedToString() {
        return super.toJson();
    }
}

