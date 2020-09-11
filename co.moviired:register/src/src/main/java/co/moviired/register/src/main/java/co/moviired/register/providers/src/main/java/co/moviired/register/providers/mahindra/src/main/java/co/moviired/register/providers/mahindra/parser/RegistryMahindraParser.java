package co.moviired.register.providers.mahindra.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.dto.RegisterRequest;
import co.moviired.register.domain.dto.RegisterResponse;
import co.moviired.register.properties.MahindraProperties;
import co.moviired.register.providers.IParser;
import co.moviired.register.providers.IRequest;
import co.moviired.register.providers.IResponse;
import co.moviired.register.providers.mahindra.request.CommandRegistryRequest;
import co.moviired.register.providers.mahindra.response.CommandRegistryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class RegistryMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final StatusCodeConfig statusCodeConfig;
    private final MahindraProperties mahindraProperties;

    @Autowired
    public RegistryMahindraParser(StatusCodeConfig pStatusCodeConfig, MahindraProperties pmahindraProperties) {
        super();
        this.statusCodeConfig = pStatusCodeConfig;
        this.mahindraProperties = pmahindraProperties;
    }

    @Override
    public final IRequest parseRequest(@NotNull RegisterRequest data) {

        CommandRegistryRequest command = new CommandRegistryRequest();

        command.setType(this.mahindraProperties.getType());
        command.setProvider(this.mahindraProperties.getProvider());
        command.setPayid(this.mahindraProperties.getPayid());
        command.setMsisdn(data.getUser().getMsisdn());
        command.setNpref(this.mahindraProperties.getNpref());
        command.setFname(data.getUser().getFirstName() + " " + data.getUser().getLastName());
        command.setLname(data.getUser().getShopName());
        command.setIdtype(data.getUser().getIdtype());
        command.setIdnumber(String.valueOf(Long.parseLong(data.getUser().getIdno())));
        command.setEmail(data.getUser().getEmail());
        command.setDob(data.getUser().getDob());
        command.setAddress(data.getUser().getAddress().replaceAll(this.mahindraProperties.getSpecialCharacters(), " ").replaceAll("[ ]+", " ").trim());
        command.setAddress(this.changeSpecialCaracteres(data.getUser().getAddress()));
        command.setDistrict(data.getUser().getDistrict());
        command.setCity(data.getUser().getCity());
        command.setGender(data.getUser().getGender());
        command.setLoginid(data.getUser().getMsisdn());
        command.setLanguage1(this.mahindraProperties.getLanguage1());
        command.setDoc1name(this.mahindraProperties.getDoc1Name());
        command.setProoftypeid1("CC");
        command.setIspincheckreq(this.mahindraProperties.getIspincheckreq());
        command.setImei(data.getImei());
        command.setSource(data.getSource());

        return command;
    }

    @Override
    public final RegisterResponse parseResponse(@NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandRegistryResponse command = (CommandRegistryResponse) pcommand;

        // Armar el objeto respuesta
        RegisterResponse response = new RegisterResponse();
        StatusCode statusCode = this.statusCodeConfig.of(command.getTxnstatus());

        response.setCode(statusCode.getCode());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setType("");
            response.setMessage("OK");
            response.setCode(StatusCode.Level.SUCCESS.value());

        } else if ("99".equalsIgnoreCase(statusCode.getCode())) {
            response.setType(ErrorType.PROCESSING.name());
            response.setMessage(((CommandRegistryResponse) pcommand).getMessage());
            response.setCode(((CommandRegistryResponse) pcommand).getTxnstatus());
        } else {
            response.setType(ErrorType.PROCESSING.name());
            response.setMessage(statusCode.getMessage());
        }

        return response;
    }


    private String changeSpecialCaracteres(String cadena) {
        String text = cadena;

        text = text.replaceAll("[ñ]", "n");
        text = text.replaceAll("[Ñ]", "N");
        text = text.replaceAll("[Á]", "A");
        text = text.replaceAll("[á]", "a");
        text = text.replaceAll("[é]", "e");
        text = text.replaceAll("[É]", "E");
        text = text.replaceAll("[í]", "i");
        text = text.replaceAll("[Í]", "I");
        text = text.replaceAll("[ó]", "o");
        text = text.replaceAll("[Ó]", "O");
        text = text.replaceAll("[ú]", "u");
        text = text.replaceAll("[Ú]", "U");
        text = text.replaceAll("[^a-zA-Z0-9 ]", "");

        return text;
    }
}

