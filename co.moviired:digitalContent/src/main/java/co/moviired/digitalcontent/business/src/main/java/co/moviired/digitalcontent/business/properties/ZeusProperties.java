package co.moviired.digitalcontent.business.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "properties.zeus")
public class ZeusProperties {

    private String driver;
    private String url;
    private String poolName;
    private String user;
    private String key;

    private String cryptoKey;
    private String smsTemplate;
    private String emailTemplate;
}

