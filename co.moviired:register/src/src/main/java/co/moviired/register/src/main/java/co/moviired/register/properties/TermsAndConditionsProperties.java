package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.termsandconditions")
public final class TermsAndConditionsProperties implements Serializable {
    private String user;
    private String password;
    private String urlLogin;
    private String urlIntocheckandpersoninto;
    private String urlIntocheckandperson1;

}

