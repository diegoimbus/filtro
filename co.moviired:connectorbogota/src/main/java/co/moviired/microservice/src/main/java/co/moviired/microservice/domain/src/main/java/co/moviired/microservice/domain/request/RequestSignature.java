package co.moviired.microservice.domain.request;

/*
 * Copyright @2020. Movii, SAS. Todos los derechos reservados.
 *
 * @author Oscar Lopez
 * @since 1.1.1
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RequestSignature {

    private String systemSignature;

}


