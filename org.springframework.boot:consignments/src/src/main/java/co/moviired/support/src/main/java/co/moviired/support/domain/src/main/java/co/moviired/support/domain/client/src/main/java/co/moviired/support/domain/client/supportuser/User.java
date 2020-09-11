package co.moviired.support.domain.client.supportuser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {

    private static final long serialVersionUID = -2381315378760910835L;

    private String firstName;
    private String lastName;
    private String msisdn;
    private String mpin;
    private String userType;
    private String agentCode;
    private String idtype;
    private String idno;
    private String gender;
    private String dob;
    private String email;
    private String cellphone;
    private String mahindraUser;
    private String mahindraPassword;
    private String status;

}

