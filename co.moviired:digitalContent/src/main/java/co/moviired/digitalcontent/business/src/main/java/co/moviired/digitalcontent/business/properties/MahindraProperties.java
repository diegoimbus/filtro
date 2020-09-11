package co.moviired.digitalcontent.business.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB

    @Value("${clients.mahindra.urlTransactional}")
    private String urlTransactional;

    @Value("${clients.mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${clients.mahindra.timeout.read}")
    private int readTimeout;

    @Value("${clients.mahindra.header.authSplitter}")
    private String authSplitter;

    // PROPERTIES

    @Value("${properties.mahindra.PAYID}")
    private String payId;

    @Value("${properties.mahindra.PAYMENT_INSTRUMENT}")
    private String paymentInstrument;

    @Value("${properties.mahindra.PROVIDER}")
    private String provider;

    @Value("${properties.mahindra.PROVIDER2}")
    private String provider2;

    @Value("${properties.mahindra.LANGUAGE1}")
    private String language1;

    @Value("${properties.mahindra.BLOCKSMS}")
    private String blockSMS;

    // MERCHART
    // PINES
    @Value("${properties.mahindra.merchant.pines.TYPE}")
    private String typePinMerchant;
    @Value("${properties.mahindra.merchant.pines.SUBTYPE}")
    private String subTypePinMerchant;

    // CARD ACTIVATE
    @Value("${properties.mahindra.merchant.card.TYPE}")
    private String typeCardMerchant;
    @Value("${properties.mahindra.merchant.card.SUBTYPE}")
    private String subTypeCardMerchant;

    // SUBCRIBER
    // PINES
    @Value("${properties.mahindra.subcriber.pines.TYPE}")
    private String typePinSubcriber;
    @Value("${properties.mahindra.subcriber.pines.SUBTYPE}")
    private String subTypePinSubcriber;

    // CARD ACTIVATE
    @Value("${properties.mahindra.subcriber.card.TYPE}")
    private String typeCardSubcriber;
    @Value("${properties.mahindra.subcriber.card.SUBTYPE}")
    private String subTypeCardSubcriber;

    //REVERSIONES
    @Value("${properties.mahindra.reversion.TYPE}")
    private String reversionType;
    @Value("${properties.mahindra.reversion.IS_TCP_CHECK_REQ}")
    private String reversionIsTcpCheckReq;


    // AUTENTICACIONES

    @Value("${properties.mahindra.autenticacion.NAME_AUTHPINREQ}")
    private String nameAuthpinreq;

    @Value("${properties.mahindra.autenticacion.OTPREQ}")
    private String otpReq;

    @Value("${properties.mahindra.autenticacion.ISPINCHECKREQ}")
    private String isPinCheckReq;

    @Value("${properties.mahindra.autenticacion.SOURCE}")
    private String source;

    @Value("${properties.mahindra.autenticacion.PROVIDERAUTH}")
    private String providerAuth;

    // REVERSO

    @Value("${clients.mahindra.reverse.excecute}")
    private Boolean reverse;

    @Value("${clients.mahindra.reverse.retries}")
    private Integer retries;

    @Value("${clients.mahindra.reverse.delay}")
    private Integer delay;
}

