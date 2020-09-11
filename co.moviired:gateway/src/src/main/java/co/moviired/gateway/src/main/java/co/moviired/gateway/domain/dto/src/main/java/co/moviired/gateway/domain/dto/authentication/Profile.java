package co.moviired.gateway.domain.dto.authentication;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.gateway.domain.dto.Operation;
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
public class Profile implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int NAME_LENGTH = 500;

    private Integer id;
    private String profileName;
    private String profileDescription;
    private String createdUser;
    private String createdDate;
    private String lastModifiedUser;
    private String lastModifiedDate;
    private GeneralStatus enableDelete;
    private GeneralStatus status;
    private List<Operation> operations;

}


