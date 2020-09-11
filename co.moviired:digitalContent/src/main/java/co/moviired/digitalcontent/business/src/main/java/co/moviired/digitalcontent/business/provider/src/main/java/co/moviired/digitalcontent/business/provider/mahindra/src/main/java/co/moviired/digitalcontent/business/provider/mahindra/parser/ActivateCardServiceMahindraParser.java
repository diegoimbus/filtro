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
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Service
public class ActivateCardServiceMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String DATE_FORMAT = "yyyyMMdd";

    private final MahindraProperties mahindraProperties;
    private final GlobalProperties config;
    private final StatusCodeConfig statusCodeConfig;

    public ActivateCardServiceMahindraParser(@NotNull MahindraProperties pmahindraProperties,
                                             @NotNull StatusCodeConfig pstatusCodeConfig,
                                             @NotNull GlobalProperties pconfig) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.config = pconfig;
        this.statusCodeConfig = pstatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull DigitalContentRequest data,
                                       @NotNull CommandResponse mhResponseAutenticacion
    ) {
        Date fechaActual = new Date();
        String ip = (data.getIp() == null) ? LOCAL_ADDRESS : data.getIp();

        // OperationCode
        String operationCode = config.getProcessActivate();

        // Datos especificos de la transaccion
        Command command = new Command();
        command.setFtxnid(data.getCorrelationId());
        command.setMsisdn1(data.getMsisdn1());
        command.setPhonenumber(data.getPhoneNumber());
        command.setMpin(data.getMpin());
        command.setAmount(data.getAmount());
        command.setEancode(data.getEanCode() + "|" + data.getCardSerialNumber() + "#" + operationCode);
        command.setProductid(data.getProductId());
        command.setEmail(data.getEmail());
        command.setIspincheckreq("Y");
        command.setImei(Utils.generateIMEI(data, ip, mhResponseAutenticacion.getAgentcode()));

        // Datos prestablecidos
        Utils.setTypeAndSubtype(command, mahindraProperties, mhResponseAutenticacion, operationCode, config);
        command.setPayid(mahindraProperties.getPayId());
        command.setPaymentInstrument(mahindraProperties.getPaymentInstrument());
        command.setLanguage(mahindraProperties.getLanguage1());
        command.setLanguage1(mahindraProperties.getLanguage1());
        command.setProvider(mahindraProperties.getProvider());
        command.setBprovider(mahindraProperties.getProvider2());
        command.setBlocksms(mahindraProperties.getBlockSMS());
        command.setCustomerdate(new SimpleDateFormat(DATE_FORMAT).format(fechaActual));
        return command;
    }


    @Override
    public final DigitalContentResponse parseResponse(@NotNull DigitalContentRequest digitalContentRequest, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandResponse command = (CommandResponse) pcommand;

        // Armar el objeto respuesta
        DigitalContentResponse response = new DigitalContentResponse();

        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus(), command.getMessage());
        response.setErrorType(ErrorType.PROCESSING.name());
        response.setErrorCode(command.getTxnstatus());
        response.setErrorMessage(command.getMessage());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setErrorType(null);
            response.setErrorMessage(statusCode.getMessage());
            response.setErrorCode(statusCode.getCode());
            response.setAmount(command.getAmount());
            response.setTransactionId(command.getTxnid());
        }

        return response;
    }


}

