package co.moviired.digitalcontent.business.provider.mahindra.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.mahindra.request.Command;
import co.moviired.digitalcontent.business.provider.parser.IParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class LoginServiceMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private final MahindraProperties mahindraProperties;

    public LoginServiceMahindraParser(MahindraProperties pmahindraProperties) {
        super();
        this.mahindraProperties = pmahindraProperties;
    }

    @Override
    public final IRequest parseRequest(@NotNull DigitalContentRequest data) {
        // Datos especificos de la transaccion
        Command loginService = new Command();

        // Usuario
        loginService.setMsisdn(data.getMsisdn1());
        loginService.setMpin(data.getMpin());

        // Datos prestablecidos
        loginService.setType(mahindraProperties.getNameAuthpinreq());
        loginService.setProvider(mahindraProperties.getProviderAuth());
        loginService.setOtpreq(mahindraProperties.getOtpReq());
        loginService.setIspincheckreq(mahindraProperties.getIsPinCheckReq());
        loginService.setSource(mahindraProperties.getSource());

        return loginService;
    }

}

