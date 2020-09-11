package co.moviired.microservice.domain.request;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.domain.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Input implements Serializable {

    private static final long serialVersionUID = 2L;

    private String shortReferenceNumber;
    private String valueToPay;
    private String lastName;
    private String echoData;
    private String imei;

    public static void validateParameters(Input parameters, OperationType opType, StatusCodeConfig statusCodeConfig) throws DataException {
        StatusCode statusCode;
        if (parameters.getShortReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("C01");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (parameters.getImei() == null || parameters.getImei().isBlank()) {
            statusCode = statusCodeConfig.of("C02");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (parameters.getLastName() == null) {
            statusCode = statusCodeConfig.of("C03");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (opType.equals(OperationType.PAYMENT) && (parameters.getEchoData() == null || parameters.getEchoData().isBlank())) {
            statusCode = statusCodeConfig.of("C04");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (opType.equals(OperationType.PAYMENT) && (parameters.getValueToPay() == null || !parameters.getValueToPay().matches("\\d+"))) {
            statusCode = statusCodeConfig.of("C05");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
    }

}

