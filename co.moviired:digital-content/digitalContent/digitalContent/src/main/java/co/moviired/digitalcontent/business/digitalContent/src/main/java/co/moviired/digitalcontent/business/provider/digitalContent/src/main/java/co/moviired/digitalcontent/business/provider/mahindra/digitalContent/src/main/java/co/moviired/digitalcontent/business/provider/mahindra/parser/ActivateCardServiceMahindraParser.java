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
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.IParser;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.IResponse;
import co.moviired.digitalcontent.business.provider.mahindra.request.Command;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandLoginServiceResponse;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
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
    private static final String LBL_DIGITAL_CONTENT = "DIGITAL-CONTENT|";
    private static final String DATE_FORMAT = "yyyyMMdd";


    private final MahindraProperties mahindraProperties;
    private final GlobalProperties config;
    private final StatusCodeConfig statusCodeConfig;

    public ActivateCardServiceMahindraParser(MahindraProperties pmahindraProperties,
                                             StatusCodeConfig statusCodeConfig,
                                             GlobalProperties config) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.config = config;
        this.statusCodeConfig = statusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull DigitalContentRequest data,
                                       CommandLoginServiceResponse mhResponseAutenticacion
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

        // Se agrega en el IMEI el número de transacción del cliente
        command.setImei("0|" + ip + "|" +
                LBL_DIGITAL_CONTENT +
                data.getCorrelationId() + "|" +
                data.getIssueDate() + "|" +
                data.getIssuerLogin() + "|" +
                data.getPosId() + "|" +
                mhResponseAutenticacion.getAgentcode() + "|" +
                data.getMsisdn1() + "|" +
                data.getIncommCode());

        // Datos prestablecidos
        setTypeAndSubtype(command, mahindraProperties, mhResponseAutenticacion, operationCode, config);
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


    private void setTypeAndSubtype(
            @NotNull Command request,
            @NotNull MahindraProperties mahindraProperties,
            @NotNull CommandLoginServiceResponse mhResponseAutenticacion,
            @NotNull String operacion,
            @NotNull GlobalProperties config) {

        if ((operacion.equalsIgnoreCase(config.getProcessActivate())) ||
                (operacion.equalsIgnoreCase(config.getProcessInactivate()))) {
            if (mhResponseAutenticacion.getUsertype().equalsIgnoreCase("CHANNEL")) {
                request.setType(mahindraProperties.getTypeCardMerchant());
                request.setSubtype(mahindraProperties.getSubTypeCardMerchant());
            } else {
                request.setType(mahindraProperties.getTypeCardSubcriber());
                request.setSubtype(mahindraProperties.getSubTypeCardSubcriber());
            }
        } else {
            if (operacion.equalsIgnoreCase(config.getProcessPines())) {
                if (mhResponseAutenticacion.getUsertype().equalsIgnoreCase("CHANNEL")) {
                    request.setType(mahindraProperties.getTypePinMerchant());
                    request.setSubtype(mahindraProperties.getSubTypePinMerchant());
                } else {
                    request.setType(mahindraProperties.getTypePinSubcriber());
                    request.setSubtype(mahindraProperties.getSubTypePinSubcriber());
                }
            }
        }
    }
}

