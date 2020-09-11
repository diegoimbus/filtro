package co.moviired.transpiler.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class GeTraxProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${getrax.urlTransactional}")
    private String urlTransactional;

    @Value("${getrax.methods.urlValidateBillPaymentByReference}")
    private String urlValidateBillPaymentByReference;

    @Value("${getrax.methods.validateBillPaymentByEANCode}")
    private String validateBillPaymentByEANCode;

    @Value("${getrax.timeout.connection}")
    private int connectionTimeout;

    @Value("${getrax.timeout.read}")
    private int readTimeout;

    // VALIDATE BILL_PAY PAYMENT BY REFERENCE

    @Value("${properties.getrax.validateBillByReference.CUSTOMER_ID}")
    private String vbrCustomerId;

    @Value("${properties.getrax.validateBillByReference.USERNAME}")
    private String vbrUserName;

    @Value("${properties.getrax.validateBillByReference.PASSWORD}")
    private String vbrPassword;

    @Value("${properties.getrax.validateBillByReference.DEVICE_CODE}")
    private String vbrDevice;

    @Value("${properties.getrax.validateBillByReference.IMEI}")
    private String vbrImei;

    @Value("${properties.getrax.validateBillByReference.SALT}")
    private String vbrSalt;


    // VALIDATE BILL_PAY PAYMENT BY EAN CODE

    @Value("${properties.getrax.validateBillByEan.CUSTOMER_ID}")
    private String vbeCustomerId;

    @Value("${properties.getrax.validateBillByEan.USERNAME}")
    private String vbeUserName;

    @Value("${properties.getrax.validateBillByEan.PASSWORD}")
    private String vbePassword;

    @Value("${properties.getrax.validateBillByEan.DEVICE_CODE}")
    private String vbeDevice;

    @Value("${properties.getrax.validateBillByEan.IMEI}")
    private String vbeSource;

    @Value("${properties.getrax.validateBillByEan.SALT}")
    private String vbeSalt;

}

