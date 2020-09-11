package co.moviired.digitalcontent.incomm.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class IncommProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Datos de CONEXIÓN

    @Value("${client.timeout.read}")
    private Integer readTimeout;

    @Value("${properties.IC_ECHOTIME}")
    private Integer echoTime;

    // Variable para la reversión

    @Value("${properties.IC_TIMEOUT_REV}")
    private Integer revertTimeout;

    @Value("${properties.IC_REVERT_RETRIES}")
    private Integer revertRetries;

    @Value("${properties.IC_REVERT_DELAY}")
    private Integer revertDelay;

    // Variables petición

    @Value("${properties.IC_ISOFIELD22}")
    private String isoField22;

    @Value("${properties.IC_ISOFIELD49}")
    private String isoField49;

    @Value("${properties.IC_ACTPROCESSCODE}")
    private String processCodeActivation;

    @Value("${properties.IC_ACTPROCESSCODE_2}")
    private String processCodeActivation2;

    @Value("${properties.IC_DESACTPROCESSCODE}")
    private String processCodeDeactivation;

    @Value("${properties.IC_ACTINCOMM}")
    private String processCodeActivationIncomm;

    @Value("${properties.IC_DEACTINCOMM}")
    private String processCodeDeactivationIncomm;

    @Value("${properties.IC_ACTPROCESSCODE_PIN}")
    private String processCodeActivationPinIncomm;

    @Value("${properties.IC_DESACTPROCESSCODE_PIN}")
    private String processCodeDeactivationPinIncomm;

    // Variables de usuario y cliente (Moviired)

    @Value("${properties.IC_PININCOMM}")
    private String pinIncomm;

    @Value("${properties.IC_DEACTPININCOMM}")
    private String inactpinIncomm;


    @Value("${properties.IC_PINTANDC}")
    private String pinTandc;

    @Value("${properties.MERCHANT_RETAILER_ID}")
    private String merchantId;

}

