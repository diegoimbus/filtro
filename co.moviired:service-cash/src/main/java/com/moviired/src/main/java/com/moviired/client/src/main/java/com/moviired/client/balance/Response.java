package com.moviired.client.balance;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response implements Serializable {

    private String errorType;
    private String errorCode;
    private String errorMessage;
    private String balance;
    private String correlationId;
    private String transactionId;
    private Date transactionDate;


}

