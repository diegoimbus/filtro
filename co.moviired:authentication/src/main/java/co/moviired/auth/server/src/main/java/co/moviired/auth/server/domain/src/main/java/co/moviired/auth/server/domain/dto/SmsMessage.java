package co.moviired.auth.server.domain.dto;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SmsMessage implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private String phoneNumber;
    private String templateCode;
    private String messageContent;
    private Map<String, String> variables;

}


