package co.moviired.cardManager.domain.dto.request;

import co.moviired.base.domain.StatusCode;
import co.moviired.cardManager.conf.StatusCodeConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import co.moviired.base.domain.exception.DataException;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestFormatCard {

    //variables Solicitud de tarjeta
    private String pointName;
    private String pointAddress;
    private String pointNeighborhood;
    private String addresDetail;
    private String city;
    private String phoneNumber;
    private String idNumber;
    private String idType;
    private Boolean isSubsidiary;

    //Variables para el manejo de paginas
    private Integer pageSize;

    //variables de login MH
    private String msisdn1;
    private String mpin;

    public static void validateParameterRegistry(RequestFormatCard requestFormatCard, StatusCodeConfig statusCodeConfig) throws DataException {
        if (requestFormatCard.getPointName().isEmpty() || requestFormatCard.getPointName() == null ){
            StatusCode statusCode = statusCodeConfig.of("CD01");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getPointAddress().isEmpty() || requestFormatCard.getPointAddress() == null){
            StatusCode statusCode = statusCodeConfig.of("CD02");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getPointNeighborhood().isEmpty() || requestFormatCard.getPointNeighborhood() == null){
            StatusCode statusCode = statusCodeConfig.of("CD03");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getCity().isEmpty() || requestFormatCard.getCity() == null){
            StatusCode statusCode = statusCodeConfig.of("CD04");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getPhoneNumber().isEmpty() || requestFormatCard.getPhoneNumber() == null){
            StatusCode statusCode = statusCodeConfig.of("CD05");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getIdNumber().isEmpty() || requestFormatCard.getIdNumber() == null){
            StatusCode statusCode = statusCodeConfig.of("CD06");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getIdType().isEmpty() || requestFormatCard.getIdType() == null){
            StatusCode statusCode = statusCodeConfig.of("CD07");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getIsSubsidiary() == null){
            StatusCode statusCode = statusCodeConfig.of("CD08");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

    }

    public static void validateParameterUpdate(RequestFormatCard requestFormatCard, StatusCodeConfig statusCodeConfig) throws DataException {

        if (requestFormatCard.getPhoneNumber().isEmpty() || requestFormatCard.getPhoneNumber() == null){
            StatusCode statusCode = statusCodeConfig.of("CD05");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getIdNumber().isEmpty() || requestFormatCard.getIdNumber() == null){
            StatusCode statusCode = statusCodeConfig.of("CD06");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (requestFormatCard.getIdType().isEmpty() || requestFormatCard.getIdType() == null){
            StatusCode statusCode = statusCodeConfig.of("CD07");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

    }

}

