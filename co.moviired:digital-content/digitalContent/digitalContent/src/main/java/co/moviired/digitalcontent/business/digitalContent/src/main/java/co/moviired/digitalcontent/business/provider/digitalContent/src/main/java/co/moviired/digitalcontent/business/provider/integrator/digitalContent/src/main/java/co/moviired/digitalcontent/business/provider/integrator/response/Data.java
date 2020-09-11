package co.moviired.digitalcontent.business.provider.integrator.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * Esta clase debera ser extendida en cada Microservicio para definir la salida especifica de cada uno de estos
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-01-28
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@lombok.Data
public class Data implements Serializable {
    private static final long serialVersionUID = -3215918910131497980L;

    private String amount;
    private String transactionCode;
    private String customerDate;
    private String subProductCode;
    private String authorizationCode;
    private String transactionDate;
    private String productId;

}


