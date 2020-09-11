package co.moviired.microservice.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Input implements Serializable {

    private static final long serialVersionUID = 1L;

    private String shortReferenceNumber;
    private String valueToPay;
    private Integer amount;
    private String echoData;
    private String imei;
    private String lastName;

    public void castValueToAmount() {
        this.amount = Integer.parseInt(valueToPay);
    }

}

