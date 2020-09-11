package co.moviired.gateway.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Profile implements Serializable {

    private String profileName;

    private List<Operation> operations;

}

