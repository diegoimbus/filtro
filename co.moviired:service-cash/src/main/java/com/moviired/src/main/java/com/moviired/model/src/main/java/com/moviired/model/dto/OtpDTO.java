package com.moviired.model.dto;


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
    private Integer amount;
    private Boolean sendSms;
    private int otpLength;
    private int timeExpired;


}

