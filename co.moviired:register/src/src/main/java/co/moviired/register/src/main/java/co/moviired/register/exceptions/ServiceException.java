package co.moviired.register.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends Exception {

    private final String code;
    private final String componentOfError;

    public ServiceException(String message, String pCode, String pComponentOfError) {
        super(message);
        this.code = pCode;
        this.componentOfError = pComponentOfError;
    }
}
