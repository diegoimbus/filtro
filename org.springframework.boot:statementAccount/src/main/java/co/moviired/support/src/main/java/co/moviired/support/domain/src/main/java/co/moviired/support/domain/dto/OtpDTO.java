package co.moviired.support.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDTO implements Serializable {

    private String correlationId;
    private String documentType;
    private String documentNumber;
    private String name;
    private String phoneNumber;
    private String email;
    private Double amount;
    private int otpLength;
    private int timeExpired;

}

