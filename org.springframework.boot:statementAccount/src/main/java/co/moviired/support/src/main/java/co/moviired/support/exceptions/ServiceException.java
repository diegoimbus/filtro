package co.moviired.support.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends Exception {

    private final String code;
    private final String componentOfError;

    public ServiceException(String pmessage,
                            String pcode,
                            String pcomponentOfError,
                            Throwable pthrowable) {
        super(pmessage, pthrowable);
        this.code = pcode;
        this.componentOfError = pcomponentOfError;
    }
}

