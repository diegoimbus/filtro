package co.moviired.support.domain.dto;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.entity.Module;
import co.moviired.support.domain.entity.Operation;
import co.moviired.support.domain.entity.Profile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Slf4j
public class Response {

    private List<Operation> operations;
    private List<Profile> profiles;
    private List<Module> modules;
    private List<String> authorities;
    private Profile profile;
    private Operation operation;

    //ERROR
    private String errorType;
    private String errorCode;
    private String errorMessage;
}

