package co.moviired.topups.mahindra.parser.impl;

import co.moviired.topups.cache.OperatorCache;
import co.moviired.topups.conf.*;
import co.moviired.topups.exception.DataException;
import co.moviired.topups.exception.ParseException;
import co.moviired.topups.mahindra.parser.IMahindraParser;
import co.moviired.topups.model.domain.Operator;
import co.moviired.topups.model.domain.dto.GestorId;
import co.moviired.topups.model.domain.dto.mahindra.ICommandRequest;
import co.moviired.topups.model.domain.dto.mahindra.ICommandResponse;
import co.moviired.topups.model.domain.dto.mahindra.recharge.request.CommandRechargeIntegration;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationHeaderRequest;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationRequest;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationResponse;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationHeaderRequest;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationRequest;
import co.moviired.topups.model.domain.dto.recharge.response.RechargeIntegrationResponse;
import co.moviired.topups.model.enums.OperatorStatusType;
import co.moviired.topups.model.enums.OperatorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@Slf4j
@Service
public class RechargeMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = -3606029583960966752L;

    private static final String COMMAND_TRX_STATUS = "200";

    private static final String INVALID_OPERATOR_MSG_ERROR = "Operador inválido";

    //Repositories, properties and services

    private final OperatorCache operatorCache;

    private final MahindraExpDateProperties mahindraFormatter;

    private final MahindraProperties mahindraProperties;

    private final GestorIdConfigProperties gestorConfig;

    private final OperatorStatusMessagesProperties operatorStatusMsg;

    private final IssuerDateParserProperties issuerDateParser;

    public RechargeMahindraParser(@NotNull MahindraProperties pmahindraProperties,
                                  @NotNull MahindraExpDateProperties mahindraFormatter,
                                  @NotNull OperatorCache operatorCache,
                                  @NotNull GestorIdConfigProperties gestorConfig,
                                  @NotNull OperatorStatusMessagesProperties operatorStatusMsg,
                                  @NotNull IssuerDateParserProperties issuerDateParser) {
        super();
        this.mahindraFormatter = mahindraFormatter;
        this.mahindraProperties = pmahindraProperties;
        this.operatorCache = operatorCache;
        this.gestorConfig = gestorConfig;
        this.operatorStatusMsg = operatorStatusMsg;
        this.issuerDateParser = issuerDateParser;
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull String logIdent,
                                              @NotNull IRechargeIntegrationRequest rechargeIntegrationRequest,
                                              @NotNull IRechargeIntegrationHeaderRequest rechargeHeaderRequest) throws ParseException {
        // Transformar al tipo específico de IRechargeIntegrationRequest Request
        RechargeIntegrationRequest request = (RechargeIntegrationRequest) rechargeIntegrationRequest;
        RechargeIntegrationHeaderRequest header = (RechargeIntegrationHeaderRequest) rechargeHeaderRequest;

        // Datos especificos de la transaccion
        co.moviired.topups.model.domain.dto.mahindra.recharge.request.CommandRechargeIntegration recharge = new co.moviired.topups.model.domain.dto.mahindra.recharge.request.CommandRechargeIntegration();
        log.info(
                "{} \"amount\": \"{}\", \"eanCode\": \"{}\" ,\"ip\": \"{}\", \"issuerDate\": \"{}\", \"issuerId\": \"{}\", \"issuerName\": \"{}\" , \"ProductId\": \"{}\", \"source\": \"{}\" , \"packageId\": \"{}\"",
                logIdent, request.getAmount(), request.getEanCode(), request.getIp(), request.getIssuerDate(),
                request.getIssuerId(), request.getIssuerName(), request.getProductId(), request.getSource(),
                request.getPackageId());

        recharge.setAmount(request.getAmount());

        recharge.setEancode(request.getEanCode());
        String componentDateFormatted = getComponentDateAsString(logIdent, header.getComponentDate());
        recharge.setImei(String.format(
                mahindraProperties.getImeiPattern(),
                null != request.getImei() ? request.getImei() : "",
                request.getIp(),
                request.getSource(),
                request.getCorrelationId(),
                componentDateFormatted)
        );
        recharge.setFtxnid(request.getCorrelationId());
        recharge.setCellid(header.getMerchantId());
        //recharge.setImei("|200.118.20.20|API|20191028173535|20191028173535.032");

        String remarkDateIssuerDate = genericFormatDate(
                logIdent,
                request.getIssuerDate(),
                issuerDateParser.getComponentDatePattern(),
                issuerDateParser.getIssuerDateOutcomingPattern()
        );

        recharge.setRemarks(String.format(
                mahindraProperties.getRemarkPattern(),
                request.getIssuerId(),
                header.getPosId(),
                remarkDateIssuerDate,
                request.getIssuerName())
        );
        recharge.setEchodata(request.getEchoData());
        recharge.setSource(request.getSource());

        // Usuario
        String[] auth = header.getAuthorization().split(mahindraProperties.getAuthSplitter());
        recharge.setMsisdn(auth[0]);
        recharge.setMsisdn2(header.getReferenceNumber());
        recharge.setMpin(auth[1]);
        recharge.setPin(auth[1]);

        // <Datos predefinidos>
        if(request.getSource().equals(mahindraProperties.getClientSubscriber())){
            recharge.setType(mahindraProperties.getTypeSubscriber());
        }else{
            recharge.setType(mahindraProperties.getTypeMerchant());
        }

        recharge.setPayid2(mahindraProperties.getPayid2());
        recharge.setPaymenttype(mahindraProperties.getPaymenttype());
        recharge.setPayid(mahindraProperties.getPayid());
        recharge.setProvider2(mahindraProperties.getProvider2());
        recharge.setLanguage1(mahindraProperties.getLanguage());
        recharge.setProvider(mahindraProperties.getProvider());

        // From Database
        Operator operator = getOperatorByRechargeOrPackage(logIdent, request);
        if (validateOperator(operator)) {
            recharge.setOperatorid(String.valueOf(operator.getOperatorId()));
            recharge.setOperatorname(operator.getName());
            recharge.setProductid(operator.getProductCode());
            recharge.setName(operator.getOperatorName());
        }
        log.info("{} \"Final mahindra Request\": \"{}\"}", logIdent, recharge);
        return recharge;
    }

    public String getComponentDateAsString(@NotNull String logIdent, long componentDateLong) {
        log.debug("{}", logIdent);
        Date componentDate = new Date(componentDateLong);
        SimpleDateFormat formatter = new SimpleDateFormat(issuerDateParser.getComponentDatePattern());
        return formatter.format(componentDate);
    }

    @Override
    public final IRechargeIntegrationResponse parseResponse(@NotNull String logIdent,
                                                            @NotNull ICommandRequest iCommandMahindraRequest,
                                                            @NotNull ICommandResponse iCommandMahindraResponse,
                                                            String... extraFields) {
        // Transformar al command específico
        co.moviired.topups.model.domain.dto.mahindra.recharge.response.CommandRechargeIntegration command = (co.moviired.topups.model.domain.dto.mahindra.recharge.response.CommandRechargeIntegration) iCommandMahindraResponse;
        CommandRechargeIntegration request = (CommandRechargeIntegration) iCommandMahindraRequest;
        RechargeIntegrationResponse response = RechargeIntegrationResponse.builder()
                .errorCode(command.getTxnstatus().toUpperCase())
                .errorMessage(command.getMessage().toUpperCase())
                .build();
        response.setErrorType("0");
        response.setCorrelationId(request.getFtxnid());

        if (COMMAND_TRX_STATUS.equals(command.getTxnstatus())) {
            response.setAmount(command.getAmount());
            response.setAuthorizationCode(command.getAuthorizationcode().toUpperCase());
            response.setInvoiceNumber(command.getInvoicenumber());
            response.setTransactionDate(formatMahindraDateToRechargeDate(logIdent, command.getTransactiondate(), mahindraFormatter));
            response.setTransferId(command.getTxnid().toUpperCase());
            response.setErrorCode("00");
            response.setOperatorName(request.getName());
            response.setPackageAmount(extraFields[0]);
            if(command.getExpirationdate() != null &&  !command.getExpirationdate().equals(""))
                response.setExpirationDate(formatMahindraDateToRechargeDate(logIdent, command.getExpirationdate(), mahindraFormatter));
            response.setGestorId(getGestorIdFromMahindraResponse(logIdent, command.getAuthorizationcode()));
            response.setCustomerBalance(command.getNewbalance());
            response.setProductName(request.getOperatorname());
        }

        return response;
    }

    // Valida si viene subProductId, si es != a nulo o vacío; busca el operador.productId por atributo request.subProductId
    private Operator getOperatorByRechargeOrPackage(String logIdent, RechargeIntegrationRequest request) throws ParseException {
        String productId = request.getProductId();
        OperatorType operatorType = OperatorType.RECHARGE;
        if (null != request.getPackageId() && !request.getPackageId().trim().isEmpty()) {
            productId = request.getPackageId();
            operatorType = OperatorType.PACKAGE;
        }

        return getOperatorValidatingSearchParams(logIdent, request.getEanCode(), productId, operatorType);
    }

    // Verificar el status del operador, si está activo(1) o inactivo(0) o suspendido(2)
    public boolean validateOperator(Operator operator) throws ParseException {
        if (null == operator) {
            throw new ParseException(INVALID_OPERATOR_MSG_ERROR);
        }

        OperatorStatusType opStatus = OperatorStatusType.parse(operator.getStatus());
        switch (opStatus) {
            case ACTIVE:
                return true;
            case INACTIVE:
                //throw new ParseException(operatorStatusMsg.getOperatorStatusInactiveMsg());
                return true;
            case SUSPENDED:
                throw new ParseException(operatorStatusMsg.getOperatorStatusSuspendedMsg());
            default:
                throw new ParseException(INVALID_OPERATOR_MSG_ERROR);
        }
    }

    // Obtener operador o por eanCode o por ProductCode, si eanCode es nulo intenta buscar por productCode y type; si producCode y type son inválidos retorna DataException
    public Operator getOperatorValidatingSearchParams(String logIdent,
                                                      String eanCode,
                                                      String productId,
                                                      @NotNull OperatorType operatorType) throws ParseException {
        log.debug("{}, \"eanCode\": \"{}\" , \"productCode\":\"{}\", \"OperatorType\":\"{}\"}", logIdent, eanCode, productId, operatorType);
        try {
            if (null != eanCode && !eanCode.isEmpty()) {
                return operatorCache.getOperatorByEanCode(eanCode);
            } else if (null != productId && !productId.isEmpty()) {
                return operatorCache.getOperatorByProductCodeAndType(productId, operatorType.getId());
            }

            throw new DataException("-1", INVALID_OPERATOR_MSG_ERROR);

        } catch (Exception e) {
            log.warn("{}, \"Error\": \"{}\"}", logIdent, e.getMessage());
            throw new ParseException(e.getMessage());
        }

    }

    public String genericFormatDate(@NotNull String logIdent,
                                    String dateToFormatStr,
                                    String dateFormatterOrigin,
                                    String dateFormatterDesination) {
        String df = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormatterOrigin);
            Date dateToFormatDT = format.parse(dateToFormatStr);
            format = new SimpleDateFormat(dateFormatterDesination);
            df = format.format(dateToFormatDT);

        } catch (Exception e) {
            log.warn("{}, \"Error\":\"{}\", \"SystemWillReturnEmptyDate\":\"true\"}", logIdent, e.getMessage());
        }

        return df;
    }

    public String formatMahindraDateToRechargeDate(@NotNull String logIdent,
                                                   String dateToFormatStr,
                                                   MahindraExpDateProperties mhdrFormatter) {
        return genericFormatDate(logIdent, dateToFormatStr, mhdrFormatter.getCmmndExpDatePattern(), mhdrFormatter.getResponseExpDatePattern());
    }

    public String getGestorIdFromMahindraResponse(@NotNull String logIdent, String authorizationcode) {
        StringBuilder gestorId = new StringBuilder();
        gestorConfig.getOperators().forEach((key, gestors) -> {
            GestorId gestorConf = gestors.stream()
                    .filter(gestor -> authorizationcode.startsWith(gestor.getPrefix()))
                    .findAny()
                    .orElse(null);

            if (null != gestorConf) {
                gestorId.append(gestorConf.getMappedValue());
                log.info("{} \"AuthorizationCodeFound\": \"{}\"}", logIdent, gestorConf);
            }
        });

        return gestorId.toString();
    }
}

