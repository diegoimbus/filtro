package co.moviired.acquisition.common.model.exceptions;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ComponentThrowable extends ServiceException {

    private final String componentOfError;

    public ComponentThrowable(ErrorType type, String message, String code, String componentOfErrorI) {
        super(type, code, message);
        this.componentOfError = componentOfErrorI;
    }

    public final String getComponentOfError() {
        return componentOfError;
    }
}
