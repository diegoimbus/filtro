package co.moviired.digitalcontent.business.domain.dto.response;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.digitalcontent.business.domain.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
public class DigitalContentResponse implements Serializable {

    private Date transactionDate;
    private Long transactionTime;
    private String transactionId;
    private String cashInId;
    private String correlationId;
    private String amount;
    private String errorType;
    private String errorCode;
    private String errorMessage;
    private String phoneNumber;
    private String name;
    private String agentCode;
    private String invoiceNumber;
    private String userName;
    private String authorizationCode;
    private String pin;
    private String termAndConditions;
    private List<Category> categories;
    private Category category;


    // CONSTRUCTORES
    public DigitalContentResponse() {
        super();
    }

    public DigitalContentResponse(String codigo, String mensaje, ErrorType perror) {
        super();
        this.errorCode = codigo;
        this.errorMessage = mensaje;
        this.errorType = perror.name();
    }
}

