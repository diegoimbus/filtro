package co.moviired.cardManager.domain.dto.response;

import co.moviired.base.domain.enumeration.ErrorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //ERROR
    private String errorType;
    private String errorCode;
    private String errorMessage;

    //Consulta por ID y IDType

    private Boolean cardDelivered;

    // CONSTRUCTORES
    public Response(String code, String message, ErrorType data) {
        super();
    }

    public Response(String codigo, String mensaje, String error) {
        super();
        this.errorCode = codigo;
        this.errorMessage = mensaje;
        this.errorType = error;
        //this.correlationId = correlationId;
    }

}

