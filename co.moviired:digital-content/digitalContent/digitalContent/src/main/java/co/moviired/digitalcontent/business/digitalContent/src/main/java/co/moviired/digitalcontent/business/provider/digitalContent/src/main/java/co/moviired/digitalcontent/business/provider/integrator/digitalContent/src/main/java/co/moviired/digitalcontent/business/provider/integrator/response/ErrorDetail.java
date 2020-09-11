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
public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = -1325330320122421891L;
    private String errorMessage;
    private Integer errorType;
    private String errorCode;
}

