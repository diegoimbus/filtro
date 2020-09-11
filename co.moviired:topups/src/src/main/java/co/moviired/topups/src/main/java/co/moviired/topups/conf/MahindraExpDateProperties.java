package co.moviired.topups.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties("properties.mahindra.recharge")
public class MahindraExpDateProperties implements Serializable {

    private static final long serialVersionUID = -432109871412047907L;

    private String cmmndExpDatePattern;

    private String responseExpDatePattern;
}

