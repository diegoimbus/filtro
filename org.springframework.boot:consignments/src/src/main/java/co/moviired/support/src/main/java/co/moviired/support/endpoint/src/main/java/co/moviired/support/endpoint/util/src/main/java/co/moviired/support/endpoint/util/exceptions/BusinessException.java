package co.moviired.support.endpoint.util.exceptions;

import co.moviired.support.endpoint.bancobogota.dto.generics.ErrorDTO;

public class BusinessException extends GenericException {
    private static final long serialVersionUID = 1L;

    public BusinessException() {
        super();
    }

    public BusinessException(String code, String description, String... args) {
        super(code, description, args);
    }

    public BusinessException(CodeErrorEnum code, Throwable cause) {
        super(code, cause);
    }

    public BusinessException(CodeErrorEnum code) {
        super(code);
    }

    public BusinessException(String description) {
        super(CodeErrorEnum.BOERROR.getCode(), description);
    }

    public BusinessException(Throwable cause) {
        super(CodeErrorEnum.BOERROR.getCode(), cause);
    }

    public BusinessException(Throwable cause, ErrorDTO errorDTO) {
        super(errorDTO.getCode(), errorDTO.getDescription(), cause);
    }

    public BusinessException(CodeErrorEnum code, String... arguments) {
        super(code, arguments);
    }

    public BusinessException(CodeErrorEnum code, String arguments) {
        super(code, arguments);
    }

    public BusinessException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public BusinessException(Throwable exception, CodeErrorEnum code, String... args) {
        super(exception, code, args);
    }
}

