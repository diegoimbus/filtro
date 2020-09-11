package co.moviired.supportaudit.domain.response;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */


import co.moviired.audit.domain.dto.AuditDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private String code;
    private String message;

    private List<AuditDto> audits;
    private AuditDto audit;

}

