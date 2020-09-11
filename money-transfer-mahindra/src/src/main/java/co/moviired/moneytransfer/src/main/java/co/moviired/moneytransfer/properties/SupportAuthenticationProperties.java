package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.supportauthentication")
public class SupportAuthenticationProperties implements Serializable {

    private static final String USER_LOGIN = "##USER_LOGIN##";
    private static final String USER_TYPE = "##USER_TYPE##";

    private String url;
    private int connectionTimeout;
    private int readTimeout;


    public String getPathQueryUserInfo(String userLogin, String userType) {
        return url.replace(USER_LOGIN, userLogin)
                .replace(USER_TYPE, userType);
    }
}

