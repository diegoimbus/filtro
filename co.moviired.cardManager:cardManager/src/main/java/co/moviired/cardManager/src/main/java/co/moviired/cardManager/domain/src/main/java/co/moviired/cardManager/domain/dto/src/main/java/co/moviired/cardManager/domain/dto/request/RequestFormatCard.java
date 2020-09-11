package co.moviired.cardManager.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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

}

