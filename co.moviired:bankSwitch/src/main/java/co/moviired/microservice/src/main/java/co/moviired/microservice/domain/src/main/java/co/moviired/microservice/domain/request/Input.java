package co.moviired.microservice.domain.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.exception.DataException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Input {

    private String shortReferenceNumber;
    private Integer valueToPay;
    private String echoData;
    private String transferId;
    private String correlationId;
    private String imei;
    private String lastName;

    private String referenceNumber; //--> referenceNumber
    private String tercId;
    private String accountType;
    private String typeDocument;
    private String numberDocument;
    private String accountOrdinal;
    private String upcId;
    private String otp;
    private String serviceCode;


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

            p = parameters.get("valueToPay");
            if (p != null) {
                if (!p.toString().equals("")) {
                    request.setValueToPay(Integer.parseInt(p.toString()));
                }
            }

            p = parameters.get("echoData");
            if (p != null) {
                request.setEchoData(p.toString());
            }

            p = parameters.get("transferId");
            if (p != null) {
                request.setTransferId(p.toString());
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

            p = parameters.get("accountOrdinal");
            if (p != null) {
                request.setAccountOrdinal(p.toString());
            }

            p = parameters.get("upcId");
            if (p != null) {
                request.setUpcId(p.toString());
            }

            p = parameters.get("otp");
            if (p != null) {
                request.setOtp(p.toString());
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

        } catch (Exception e) {
            throw new DataException("-2", e.getMessage());
        }
        return request;
    }

}

