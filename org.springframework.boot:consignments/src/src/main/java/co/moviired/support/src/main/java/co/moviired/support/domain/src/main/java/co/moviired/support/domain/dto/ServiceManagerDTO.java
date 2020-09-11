package co.moviired.support.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ServiceManagerDTO {

    private String actionMethod;
    @JsonProperty("eMail")
    private String email;
    @JsonProperty
    private String message;
}

