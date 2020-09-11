package co.movii.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.support-sms")
public class SupportSmsProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private long numRetries;

    // MOVII
    private boolean loginMoviiEnable;
    private String loginTemplateMovii;
    private boolean userBlockMoviiEnable;
    private String userBlockMoviiTemplate;
    private String otpMoviiTemplate;
    private String deviceMoviiTemplate;


}

