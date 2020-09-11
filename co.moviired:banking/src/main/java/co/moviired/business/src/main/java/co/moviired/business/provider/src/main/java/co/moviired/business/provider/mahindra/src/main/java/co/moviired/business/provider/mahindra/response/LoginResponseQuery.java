package co.moviired.business.provider.mahindra.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "msisdn",
        "userType",
        "firstName",
        "agentCode",
        "idType",
        "idNo",
        "email",
        "gender",
        "dob"
})
public class LoginResponseQuery {

    private String msisdn;
    private String userType;
    private String firstName;
    private String agentCode;
    private String idType;
    private String idNo;
    private String email;
    private String gender;
    private String dob;

}
