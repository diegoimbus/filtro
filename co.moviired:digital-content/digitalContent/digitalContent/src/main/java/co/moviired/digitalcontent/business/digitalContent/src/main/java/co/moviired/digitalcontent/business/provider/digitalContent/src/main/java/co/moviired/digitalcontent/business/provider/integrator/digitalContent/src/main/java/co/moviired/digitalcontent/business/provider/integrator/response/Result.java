package co.moviired.digitalcontent.business.provider.integrator.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-01-28
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class Result implements Serializable {

    private static final long serialVersionUID = 1165947405987692008L;
    private String message;
    private Integer statusCode;
    private ErrorDetail error;
}

