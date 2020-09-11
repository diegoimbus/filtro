package co.moviired.digitalcontent.incomm.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-22
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@lombok.Data
public class Data implements Serializable {

    private static final long serialVersionUID = 6200933249410971016L;
    private Date date;
    private Long transactionTime;
    private String code;
    private String message;
    private String transactionUid;
    private String authorizationCode;
    private String pin;

}

