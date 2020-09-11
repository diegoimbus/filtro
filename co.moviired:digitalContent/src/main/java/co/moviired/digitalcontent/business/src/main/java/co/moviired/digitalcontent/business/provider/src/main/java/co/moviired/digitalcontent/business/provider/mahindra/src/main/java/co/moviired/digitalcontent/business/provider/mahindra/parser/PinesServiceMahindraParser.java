package co.moviired.digitalcontent.business.provider.mahindra.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.digitalcontent.business.conf.StatusCodeConfig;
import co.moviired.digitalcontent.business.domain.StatusCode;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.helper.Utils;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.IResponse;
import co.moviired.digitalcontent.business.provider.mahindra.request.Command;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
import co.moviired.digitalcontent.business.provider.parser.IParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Service
public class PinesServiceMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String DATE_FORMAT = "yyyyMMdd";


    private final MahindraProperties mahindraProperties;
    private final GlobalProperties config;

    private final StatusCodeConfig statusCodeConfig;

    public PinesServiceMahindraParser(@NotNull MahindraProperties pmahindraProperties,
                                      @NotNull StatusCodeConfig pstatusCodeConfig,
                                      @NotNull GlobalProperties pconfig) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.config = pconfig;
        this.statusCodeConfig = pstatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull DigitalContentRequest data,
                                       CommandResponse mhResponseAutenticacion
    ) {
        Command command = new Command();
        Date fechaActual = new Date();
        String ip = (data.getIp() == null) ? LOCAL_ADDRESS : data.getIp();

        // Datos especificos de la transaccion
        command.setMsisdn1(data.getMsisdn1());
        command.setPhonenumber(data.getPhoneNumber());
        command.setMpin(data.getMpin());
        command.setAmount(data.getAmount());
        command.setEancode(data.getEanCode() + "|#" + config.getProcessPines());
        command.setProductid(data.getProductId());
        command.setEmail(data.getEmail());
        command.setImei(Utils.generateIMEI(data, ip, mhResponseAutenticacion.getAgentcode()));

        // Datos prestablecidos
        Utils.setTypeAndSubtype(command, mahindraProperties, mhResponseAutenticacion, config.getProcessPines(), config);
        command.setPayid(mahindraProperties.getPayId());
        command.setPaymentInstrument(mahindraProperties.getPaymentInstrument());
        command.setLanguage1(mahindraProperties.getLanguage1());
        command.setProvider(mahindraProperties.getProvider());
        command.setBprovider(mahindraProperties.getProvider2());
        command.setBlocksms(mahindraProperties.getBlockSMS());
        command.setCustomerdate(new SimpleDateFormat(DATE_FORMAT).format(fechaActual));
        command.setTrid("");
        return command;

    }

    @Override
    public final DigitalContentResponse parseResponse(@NotNull DigitalContentRequest digitalContentRequest, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandResponse command = (CommandResponse) pcommand;

        // Armar el objeto respuesta
        DigitalContentResponse response = new DigitalContentResponse();
        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus(), command.getMessage());
        response.setErrorCode(command.getTxnstatus());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setAmount(command.getAmount());
            response.setTransactionId(command.getTxnid());
            response.setErrorType("");

            // Verificar si viene TRANSACTION_CODE = CONTROL_NUMBER | PIN (ENCRIPTADO)
            if (command.getTransactioncode() != null) {
                String[] varr = StringUtils.splitPreserveAllTokens(command.getTransactioncode(), '|');
                if (varr.length == 2) {
                    response.setPin(varr[1]);
                } else {
                    response.setPin(varr[0]);
                }
            }

            response.setAuthorizationCode(command.getAuthorizationcode());
            response.setErrorMessage("OK");
            response.setErrorCode(StatusCode.Level.SUCCESS.value());

        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }

}

