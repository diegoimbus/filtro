package co.moviired.support.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.support.util.ConstantsHelper.PREFIX_EMAIL_GENERATOR;

@Data
@ConfigurationProperties(prefix = PREFIX_EMAIL_GENERATOR)
public class EmailGeneratorProperties implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    private String urlPathSendMovements;
}

