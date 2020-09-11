package co.moviired.digitalcontent.business.helper;

import co.moviired.base.domain.exception.ServiceException;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.domain.entity.IncommConfig;
import co.moviired.digitalcontent.business.domain.entity.PinHistory;
import co.moviired.digitalcontent.business.domain.entity.Product;
import co.moviired.digitalcontent.business.domain.repository.IPinHistoryRepository;
import co.moviired.digitalcontent.business.domain.repository.IProductRepository;
import co.moviired.digitalcontent.business.domain.repository.impl.SMSRepository;
import co.moviired.digitalcontent.business.domain.repository.impl.ZeusRepository;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.SQLException;

@Slf4j
@Component
public final class PinHelper implements Serializable {
    private static final long serialVersionUID = -3595590727813631179L;

    private static final String PIN = "#PIN#";
    private static final String MOVII = "SUBSCRIBER";

    private final IPinHistoryRepository pinHistoryRepository;
    private final SMSRepository smsRepository;
    private final ZeusRepository zeusRepository;
    private final IProductRepository productRepository;
    private final GlobalProperties globalProperties;

    public PinHelper(
            @NotNull IPinHistoryRepository ppinHistoryRepository,
            @NotNull SMSRepository psmsRepository,
            @NotNull IProductRepository pproductRepository,
            @NotNull GlobalProperties pglobalProperties,
            @NotNull ZeusRepository zeusRepository) {
        super();
        this.pinHistoryRepository = ppinHistoryRepository;
        this.smsRepository = psmsRepository;
        this.productRepository = pproductRepository;
        this.globalProperties = pglobalProperties;
        this.zeusRepository = zeusRepository;
    }


    public String desencriptarPinUsuario(String pinEncrypt) {
        return AESCrypt.decrypt(pinEncrypt);
    }

    public void encriptarPinUsuario(DigitalContentResponse resp, IncommConfig config) {
        try {
            if (config == null) {
                return;
            }

            String key = AESCrypt.decrypt(config.getEncryptionKey());
            if (config.isUseNewEncryptionAlgorithm()) {
                resp.setPin(AESGCMHelper.encrypt(resp.getPin(), key));
            } else {
                resp.setPin(EncriptPin.encrypt(resp.getPin(), key));
            }

        } catch (Exception e) {
            log.error("Error encriptando el pin. Causa: {}", e.getMessage());
        }
    }

    public void guardarHistorialPin(DigitalContentResponse resp, DigitalContentRequest data, IncommConfig config) {
        try {
            PinHistory pinHistory = new PinHistory();
            pinHistory.setEmail(data.getEmail());
            pinHistory.setCorrelativoId(data.getCorrelationId());
            pinHistory.setTransferId(resp.getTransactionId());
            pinHistory.setPhoneNumber(data.getPhoneNumber());
            pinHistory.setPin(resp.getPin());
            pinHistory.setAuthorizationCode(resp.getAuthorizationCode());

            pinHistory.setSendMail(config.isSendMail());
            pinHistory.setSendSms(config.isSendSMS());
            pinHistory.setAmount(data.getAmount());
            pinHistory.setIssueDate(data.getIssueDate());
            pinHistory.setPersonName(data.getPersonName());
            pinHistory.setEanCode(data.getEanCode());
            pinHistory.setAgentCode(data.getAgentCode());
            pinHistory.setSource(data.getSource());
            pinHistory.setTemplateSms(config.getTemplateSms());
            pinHistory.setTemplateMail(config.getTemplateMail());
            pinHistory.setSendResponse(Boolean.FALSE);

            if (config.isSendPin()) {
                pinHistory.setSendResponse(Boolean.TRUE);
            }

            this.pinHistoryRepository.save(pinHistory);

        } catch (Exception e) {
            log.error("<== No se guardo en BD el pin generado. Causa: {}", e.getMessage());
        }
    }

    public PinHistory findPin(String authorizationCode, String transferId) {
        PinHistory pinHistory = this.pinHistoryRepository.findPin(authorizationCode, transferId).orElse(null);

        if (pinHistory == null) {
            try {
                pinHistory = zeusRepository.findPin(authorizationCode, transferId);
            } catch (ServiceException | SQLException e) {
                log.error("Error finding pin in getrax for authorization code \"{}\" or transferId \"{}\": {}", authorizationCode, transferId, e.getMessage());
            }
        }

        return pinHistory;
    }

    public void notificarPin(DigitalContentResponse resp, DigitalContentRequest data, IncommConfig config, String pin) {
        asignarCorrelativo(data.getCorrelationId());

        if ((config.isSendMail() && data.getEmail() != null && !data.getEmail().isEmpty())) {
            notifyByEmail(resp, data, config, pin);
        } else {
            log.debug("<== No hay correo en los datos de entrada");
        }

        if ((config.isSendSMS()) && (data.getPhoneNumber() != null) && (!data.getPhoneNumber().equalsIgnoreCase("3000000000"))) {
            notifyBySms(data, config, pin);
        } else {
            log.debug("<== No hay PhoneNumber en los datos de entrada");
        }
    }

    public void notifyPin(String correlative, PinHistory pinHistory) {
        DigitalContentResponse resp = DigitalContentResponse.builder()
                .transactionId(pinHistory.getTransferId())
                .authorizationCode(pinHistory.getAuthorizationCode())
                .amount(pinHistory.getAmount())
                .build();

        DigitalContentRequest data = DigitalContentRequest.builder()
                .phoneNumber(pinHistory.getPhoneNumber())
                .email(pinHistory.getEmail())
                .correlationId(correlative)
                .issueDate(pinHistory.getIssueDate())
                .personName(pinHistory.getPersonName())
                .eanCode(pinHistory.getEanCode())
                .agentCode(pinHistory.getAgentCode())
                .source(pinHistory.getSource())
                .build();

        IncommConfig config = IncommConfig.builder()
                .sendMail(pinHistory.isSendMail())
                .sendSMS(pinHistory.isSendSms())
                .templateSms(pinHistory.getTemplateSms())
                .templateMail(pinHistory.getTemplateMail())
                .build();

        String pin = AESCrypt.decrypt(pinHistory.getPin());

        notificarPin(resp, data, config, pin);
    }

    private void notifyByEmail(DigitalContentResponse resp, DigitalContentRequest data, IncommConfig config, String pin) {
        log.info("==> Se enviará al correo el PIN");
        log.info("==> Correo: " + data.getEmail());
        log.info("==> Mensaje: " + config.getTemplateMail());

        config.setTemplateMail(config.getTemplateMail().replace(PIN, pin));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // Transaction data
        formData.add("txNumber", resp.getTransactionId());
        formData.add("datetime", data.getIssueDate());
        formData.add("approved", resp.getAuthorizationCode());
        formData.add("valor", resp.getAmount());
        formData.add("pin", pin);

        // user info
        formData.add("email", data.getEmail());
        formData.add("user_id", data.getPhoneNumber());
        formData.add("personName", data.getPersonName());

        // Product data
        Product product = this.productRepository.findTopByEanCodeStartingWith(data.getEanCode());
        formData.add("category", product.getCategory().getName());
        formData.add("product", product.getName());
        formData.add("agentCode", data.getAgentCode());

        // Invocar al servicio de envío de correo
        try {
            boolean isMovii = (MOVII.equals(data.getSource()));
            postEmail(formData, isMovii);
            log.info("<== Se ha enviado del mensaje EMAIL de forma satisfactoria");

        } catch (Exception e) {
            log.error("<== Error programando el envío del mensaje Email - Causa: " + e.getMessage());
        }
    }

    private void notifyBySms(DigitalContentRequest data, IncommConfig config, String pin) {
        log.info("==> Se enviará sms el PIN");
        log.info("==> telefono: " + data.getPhoneNumber());
        log.info("==> Mensaje: " + config.getTemplateSms().replace(PIN, "******"));
        config.setTemplateSms(config.getTemplateSms().replace(PIN, pin));
        try {
            this.smsRepository.sendSMS(data.getPhoneNumber(), config.getTemplateSms());
            log.info("<== Se ha programado el enviado del mensaje de forma satisfactoria");

        } catch (ServiceException e) {
            log.error("<== Error programando el envío del mensaje SMS. Causa: {}", e.getMessage());
        }
    }

    private void postEmail(MultiValueMap<String, String> map, boolean isMovii) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            // correo MOVii/MOViiRED
            String url = globalProperties.getUrlMail();
            if (isMovii) {
                url = globalProperties.getUrlMailMovii();
            }

            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString(), e);

            HttpHeaders respH = e.getResponseHeaders();
            if (respH != null) {
                log.info("Content-type = " + e.getResponseHeaders().getFirst("Content-Type"));
                log.info("Authorization = " + e.getResponseHeaders().getFirst("Authorization"));
                log.info("grant_type = " + e.getResponseHeaders().getFirst("grant_type"));
            }
        }
    }

    private void asignarCorrelativo(String correlation) {
        String cId = correlation;
        if (correlation == null || correlation.isEmpty()) {
            cId = Utils.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
    }

}

