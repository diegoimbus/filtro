package co.moviired.support.domain.client.supportuser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class Response {
    //ERROR
    private String errorType;
    private String errorCode;
    private String errorMessage;

    private User user;
    private List<User> users;
    private String otp;
}

