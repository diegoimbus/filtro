package co.moviired.digitalcontent.business.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class SMSProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SMS DB
    @Value("${properties.SMS.DRIVER_SMS_DB}")
    private String driverSmsDb;

    @Value("${properties.SMS.URL_SMS_DB}")
    private String urlSmsDb;

    @Value("${properties.SMS.POOL_NAME_SMS_DB}")
    private String poolNameSmsDb;

    @Value("${properties.SMS.USER_SMS_DB}")
    private String userSmsDb;

    @Value("${properties.SMS.PASS_SMS_DB}")
    private String passSmsDb;

    @Value("${properties.SMS.SUFIJO_CELULAR}")
    private String sufijoCelular;


}
