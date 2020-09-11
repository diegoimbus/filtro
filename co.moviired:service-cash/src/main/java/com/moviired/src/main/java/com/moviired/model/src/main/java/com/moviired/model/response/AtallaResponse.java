package com.moviired.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtallaResponse {

    private boolean status; //say if the response is successful or not
    private boolean validMessage; //say if the message is mapped on yml

    // Map error response
    private String atallaStatus; //atalla response error status (first field on atalla error message)
    private String error; //Error complete (second field on atalla error message)
    private String errorCode; //Error code (first 2 characters of error)
    private String errorLocation; //Param position with the error (second 2 characters of error)

    //Generate response
    private String mac;

    //Validate response
    private boolean valid;

    //Common response params
    private String verificationDigits;
}

