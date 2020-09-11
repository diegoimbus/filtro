package co.moviired.moneytransfer.client.registraduria;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegisterResponse implements Serializable {
    private StatusDTO statusDTO;
    private String documentNumber;
    private String identificationType;
    private String firstName;
    private String secondName;
    private String firstSurname;
    private String secondSurname;
}

