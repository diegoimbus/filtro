package co.moviired.transpiler.jpa.movii.domain.dto.hermes.response;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.DigitalContentHermesRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class DigitalContentHermesResponse implements IHermesResponse {


    private DigitalContentHermesRequest request;
    private ResponseHermes response;
    private String userName;
    private String agentCode;
    private String name;
    private String phoneNumber;
    private String errorMessage;
    private String errorCode;
    private String errorType;
    private String amount;
    private String correlationId;
    private String cashInId;
    private String transactionId;
    private Long transactionTime;
    private Date transactionDate;
    private String authorizationNumber;
    private String authorizationCode;
    private String pin;
    private String termAndConditions;
    private String invoiceNumber;


    @Override
    public void setRequest(IHermesRequest request) {
        // No se implementa
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate != null ? (Date) transactionDate.clone() : null;
    }
}

