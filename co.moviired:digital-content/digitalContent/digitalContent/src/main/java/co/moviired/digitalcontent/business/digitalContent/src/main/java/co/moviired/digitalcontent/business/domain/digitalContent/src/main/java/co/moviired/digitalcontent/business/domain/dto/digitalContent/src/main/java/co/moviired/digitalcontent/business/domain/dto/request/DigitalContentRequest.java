package co.moviired.digitalcontent.business.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class DigitalContentRequest implements Serializable {

    private static final Long MULTIPLE = 10000L;

    private String phoneNumber;

    private String source;

    private String network;

    private String mpin;

    private String usuario;

    private String amount;

    private String correlationId;

    private String correlationIdR;

    private String issueDate;

    private String issuerLogin;

    private String transactionId;

    private String otp;

    private String agentCode;

    private String posId;

    private String email;

    private String eanCode;

    private String productId;

    private String cardSerialNumber; // Card Number

    private String operation;

    private String msisdn1;

    private String incommCode;

    private String ip;

    private String personName;

    private String operatingSystem;

    private String version;

}

