package com.moviired.model.util;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OTP implements Serializable {

    private String value;
    private int validityOTP;
    private String unitOTP;
    private LocalDateTime vigenciaOTP;

}

