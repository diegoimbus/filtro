package co.moviired.business.domain.dto.banking.response;

import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.provider.bankingswitch.response.Obligation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Response {

    //DEPOSTIO - CASHIN
    private String authorizationCode;
    private String referenceNumber; // Numero de referencia -- numero de la cuenta
    private Integer amount;
    private String billReferenceNumber; // Numero de aprobación Movii
    private String transferId;

    //QUERY
    private String balance;
    private String comission;
    private String upcId;
    private List<Obligation> obligations;
    private String correlationId;

    //ERROR
    private String errorType;
    private String errorCode;
    private String errorMessage;

    //VALIDATE_PAYBILL
    private String ean13BillerCode;
    private CollectionType typePayBill;
    private String minPartialPayment;
    private String maxPaymentValue;
    private String serviceCode; //billeCode
    private String billerName;
    private Boolean partialPayment;
    private String echoData;
    private String minPaymentValue;
    private String transactionDate;
    private String shortReferenceNumber;
    private String transactionId;
    private String gestorId;

    // CONSTRUCTORES
    public Response() {
        super();
    }

    public Response(String codigo, String mensaje, String error, String correlationId) {
        super();
        this.errorCode = codigo;
        this.errorMessage = mensaje;
        this.errorType = error;
        this.correlationId = correlationId;
    }

}

