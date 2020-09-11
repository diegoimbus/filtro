package co.moviired.moneytransfer.domain.model.response;

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
public class MoneyTransferResponse implements Serializable {

    private static final long serialVersionUID = -5632439623236556629L;

    private transient Object data;
    private String responseType;
    private String responseCode;
    private String responseMessage;

    public MoneyTransferResponse() {

    }

    public MoneyTransferResponse(Object pData, String pResponseType, String pResponseCode, String pResponseMessage) {
        this.data = pData;
        this.responseType = pResponseType;
        this.responseCode = pResponseCode;
        this.responseMessage = pResponseMessage;
    }

    @Override
    public String toString() {
        return "{ data='" + data + "', responseType='" + responseType + "', responseCode='" + responseCode + "', responseMessage='" + responseMessage + "'}";
    }
}
