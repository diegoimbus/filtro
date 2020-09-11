package co.moviired.moneytransfer.client.supportathentication;

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
public class SupportAuthenticationRequest implements Serializable {

    private String userLogin;
    private String userType;
    private String correlationId;

}

