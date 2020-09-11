package co.moviired.register.providers.reegistraduria;

import co.moviired.register.providers.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistraduriaResponse implements IResponse {

    private String documentNumber;
    private String identificationType;
    private String firstSurname = "";
    private String secondSurname = "";
    private String firstName = "";
    private String secondName = "";
    private String expeditionDate;
    private String dob;
    private String gender;
    private String birthPlace;

    private String status;
    private StatusDTO statusDTO;

}

