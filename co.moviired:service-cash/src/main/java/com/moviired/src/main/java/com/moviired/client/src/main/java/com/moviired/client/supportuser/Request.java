package com.moviired.client.supportuser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private String msisdn;
    private String mpin;
    private String newmpin;
    private User user;
    private String otp;
    private String correlationId;
    private String ip;

}

