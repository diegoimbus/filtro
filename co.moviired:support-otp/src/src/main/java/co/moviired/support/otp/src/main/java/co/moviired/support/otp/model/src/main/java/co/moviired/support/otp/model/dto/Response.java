package co.moviired.support.otp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response implements Serializable {

    // Datos de la respuesta
    private String responseCode;
    private String responseMessage;
    private String otp;
    // Datos específicos del servicio
    private Boolean valid;

}
