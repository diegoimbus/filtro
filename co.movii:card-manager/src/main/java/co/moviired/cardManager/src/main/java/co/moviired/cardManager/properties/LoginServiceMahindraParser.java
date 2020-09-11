package co.moviired.cardManager.properties;

import co.moviired.cardManager.domain.dto.request.RequestFormatCard;
import co.moviired.cardManager.provider.mahindra.CommandLoginServiceRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@AllArgsConstructor
public class LoginServiceMahindraParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final MahindraProperties mahindraProperties;

    public final CommandLoginServiceRequest parseRequest(@NotNull String userpass) {
        // Datos especificos de la transaccion
        CommandLoginServiceRequest loginService = new CommandLoginServiceRequest();

        String[] vautorization = userpass.split(":");

        log.info("BANKING VALIDATE USER REQUEST");
        log.info("msisdn1: " + vautorization[0]);

        // Usuario
        loginService.setMsisdn(vautorization[0]);
        loginService.setMpin(vautorization[1]);

        // Datos prestablecidos
        loginService.setType(this.mahindraProperties.getLsType());
        loginService.setProvider(this.mahindraProperties.getLsProvider());
        loginService.setOtpreq(this.mahindraProperties.getLsOtpreq());
        loginService.setIspincheckreq(this.mahindraProperties.getLsIsPINCheckReq());
        loginService.setSource(this.mahindraProperties.getLsSource());

        return loginService;
    }

}

