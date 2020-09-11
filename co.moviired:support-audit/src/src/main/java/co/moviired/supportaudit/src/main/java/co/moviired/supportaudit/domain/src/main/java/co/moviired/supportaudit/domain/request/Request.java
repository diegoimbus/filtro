package co.moviired.supportaudit.domain.request;
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
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private AuditDto audit;
}

