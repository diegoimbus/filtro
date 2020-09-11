package com.moviired.manager;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import com.moviired.client.supportotp.Request;
import com.moviired.client.supportotp.Response;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.model.dto.OtpDTO;
import com.moviired.properties.SupportOTPProperties;
import com.moviired.properties.SupportSmsProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class InternalOtpManager implements IOtpManager {

    private static final String SOURCE = "srv-cash";

    private final ReactiveConnector supportOtpClient;
    private final SupportOTPProperties supportOTPProperties;
    private final SupportSmsProperties supportSmsProperties;
    private final CryptoHelper cryptoHelperOtp;
    private final StatusCodeConfig statusCodeConfig;

    /**
     * metodo genetate (invoca support Otp para generar la otp)
     *
     * @param user
     * @return Response
     */
    @Override
    public Response generate(@NotNull OtpDTO user) throws ServiceException {
        // Establecer el template del SMS, según el source
        String template = this.supportSmsProperties.getOtpMoviiTemplate();
        Response response;
        // Obtener el endpoint a invocar
        String url = supportOTPProperties.getPathGenerateOTP(SOURCE, user.getPhoneNumber(), user.getEmail(), template);
        // Variables del SMS
        Map<String, String> variables = new HashMap<>();
        variables.put("NOMBRE", user.getName());
        variables.put("EXPIRATION", supportOTPProperties.getOtpExpiration().toString());
        // Generar los params de la peticion
        Request request = Request.builder()
                .email(user.getEmail())
                .otpAlphanumeric(supportOTPProperties.isOtpAlpha())
                .sendSms(user.getSendSms())
                .otpLength(user.getOtpLength())
                .otpExpiration(user.getTimeExpired())
                .templateCode(template)
                .variables(variables)
                .build();

        // Invocar al servicio
        log.info("Request Support Otp :{} ", request);
        response = (Response) supportOtpClient.post(url, request, Response.class, MediaType.APPLICATION_JSON, null).block();

        if (response.getResponseCode() == null) {
            throw new ServiceException(ErrorType.PROCESSING, "500", "No se obtuvo respuesta desde support-otp");
        }

        return validateResponse(response);
    }

    /**
     * metodo isValid (Invoca support-otp para validar una otp)
     *
     * @param phoneNumber,otp
     * @return Response
     */
    @Override
    public Response isValid(String phoneNumber, String otp) throws ServiceException {
        String url = supportOTPProperties.getPathValidateOTP(SOURCE, phoneNumber, cryptoHelperOtp.decoder(otp));
        Response resp = (Response) supportOtpClient.post(url, "", Response.class, MediaType.APPLICATION_JSON, null).block();

        if (resp.getResponseCode() == null) {
            throw new ServiceException(ErrorType.PROCESSING, "500", "No se obtuvo respuesta desde support-otp");
        }

        return validateResponse(resp);
    }

    /**
     * metodo resend (Invoca support-otp para reenviar la otp a un usuario)
     *
     * @param phoneNumber,notifyChannel
     * @return void
     */
    @Override
    public void resend(String phoneNumber, String notifyChannel) {
        String url = supportOTPProperties.getPathResendOTP(SOURCE, phoneNumber, notifyChannel);
        log.info("resendOTP Request: {URL: '{}', source: '{}', phone_number: '{}', notify_channel: '{}'}", supportOTPProperties.getUrl(), SOURCE, phoneNumber, notifyChannel);

        // Invocar al servicio
        try {
            supportOtpClient.exchange(HttpMethod.PUT, url, "", Response.class, MediaType.APPLICATION_JSON, null).block();
            log.info("Otp reenviada exitosamente {}", phoneNumber);
        } catch (Exception e) {
            log.error("Error generando OTP {}", e.getMessage());
        }
    }


    /**
     * metodo validateResponse (valida respuesta del support-otp)
     *
     * @param response
     * @return Response
     */
    private Response validateResponse(Response response) {

        log.info("Response Support-otp {}", response);

        StatusCode statusCode = statusCodeConfig.of(response.getResponseCode(), response.getResponseMessage());
        statusCode.setCode(statusCode.getExtCode());

        if (StatusCode.Level.FAIL.equals(statusCode.getLevel()) && !"99".equals(statusCode.getCode())) {
            response.setResponseCode(statusCodeConfig.of(response.getResponseCode(), StatusCode.Level.FAIL).getCode());
            response.setResponseMessage(statusCodeConfig.of(response.getResponseCode(), StatusCode.Level.FAIL).getMessage());
        }

        return response;
    }
}




