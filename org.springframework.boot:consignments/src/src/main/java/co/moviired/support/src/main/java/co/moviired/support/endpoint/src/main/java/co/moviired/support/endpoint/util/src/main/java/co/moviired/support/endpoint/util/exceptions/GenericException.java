package co.moviired.support.endpoint.util.exceptions;

import co.moviired.support.endpoint.bancobogota.dto.generics.ErrorDTO;
import co.moviired.support.endpoint.util.generics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Component
public class GenericException extends Exception {
    private static final long serialVersionUID = 1L;
    private ErrorDTO errorDTO = new ErrorDTO();

    public GenericException() {
        super();
    }

    public GenericException(String code, String description, Throwable cause) {
        String message;
        if(cause.getMessage() == null || cause.getMessage().equals("")){
            message = description;
        }else{
            message = description + ", " + cause.getMessage();
        }

        errorDTO = new ErrorDTO(code, message);
        this.printLog();
    }

    public GenericException(String code, String description, String... args) {
        String message = description;
        if (Validation.isNotNull(args)) {
            message = args[0];
        }

        errorDTO = new ErrorDTO(code, message);
        this.printLog();
    }

    public GenericException(String code, Throwable cause) {
        errorDTO = new ErrorDTO(code, cause.getMessage());
        this.printLog();
    }

    public GenericException(CodeErrorEnum mensaje, Throwable cause) {
        String message = cause.getMessage();
        errorDTO = new ErrorDTO(mensaje.getCode(), message);
        this.printLog();
    }

    public GenericException(CodeErrorEnum mensaje) {
        String message = null;
        errorDTO = new ErrorDTO(mensaje.getCode(), message);
        this.printLog();
    }

    public GenericException(Throwable exception, CodeErrorEnum code, String[] args) {
        String message = code.getDescription();
        if (Validation.isNotNull(args)) {
            message = args[0];
        }

        errorDTO = new ErrorDTO(code.getCode(), message);
        this.printLog();
        this.printTrace(exception);
    }

    public GenericException(CodeErrorEnum code, String[] args) {
        String message = args[0];

        errorDTO = new ErrorDTO(code.getCode(), message);
        this.printLog();
    }

    public GenericException(CodeErrorEnum code, String message) {

        if (Validation.isNotNull(message)) {
            errorDTO = new ErrorDTO(code.getCode(), message);
        } else {
            String messageCode = code.getDescription();
            errorDTO = new ErrorDTO(code.getCode(), messageCode);
        }

        this.printLog();
    }

    public ErrorDTO getErrorDTO() {
        return errorDTO;
    }

    public GenericException setErrorDTO(ErrorDTO errorDTOx) {
        errorDTO = errorDTOx;
        return this;
    }

    public GenericException setErrorDTO(String code, String description) {
        errorDTO = new ErrorDTO(code, description);
        return this;
    }

    private void printLog() {
        StringBuilder string = new StringBuilder();
        string.append("Cod. Error: ").append(errorDTO.getCode()).append(" - Mensaje: ").append(errorDTO.getDescription());
        log.error(string.toString());
    }

    private void printTrace(Throwable cause) {
        try {
            StringWriter result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            cause.printStackTrace(printWriter);
            log.error(String.valueOf(result));
            result.close();
            printWriter.close();
        } catch (Exception var4) {
            log.error(String.valueOf((new StringBuilder("ocurrio un error al intentar")).append("imprimir la traza de error ").append(":").append(var4.getMessage())));
        }

    }

}

