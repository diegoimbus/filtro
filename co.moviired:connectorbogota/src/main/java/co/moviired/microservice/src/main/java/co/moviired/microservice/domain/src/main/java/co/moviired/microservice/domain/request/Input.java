package co.moviired.microservice.domain.request;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.base.domain.exception.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

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


    public static Input parseInput(Map<String, Object> parameters) throws DataException {
        Input request = new Input();

        try {
            // Transformar los parámetros al objeto entrada
            Object p = parameters.get("shortReferenceNumber");
            if (p != null) {
                request.setShortReferenceNumber(p.toString());
            }

            p = parameters.get("serviceCode");
            if (p != null) {
                request.setServiceCode(p.toString());
            } else {
                p = parameters.get("billerCode");
                if (p != null) {
                    request.setServiceCode(p.toString());
                }
            }

            p = parameters.get("source");
            if (p != null) {
                request.setSource(p.toString());
            }

            p = parameters.get("internalCode");
            if (p != null) {
                request.setInternalCode(p.toString());
            }

            p = parameters.get("valueToPay");
            if (p != null && !p.toString().equals("null")) {
                request.setAmount(Integer.parseInt(p.toString()));
            }

            p = parameters.get("echoData");
            if (p != null) {
                request.setEchoData(p.toString());
            }

            p = parameters.get("correlationId");
            if (p != null) {
                request.setCorrelationId(p.toString());
            }

            p = parameters.get("referenceNumber");
            if (p != null) {
                request.setReferenceNumber(p.toString());
            }

            p = parameters.get("tercId");
            if (p != null) {
                request.setTercId(p.toString());
            }

            p = parameters.get("accountType");
            if (p != null) {
                request.setAccountType(p.toString());
            }

            p = parameters.get("typeDocument");
            if (p != null) {
                request.setTypeDocument(p.toString());
            }

            p = parameters.get("imei");
            if (p != null) {
                request.setImei(p.toString());
            }

            p = parameters.get("lastName");
            if (p != null) {
                request.setLastName(p.toString());
            }

            p = parameters.get("numberDocument");
            if (p != null) {
                request.setNumberDocument(p.toString());
            }

            p = parameters.get("transferId");
            if (p != null) {
                request.setTransferId(p.toString());
            }

        } catch (Exception e) {
            throw new DataException("-2", e.getMessage());
        }
        return request;
    }

}

