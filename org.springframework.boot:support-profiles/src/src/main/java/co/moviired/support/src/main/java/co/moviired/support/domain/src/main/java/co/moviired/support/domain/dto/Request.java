package co.moviired.support.domain.dto;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.entity.Profile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class Request {

    private String idProfile;
    private String statusOperation;
    private String statusProfile;
    private Profile profile;
    private List<Integer> operationsId;


}

