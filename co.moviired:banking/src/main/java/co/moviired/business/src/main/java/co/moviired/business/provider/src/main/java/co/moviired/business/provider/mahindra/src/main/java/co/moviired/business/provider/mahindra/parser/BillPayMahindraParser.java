package co.moviired.business.provider.mahindra.parser;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.enums.Seller;
import co.moviired.business.domain.enums.WeftType;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.mahindra.request.CommandBillPayRequest;
import co.moviired.business.provider.mahindra.response.CommandBillPayResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class BillPayMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private static final String SEPARATOR = "|";
    private static final String NOT_EXIT_CODE = "NOT EXIT CODE INTERNAL";
    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;
    private final MahindraProperties mahindraProperties;
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String validateEchoDataConectorCitibank(RequestFormatBanking req) throws DataException {
        if (bankingProperties.getGestorIdCitibank().equals(req.getGestorId())) {
            String transactionType;
            String[] partsEchoData = req.getEchoData().split("\\|");
            if (partsEchoData[1].equals("Y")) {
                transactionType = "A";
            } else if (partsEchoData[3].equals("Y")) {
                transactionType = "P";
            } else {
                StatusCode statusCode = statusCodeConfig.of("41");
                throw new DataException(statusCode.getCode(), statusCode.getMessage());
            }
            return "|".concat(partsEchoData[0]).concat("|").concat(transactionType).concat("|").concat(partsEchoData[4]);
        } else {
            return "";
        }
    }

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking req) throws DataException {
        // Datos especificos de la transaccion
        CommandBillPayRequest billPay = new CommandBillPayRequest();
        String additionalRemarks = validateEchoDataConectorCitibank(req);

        if (req.getAccountType() == null) {
            req.setAccountType("");
        }
        if (req.getWeftType().equals(WeftType.CONNECTOR)) {
            billPay.setEchodata(req.getEchoData());
        } else {
            billPay.setEchodata(req.getLastName() + "|" + this.mahindraProperties.getBpProcessCode() + "|" + req.getServiceCode());
        }

        String correlationIdRemarks = UtilHelper.stringNotNullOrNotEmpty(req.getCorrelationIdPortal()) ? req.getCorrelationIdPortal() : req.getCorrelationId();

        switch (req.getTypePayBill()) {
            case AUTOMATIC:
                //Datos para recaudo automatico
                if (req.getInternalCode() != null && !req.getInternalCode().equals(NOT_EXIT_CODE)) {
                    billPay.setEan13billercode(req.getEan13BillerCode());
                    billPay.setBillreferencenumber(req.getReferenceNumber() + "|" + req.getTypePayBill() + "|" + req.getInternalCode() + "|" + req.getSource());
                } else {
                    //CIPABEMO COMMAND NORMAL
                    billPay.setEan13billercode(req.getEan13BillerCode());
                    billPay.setBillreferencenumber(req.getReferenceNumber()); //ajuste para hacer respuesta
                    billPay.setEchodata(req.getEchoData());
                }
                break;

            case MANUAL:
                //Datos para recaudo manual
                if (req.getInternalCode() != null && !req.getInternalCode().equals(NOT_EXIT_CODE)) {
                    billPay.setBillercode(req.getServiceCode());
                    billPay.setShortreferencenumber(req.getReferenceNumber() + "|" + req.getTypePayBill() + "|" + req.getInternalCode() + "|" + req.getSource());
                } else {
                    //CIPABEMO COMMAND NORMAL
                    billPay.setBillercode(req.getServiceCode());
                    billPay.setShortreferencenumber(req.getReferenceNumber());
                    billPay.setEchodata(req.getEchoData());

                }
                break;

            default:
                if (req.getTypePayBillDeposit().equals("DEPOSIT")) {
                    //Datos para deposito
                    billPay.setBillercode(req.getServiceCode());
                    billPay.setShortreferencenumber(req.getReferenceNumber() + "|" + req.getAccountType());
                    break;
                } else {
                    log.error("Operación inválida");
                }
        }

        // Específicos
        billPay.setAmount(req.getAmount());
        billPay.setBname(req.getBillerName());

        // Usuario
        billPay.setMsisdn1(req.getMsisdn1());
        billPay.setMsisdn2(req.getMsisdn1());
        billPay.setMpin(req.getMpin());
        billPay.setPin(req.getMpin());
        billPay.setFtxnId(req.getCorrelationIdPortal());
        billPay.setRemarks(req.getMsisdn1() + "|" + req.getPosId() + "|" + req.getIssueDate() + "|" + req.getIssuerName() + "|" + correlationIdRemarks + additionalRemarks);
        billPay.setCellid(req.getTercId() + "|" + req.getHomologateBankId() + "|" + req.getAccountType());
        billPay.setSource(req.getSource());

        // Datos prestablecidos
        billPay.setType(this.mahindraProperties.getBpTypeSubscriber());
        if (req.getSource().equals(this.mahindraProperties.getBpCodigoChannel())) {
            billPay.setType(this.mahindraProperties.getBpTypeChannel());
        }
        billPay.setSubtype(this.mahindraProperties.getBpSubtype());
        billPay.setPayid(this.mahindraProperties.getBpPayId());
        billPay.setPaymentInstrument(this.mahindraProperties.getBpPaymentInstrument());
        billPay.setLanguage1(this.mahindraProperties.getBpLanguage1());
        billPay.setProvider(this.mahindraProperties.getBpProvider());
        billPay.setBprovider(this.mahindraProperties.getBpBprovider());
        billPay.setImei(generateImei(req));

        if (req.getServiceCode() != null && (req.getServiceCode().equals(bankingProperties.getBepsBillerCode()) || req.getServiceCode().equals(bankingProperties.getPaynetBillerCode()))) {
            billPay.setRequestorId(mahindraProperties.getBpRequestorId());
            if (req.getServiceCode().equals(bankingProperties.getPaynetBillerCode())) {
                billPay.setIspincheckreq(mahindraProperties.getBpIspincheckreq());
            }
        }

        return billPay;
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandBillPayResponse command = (CommandBillPayResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        response.setErrorCode(command.getTxnstatus());
        response.setErrorMessage(command.getMessage());
        response.setErrorType(ErrorType.PROCESSING.name());

        if ("200".equals(command.getTxnstatus())) {
            response.setAmount(Integer.parseInt(bankingRequest.getAmount().replace(".", "")));
            response.setReferenceNumber(bankingRequest.getReferenceNumber());
            response.setAuthorizationCode(command.getBanktransactionid());
            response.setTransferId(command.getTxnid());
            response.setGestorId(command.getBankid());
            response.setBillerName(bankingRequest.getBillerName());
            response.setErrorCode("00");
            response.setErrorType("OK");
            response.setCorrelationId(bankingRequest.getCorrelationId());
            response.setTransactionDate(fechaISO.format(new Date()));
        }
        return response;
    }

    private String generateImei(@NotNull RequestFormatBanking req) {
        if (req.getServiceCode() == null || !req.getServiceCode().equals(bankingProperties.getPaynetBillerCode()) &&
                (!req.getServiceCode().equals(bankingProperties.getBepsBillerCode()) || req.getSource().equals(Seller.SUBSCRIBER.name()))) {
            StringBuilder imei = new StringBuilder();
            imei.append("0");
            imei.append(SEPARATOR);
            imei.append(req.getIp());
            imei.append(SEPARATOR);
            imei.append(req.getSource());
            imei.append(SEPARATOR);
            imei.append(req.getCorrelationId());
            imei.append(SEPARATOR);
            imei.append(req.getComponentDate());
            imei.append(SEPARATOR);
            imei.append(SEPARATOR);
            imei.append(this.mahindraProperties.getDispositivoGetrax());
            imei.append(SEPARATOR);
            imei.append(req.getAgentCode());
            imei.append(SEPARATOR);
            imei.append(req.getMsisdn1());
            imei.append(SEPARATOR);
            imei.append(SEPARATOR);
            imei.append(req.getTercId());
            imei.append(SEPARATOR);
            imei.append(req.getHomologateBankId());
            imei.append(SEPARATOR);
            imei.append(req.getGestorId());
            imei.trimToSize();
            return imei.toString();
        } else {
            return "";
        }
    }

}
