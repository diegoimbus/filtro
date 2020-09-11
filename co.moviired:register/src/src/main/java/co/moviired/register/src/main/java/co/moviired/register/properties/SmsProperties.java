package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "providers.support-sms")
public final class SmsProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private long numRetries;
    private String defaultEmail;
    private String cellPrefix;

    // TEMPLATES
    private String otpMoviiTemplate;
    private String registerMoviiTemplate;
    private String otpMoviiredTemplate;
    private String registerMoviiredTemplate;
    private String forgetPasswordMoviiredTemplate;

    // SMS: OTP Registro
    private String queryOtp;
    private Integer queryOtpRetries;
    private Integer queryOtpRetriesDelay;
    private Integer queryOtpHours;
    private String queryOtpExprPattern;
    private Map<String, String> queryOtpRegExpr;

    // DATABASE
    private String driverDb;
    private String urlDb;
    private String poolNameDb;
    private String catalogDb;
    private String userDb;
    private String passDb;
    private Boolean autoCommitDb;
    private Boolean allowPoolSuspensionDb;
    private Integer connectionTimeoutDb;
    private Integer idleTimeoutDb;
    private Integer maximumPoolSizeDb;
    private Integer minimumIdleDb;
    private Integer maxLifetimeDb;

}

