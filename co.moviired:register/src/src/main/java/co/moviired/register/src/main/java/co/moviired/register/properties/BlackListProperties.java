package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.register.helper.ConstantsHelper.BLACK_LIST_PREFIX;

@Data
@ConfigurationProperties(prefix = BLACK_LIST_PREFIX)
public final class BlackListProperties implements Serializable {

    private String url;
    private boolean isEnable;
    private Integer timeoutConnect;
    private Integer timeoutRead;
}

