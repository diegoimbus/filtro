package co.moviired.acquisition.model;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentUser extends IModel {

    private String name;
    private String user;
    private String encryptedPass;
    private List<String> methodsAllowed;

    @Override
    public final String protectedToString() {
        return toJson("encryptedPass", "user");
    }
}

