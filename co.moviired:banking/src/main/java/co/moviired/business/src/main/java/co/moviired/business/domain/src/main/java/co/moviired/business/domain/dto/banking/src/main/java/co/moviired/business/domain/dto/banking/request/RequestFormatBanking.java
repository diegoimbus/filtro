package co.moviired.business.domain.dto.banking.request;

import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.enums.Modality;
import co.moviired.business.domain.enums.WeftType;
import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class RequestFormatBanking implements IRequest {

    private String amount; //depositValue

    //OPERACION DEPOSITO
    //private String destinationAccount; //shortReferenceNumber
    private String typeClient; //Variable que define si es MERCHANT or SUBSCRIBER para operaciòn de depositos
    private String accountType; //shortReferenceNumber
    private String source;
    private String ip;
    private String serviceCode; // --> billerCode, productId, EANCode
    private String componentDate; //fecha del sistema de cuando llega la petición
    private String gestorId;
    private String lastName;
    private String referenceNumber; // --> numberDocument


    //OPERACION RETIRO // CONSULTA
    private String typeDocument;
    private String numberDocument;
    private String accountOrdinal;
    private String upcId;
    private String otp;
    private String msisdn1;
    private String internalCode;

    //OTHERS
    private String mpin;
    private String correlationId;
    private String correlationIdPortal;
    private String issueDate;
    private String issuerId;
    private String issuerName;
    private String agentCode;
    private String posId;
    private String tercId;
    private String productCode;
    private String processCode;
    private String billerName;
    private String echoData;
    private String specialFields;

    //Variables necesarias para pagos automàticos
    private String ean13BillerCode;
    private Integer positionReference1;
    private Integer lengthReference1;
    private String billReferenceNumber;
    private String shortReferenceNumber;
    private String collectionMethod;
    private String customerId;
    private String userName;
    private String passwordHash;
    private String requestDate;

    private String homologateIncom;
    private String homologateBankId;

    //nuevas variables
    private String emailNotification;
    private String phoneNumberNotification;
    private String referenceNumber2;
    private String deadLine;
    private String customerNotification;
    private String typePayBillDeposit;

    private Integer maxValue;
    private Integer minValue;
    private String url;
    private WeftType weftType;
    private Modality modality;
    private Boolean partialPayment;
    private CollectionType typePayBill;

}

