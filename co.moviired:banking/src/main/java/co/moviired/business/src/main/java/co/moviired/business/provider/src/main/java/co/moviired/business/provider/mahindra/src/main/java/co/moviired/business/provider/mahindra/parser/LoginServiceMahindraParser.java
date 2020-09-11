package co.moviired.business.provider.mahindra.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.mahindra.request.CommandLoginServiceRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@AllArgsConstructor
public class LoginServiceMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final MahindraProperties mahindraProperties;

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking bankingRequest) {
        // Datos especificos de la transaccion
        CommandLoginServiceRequest loginService = new CommandLoginServiceRequest();

        log.info("BANKING VALIDATE USER REQUEST");
        log.info("msisdn1: " + bankingRequest.getMsisdn1());

        // Usuario
        loginService.setMsisdn(bankingRequest.getMsisdn1());
        loginService.setMpin(bankingRequest.getMpin());

        // Datos prestablecidos
        loginService.setType(this.mahindraProperties.getLsType());
        loginService.setProvider(this.mahindraProperties.getLsProvider());
        loginService.setOtpreq(this.mahindraProperties.getLsOtpreq());
        loginService.setIspincheckreq(this.mahindraProperties.getLsIsPINCheckReq());
        loginService.setSource(this.mahindraProperties.getLsSource());

        return loginService;
    }

}

