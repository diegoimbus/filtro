package co.moviired.register.providers.reegistraduria;

import co.moviired.register.providers.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StatusDTO implements IResponse {

    private String code;
    private String message;

}

