package co.moviired.transpiler.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class DigitalContentProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${digitalcontent.card.activate.url}")
    private String urlActivate;

    @Value("${digitalcontent.card.activate.code}")
    private String codeActivate;

    @Value("${digitalcontent.card.inactivate.url}")
    private String urlInactivate;

    @Value("${digitalcontent.card.inactivate.code}")
    private String codeInactivate;

    @Value("${digitalcontent.pines.sales.url}")
    private String urlPinesSale;

    @Value("${digitalcontent.pines.sales.code}")
    private String codePinesSale;

    @Value("${digitalcontent.pines.inactivate.url}")
    private String urlPinesInactivate;

    @Value("${digitalcontent.pines.inactivate.code}")
    private String codePinesInactivate;

    @Value("${digitalcontent.timeout.connection}")
    private int connectionTimeout;

    @Value("${digitalcontent.timeout.read}")
    private int readTimeout;

}

