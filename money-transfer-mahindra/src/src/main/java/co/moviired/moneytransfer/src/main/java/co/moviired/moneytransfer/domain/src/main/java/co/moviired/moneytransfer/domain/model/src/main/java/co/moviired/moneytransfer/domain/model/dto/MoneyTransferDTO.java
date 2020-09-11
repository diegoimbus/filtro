package co.moviired.moneytransfer.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MoneyTransferDTO implements Serializable {

    private static final long serialVersionUID = -6989163067071783370L;

    private String merchantId;
    private String posId;

    private String transactionId;
    private String correlationId;
    private String eanCode;
    private String issuerDate;
    private String origin;
    private String source;
    private String idTypeSender;
    private String idSender;
    private String phoneNumberSender;
    private String emailSender;
    private String idTypeReceiver;
    private String idReceiver;
    private String phoneNumberReceiver;
    private String emailReceiver;
    private Integer amount;
    private Integer freight;
    private Integer freightIva;
    private Integer amountTotal;
    private String txnCount;
    private transient List<Object> txn;
}

