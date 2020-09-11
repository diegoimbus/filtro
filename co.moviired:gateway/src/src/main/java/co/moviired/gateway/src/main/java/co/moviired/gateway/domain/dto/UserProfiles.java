package co.moviired.gateway.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserProfiles implements Serializable {

    @JsonProperty
    private List<Profile> profiles;

}

