package co.moviired.transaction.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RequestManager implements Serializable {

    private static final long serialVersionUID = 231930029793865102L;

    private String userId;
    private String user;
    private String mpin;
    private String posId;
    private String merchantId;
    private String correlationId;
    private String transactionId;
    private String serviceSubType;
    private String getTransactionOrigin;
    private Integer numTransactions;
    private String createdBy;
    private String pageNumber;
    private String pageSize;
    private String startDate;
    private String endDate;

}

