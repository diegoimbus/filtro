package co.moviired.acquisition.common.model.method;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Service extends IMethod {

    private String path;
}

