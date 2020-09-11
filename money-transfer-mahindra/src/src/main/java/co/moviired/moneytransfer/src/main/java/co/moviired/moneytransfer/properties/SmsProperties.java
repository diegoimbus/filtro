package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-27
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "client.sms")
public class SmsProperties implements Serializable {
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private String smsContentOriginator;
    private String smsContentBeneficiary;
}

