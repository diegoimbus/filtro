package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DigitalContentRequest implements ICommandRequest {


    private String correlationId;

    private String issueDate;

    private String issuerName;

    private String phoneNumber;

    private String source;

    private String ip;

    private String productId;

    private String eanCode;

    private Integer amount;

    private String email;

    private String usename;

    private String customerId;

    private String operation;

    private String cardSerialNumber;

    private String correlationIdR;


}

