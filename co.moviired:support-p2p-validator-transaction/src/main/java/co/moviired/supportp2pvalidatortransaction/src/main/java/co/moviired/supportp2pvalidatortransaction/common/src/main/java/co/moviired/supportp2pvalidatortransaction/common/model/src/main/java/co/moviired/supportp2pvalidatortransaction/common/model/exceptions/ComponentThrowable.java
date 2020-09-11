package co.moviired.supportp2pvalidatortransaction.common.model.exceptions;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ComponentThrowable extends ServiceException {

    private final String componentOfError;

    public ComponentThrowable(ErrorType type, String message, String code, String componentOfError) {
        super(type, code, message);
        this.componentOfError = componentOfError;
    }

    public String getComponentOfError() {
        return componentOfError;
    }
}
