package co.moviired.cardManager.domain.dto.response;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.cardManager.domain.entity.ReclaimCard;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //ERROR
    private String errorType;
    private String errorCode;
    private String errorMessage;

    //Consulta por ID y IDType

    private Boolean cardDelivered;

    //Lista que contiene los valores de reclaimcard y variables de las paginas
    List<ReclaimCard> pageReclaimCard;
    private Integer pages;
    private Long numRegistry;

    //Lista que contiene los valores de reporte
    List<ReclaimCard> reponseReport;

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

