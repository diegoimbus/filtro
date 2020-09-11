package co.moviired.transaction.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "data",
        "responseType",
        "responseCode",
        "responseMessage"
})
public class TransactionResponse implements Serializable {

    private static final long serialVersionUID = 120223841955431978L;

    private transient Object data;
    private String responseType;
    private String responseCode;
    private String responseMessage;

    public TransactionResponse(Object pData, String pResponseType, String pResponseCode, String pResponseMessage) {
        this.data = pData;
        this.responseType = pResponseType;
        this.responseCode = pResponseCode;
        this.responseMessage = pResponseMessage;
    }

    public TransactionResponse() {

    }
}
