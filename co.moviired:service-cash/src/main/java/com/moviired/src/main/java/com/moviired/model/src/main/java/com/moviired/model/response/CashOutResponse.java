package com.moviired.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

@lombok.Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CashOutResponse implements Serializable {

    private String cashOutId;

    private String state;

    private Integer amount;

    private String phoneNumber;

    private String agentName;

    private String time;

    private String token;


    public CashOutResponse(String pCashOutId, String pState, Integer pAmount, String pToken) {
        super();
        this.cashOutId = pCashOutId;
        this.state = pState;
        this.amount = pAmount;
        this.token = pToken;
    }
}

