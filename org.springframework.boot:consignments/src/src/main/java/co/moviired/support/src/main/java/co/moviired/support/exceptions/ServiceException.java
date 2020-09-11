package co.moviired.support.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends Exception {

    private final String code;
    private final String componentOfError;

    public ServiceException(String message, String code, String componentOfError) {
        super(message);
        this.code = code;
        this.componentOfError = componentOfError;
    }
}
