package co.moviired.topups.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Data
@ConfigurationProperties("properties.mahindra")
public class MahindraProperties implements Serializable {

    @Value("${properties.mahindra.urlTransactional}")
    private String urlTransactional;

    @Value("${properties.mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${properties.mahindra.timeout.read}")
    private int readTimeout;

    // Recharge
    @Value("${properties.mahindra.client.merchant}")
    private String clientMerchant;

    @Value("${properties.mahindra.client.subscriber}")
    private String clientSubscriber;

    @Value("${properties.mahindra.recharge.TYPESUBSCRIBER}")
    private String typeSubscriber;

    @Value("${properties.mahindra.recharge.TYPEMERCHANT}")
    private String typeMerchant;

    @Value("${properties.mahindra.recharge.PAYID2}")
    private String payid2;

    @Value("${properties.mahindra.recharge.PAYMENTTYPE}")
    private String paymenttype;

    @Value("${properties.mahindra.recharge.PAYID}")
    private String payid;

    @Value("${properties.mahindra.recharge.PROVIDER2}")
    private String provider2;

    @Value("${properties.mahindra.recharge.header.authSplitter}")
    private String authSplitter;

    @Value("${properties.mahindra.recharge.imeiPattern}")
    private String imeiPattern;

    @Value("${properties.mahindra.recharge.remarkPattern}")
    private String remarkPattern;

    @Value("${properties.mahindra.recharge.PROVIDER}")
    private String provider;

    @Value("${properties.mahindra.recharge.LANGUAGE1}")
    private String language;
}

