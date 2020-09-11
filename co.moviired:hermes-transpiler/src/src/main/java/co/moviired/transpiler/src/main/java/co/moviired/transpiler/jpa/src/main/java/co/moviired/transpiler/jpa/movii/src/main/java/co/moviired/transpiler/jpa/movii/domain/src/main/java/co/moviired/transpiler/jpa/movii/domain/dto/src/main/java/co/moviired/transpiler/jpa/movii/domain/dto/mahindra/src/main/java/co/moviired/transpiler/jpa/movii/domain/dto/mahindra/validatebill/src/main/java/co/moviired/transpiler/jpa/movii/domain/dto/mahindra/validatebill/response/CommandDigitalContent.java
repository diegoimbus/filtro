package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "outcome",
        "data"
})
public class CommandDigitalContent implements ICommandResponse {

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
    private String userName;
    private String authorizationCode;
    private String pin;
    private String termAndConditions;
    private String invoiceNumber;

    public Date getTransactionDate() {
        return transactionDate != null ? (Date) transactionDate.clone() : null;
    }

}

