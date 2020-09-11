package co.moviired.microservice.domain.request;

import co.moviired.base.domain.exception.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Input {

    private String shortReferenceNumber;
    private Integer amount;
    private String echoData;
    private String correlationId;
    private String imei;
    private String lastName;
    private String source;
    private String transferId;

    private String referenceNumber; // --> referenceNumber
    private String tercId;
    private String accountType;
    private String typeDocument;
    private String numberDocument;
    private String serviceCode;
    private String internalCode;
    private String valueToPay;

    private String ticket;//Token

    public static Input parseInput(Map<String, Object> parameters) throws DataException {
        Input request = new Input();

        try {
            // Transformar los parámetros al objeto entrada
            Object p = parameters.get("shortReferenceNumber");
            if (p != null) {
                request.setShortReferenceNumber(p.toString());
            }

            p = parameters.get("imei");
            if (p != null) {
                request.setImei(p.toString());
            }

            p = parameters.get("lastName");
            if (p != null) {
                request.setLastName(p.toString());
            }

            p = parameters.get("echoData");
            if (p != null) {
                request.setEchoData(p.toString());
            }

            p = parameters.get("valueToPay");
            if (p != null) {
                request.setValueToPay(p.toString());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return request;
    }

}

