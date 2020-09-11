package co.moviired.support.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "endpoints.mahindra")
public class MahindraProperties implements Serializable {

    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // Login
    private String loginType;
    private String loginSource;
    private String loginProvider;
    private String loginLanguage1;
    private String loginOtpReq;
    private String loginIsPinCheckReq;
}

