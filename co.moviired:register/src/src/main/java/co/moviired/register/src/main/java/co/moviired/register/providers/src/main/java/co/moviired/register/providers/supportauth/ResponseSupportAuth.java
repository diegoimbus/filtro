package co.moviired.register.providers.supportauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author JAP, SBD
 * @version 1, 2019-08-30
 * @since 2.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseSupportAuth implements Serializable {

    private String errorType;
    private String errorCode;
    private String errorMessage;
}

