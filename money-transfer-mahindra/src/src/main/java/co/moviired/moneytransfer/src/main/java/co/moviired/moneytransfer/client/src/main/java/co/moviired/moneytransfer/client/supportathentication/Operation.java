package co.moviired.moneytransfer.client.supportathentication;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.moneytransfer.domain.enums.GeneralStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Operation implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;
    private Integer id;
    private String operationName;
    private String operationDescription;
    private String operationUrl;
    private GeneralStatus status;
    private List<Profile> profiles;

}

