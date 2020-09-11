package co.moviired.cardManager.properties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${mahindra.urlTransactional}")
    private String urlTransactional;

    @Value("${mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${mahindra.timeout.read}")
    private int readTimeout;

    // LOGIN USER

    @Value("${properties.mahindra.loginService.TYPE}")
    private String lsType;

    @Value("${properties.mahindra.loginService.OTPREQ}")
    private String lsOtpreq;

    @Value("${properties.mahindra.loginService.ISPINCHECKREQ}")
    private String lsIsPINCheckReq;

    @Value("${properties.mahindra.loginService.SOURCE}")
    private String lsSource;

    @Value("${properties.mahindra.loginService.PROVIDER}")
    private String lsProvider;
}

