package co.moviired.transpiler.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${mahindra.urlTransactional}")
    private String urlTransactional;

    @Value("${mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${mahindra.timeout.read}")
    private int readTimeout;

    // TOPUP

    @Value("${properties.mahindra.topUp.TYPE}")
    private String type;

    @Value("${properties.mahindra.topUp.PAYMENTTYPE}")
    private String paymentType;

    @Value("${properties.mahindra.topUp.PAYID}")
    private String payId;

    @Value("${properties.mahindra.topUp.PAYID2}")
    private String payId2;

    @Value("${properties.mahindra.topUp.LANGUAGE1}")
    private String language1;

    @Value("${properties.mahindra.topUp.PROVIDER}")
    private String provider;

    @Value("${properties.mahindra.topUp.PROVIDER2}")
    private String provider2;

    @Value("${properties.mahindra.topUp.PIN}")
    private String pin;

    @Value("${properties.mahindra.topUp.IMEI}")
    private String imei;

    @Value("${properties.mahindra.topUp.SOURCE}")
    private String source;

    // BILL_PAY PAYMENT

    @Value("${properties.mahindra.billPay.TYPE}")
    private String bpType;

    @Value("${properties.mahindra.billPay.SUBTYPE}")
    private String bpSubtype;

    @Value("${properties.mahindra.billPay.PAYID}")
    private String bpPayId;

    @Value("${properties.mahindra.billPay.PAYMENT_INSTTRUMENT}")
    private String bpPaymentInstrument;

    @Value("${properties.mahindra.billPay.PROVIDER}")
    private String bpProvider;

    @Value("${properties.mahindra.billPay.BPROVIDER}")
    private String bpBprovider;

    @Value("${properties.mahindra.billPay.LANGUAGE1}")
    private String bpLanguage1;

    @Value("${properties.mahindra.billPay.SOURCE}")
    private String bpSource;

    @Value("${properties.mahindra.billPay.IMEI}")
    private String bpImei;

}
