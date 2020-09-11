package co.moviired.business.provider.bankingswitch.parser;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.jpa.getrax.repository.IKEYINFORepository;
import co.moviired.business.helper.AtallaHelper;
import co.moviired.business.helper.Encrypter;
import co.moviired.business.properties.AtallaProperties;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.bankingswitch.request.CashOutbankingRequest;
import co.moviired.business.provider.bankingswitch.response.CommandCashOutBankingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CashOutBankingParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private static final String SEPARATOR = "|";

    private final BankingProperties bankProperties;
    private final MahindraProperties mahindraProperties;
    private final IKEYINFORepository keyInfoRepository;
    private final AtallaProperties atallaProperties;

    public CashOutBankingParser(@NotNull BankingProperties bankProperties,
                                @NotNull MahindraProperties pmahindraProperties,
                                @NotNull IKEYINFORepository pkeyInfoRepository,
                                @NotNull AtallaProperties patallaProperties) {
        super();
        this.bankProperties = bankProperties;
        this.mahindraProperties = pmahindraProperties;
        this.keyInfoRepository = pkeyInfoRepository;
        this.atallaProperties = patallaProperties;

    }

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking cashOut) throws ProcessingException {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("referenceNumber", cashOut.getReferenceNumber());
        parameters.put("accountType", cashOut.getAccountType());
        parameters.put("typeDocument", cashOut.getTypeDocument());
        parameters.put("accountOrdinal", cashOut.getAccountOrdinal());
        parameters.put("valueToPay", cashOut.getAmount());
        parameters.put("tercId", cashOut.getTercId());
        parameters.put("upcId", cashOut.getUpcId());
        parameters.put("otp", cashOut.getOtp());
        parameters.put("correlationId", cashOut.getCorrelationId());
        parameters.put("serviceCode", cashOut.getServiceCode());
        parameters.put("imei", generateImei(cashOut));
        parameters.put("lastName", cashOut.getLastName());

        if (bankProperties.getGestorIdBBVA().equals(cashOut.getGestorId())) {
            try {
                cashOut.setOtp(Encrypter.encryptAES(cashOut.getOtp(), bankProperties.getAppKey()));
                parameters.put("otp", cashOut.getOtp());
            } catch (Exception ex) {
                throw new ProcessingException("-1", ex.getMessage());
            }
        } else if (bankProperties.getGestorIdAgrario().equals(cashOut.getGestorId())) {
            try {

                log.info("Consultando en base de datos KEYS ...");

                Map<String, Object> mapKEIN = keyInfoRepository.findAllByKetyName("KPE-PORTAL");
                final String keinMac = mapKEIN.get("KEIN_MAC").toString();
                final String keinHeader = mapKEIN.get("KEIN_HEADER").toString();
                final String keinCryptogram = mapKEIN.get("KEIN_CRYPTOGRAM").toString();

                log.info("KEYS encontradas => " + "KEIN_HEADER = " + keinHeader + "  KEIN_CRYPTOGRAM = " + keinCryptogram + " KEIN_MAC = " + keinMac);

                atallaProperties.setKeinMac(keinMac);
                atallaProperties.setKeinHeader(keinHeader);
                atallaProperties.setKeinCryptogram(keinCryptogram);

                cashOut.setOtp(AtallaHelper.encryptAtalla(cashOut.getReferenceNumber(), cashOut.getOtp(),
                        atallaProperties));
                parameters.put("otp", cashOut.getOtp());

            } catch (DataException | Exception ex) {
                throw new ProcessingException("-1", ex.getMessage());
            }
        }

        CashOutbankingRequest request = new CashOutbankingRequest();
        request.setData(parameters);

        return request;
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandCashOutBankingResponse command = (CommandCashOutBankingResponse) pcommand;
        // Armar el objeto respuesta
        Response response = new Response();

        String statusCodeQuery = command.getOutcome().getStatusCode().toString();
        String errorCodeQuery = command.getOutcome().getError().getErrorCode();


        if (("200").equals(statusCodeQuery) && ("00").equals(errorCodeQuery)) {

            response.setErrorCode("00");
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setAuthorizationCode(command.getData().getAuthorizationCode());
            response.setTransactionId(command.getData().getTransactionId());

        } else {

            String errorMessage = command.getOutcome().getError().getErrorMessage();
            String errorType = command.getOutcome().getError().getErrorType().toString();
            response.setErrorCode(errorCodeQuery);
            response.setErrorMessage(errorMessage);
            response.setErrorType(errorType);
        }

        return response;
    }

    private String generateImei(@NotNull RequestFormatBanking cashOut) {
        StringBuilder imei = new StringBuilder();
        imei.append("0");
        imei.append(SEPARATOR);
        imei.append(cashOut.getIp());
        imei.append(SEPARATOR);
        imei.append(cashOut.getSource());
        imei.append(SEPARATOR);
        imei.append(cashOut.getCorrelationId());
        imei.append(SEPARATOR);
        imei.append(cashOut.getComponentDate());
        imei.append(SEPARATOR);
        imei.append(SEPARATOR);
        imei.append(this.mahindraProperties.getDispositivoGetrax());
        imei.append(SEPARATOR);
        imei.append(cashOut.getAgentCode());
        imei.append(SEPARATOR);
        imei.append(cashOut.getMsisdn1());
        imei.append(SEPARATOR);
        imei.append(SEPARATOR);
        imei.append(cashOut.getTercId());
        imei.append(SEPARATOR);
        imei.append(cashOut.getHomologateBankId());
        imei.append(SEPARATOR);
        imei.append(cashOut.getGestorId());

        imei.trimToSize();
        return imei.toString();
    }

}



