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
import co.moviired.digitalcontent.business.exception.EncryptionException;
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

@Slf4j
@Component
public final class PinHelper implements Serializable {
    private static final long serialVersionUID = -3595590727813631179L;

    private static final String PIN = "#PIN#";
    private static final String MOVII = "SUBSCRIBER";

    private final IPinHistoryRepository pinHistoryRepository;
    private final SMSRepository smsRepository;
    private final IProductRepository productRepository;
    private final GlobalProperties globalProperties;

    public PinHelper(
            @NotNull IPinHistoryRepository pinHistoryRepository,
            @NotNull SMSRepository smsRepository,
            @NotNull IProductRepository productRepository,
            @NotNull GlobalProperties globalProperties) {
        super();
        this.pinHistoryRepository = pinHistoryRepository;
        this.smsRepository = smsRepository;
        this.productRepository = productRepository;
        this.globalProperties = globalProperties;
    }


    public String desencriptarPinUsuario(String pinEncrypt) throws EncryptionException {
        return AESCrypt.decrypt(pinEncrypt);
    }

    public void encriptarPinUsuario(DigitalContentResponse resp, IncommConfig config) {
        try {
            if (config == null) {
                return;
            }

            String key = AESCrypt.decrypt(config.getEncryptionKey());
            resp.setPin(EncriptPin.encrypt(resp.getPin(), key));

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
            pinHistory.setSendResponse(Boolean.FALSE);

            if (config != null && config.isSendPin()) {
                pinHistory.setSendResponse(Boolean.TRUE);
            }

            this.pinHistoryRepository.save(pinHistory);

        } catch (Exception e) {
            log.error("<== No se guardo en BD el pin generado. Causa: {}", e.getMessage());
        }
    }

    public void notificarPin(DigitalContentResponse resp, DigitalContentRequest data, IncommConfig config) {
        asignarCorrelativo(data.getCorrelationId());

        if ((config != null) && (config.isSendMail() && (data.getEmail() != null) && (!data.getEmail().isEmpty()))) {
            log.info("==> Se enviará al correo el PIN");
            log.info("==> Correo: " + data.getEmail());
            log.info("==> Mensaje: " + config.getTemplateMail());

            config.setTemplateMail(config.getTemplateMail().replace(PIN, resp.getPin()));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            // Transaction data
            formData.add("txNumber", resp.getTransactionId());
            formData.add("datetime", data.getIssueDate());
            formData.add("approved", resp.getAuthorizationCode());
            formData.add("valor", resp.getAmount());
            formData.add("pin", resp.getPin());

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

        } else {
            log.debug("<== No hay correo en los datos de entrada");
        }

        if (config != null && config.isSendSMS() && (data.getPhoneNumber() != null) && (!data.getPhoneNumber().equalsIgnoreCase("3000000000"))) {
            log.info("==> Se enviará sms el PIN");
            log.info("==> telefono: " + data.getPhoneNumber());
            log.info("==> Mensaje: " + config.getTemplateSms().replace(PIN, "******"));
            config.setTemplateSms(config.getTemplateSms().replace(PIN, resp.getPin()));
            try {
                this.smsRepository.sendSMS(data.getPhoneNumber(), config.getTemplateSms());
                log.info("<== Se ha programado el enviado del mensaje de forma satisfactoria");

            } catch (ServiceException e) {
                log.error("<== Error programando el envío del mensaje SMS. Causa: {}", e.getMessage());
            }
        } else {
            log.debug("<== No hay PhoneNumber en los datos de entrada");
        }

        if (config != null && config.isSendPin()) {
            log.debug("<== Se enviará el PIN en la respuesta del servicio");
        } else {
            resp.setPin(null);
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
            cId = Utilidades.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
    }

}

