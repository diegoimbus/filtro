package co.moviired.gateway.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Operation implements Serializable {

    private String id;

    private String operationName;

    private String operationDescription;

    private String operationUrl;

    private String status;

    private List<Profile> profiles;

}

