package co.moviired.business.properties;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class IntegratorProperties implements Serializable {

    //DATOS DE INTEGRATOR - VALIDATEBILLPAYMENTBYEANCODE

    @Value("${integrador_validateBillPaymentByEANCode.urlTransactional}")
    private String urlIntegratorValidateByEANCode;

    @Value("${integrador_validateBillPaymentByEANCode.timeout.connection}")
    private int connectionTimeoutIntegratorValidateByEANCode;

    @Value("${integrador_validateBillPaymentByEANCode.timeout.read}")
    private int readTimeoutIntegratorValidateByEANCode;


    @Value("${properties.integrator.validateBillByEan.SALT}")
    private String saltEANCode;

    @Value("${properties.integrator.validateBillByEan.CHANEL}")
    private String chanelEANCode;

    @Value("${properties.integrator.validateBillByEan.ORIGIN_ADRESS}")
    private String originAdressEANCode;

    @Value("${properties.integrator.validateBillByEan.SYSTEM_ID}")
    private String systemIdEANCode;

    @Value("${properties.integrator.validateBillByEan.USERNAME}")
    private String userNameEANCode;

    @Value("${properties.integrator.validateBillByEan.PASSWORD}")
    private String passwordEANCode;

    @Value("${properties.integrator.validateBillByEan.CUSTOMER_ID}")
    private String customerIdEANCode;

    @Value("${properties.integrator.validateBillByEan.DEVICE_CODE}")
    private String deviceCodeEANCode;

    @Value("${properties.integrator.validateBillByEan.COLLECTION}")
    private String collectionAutomaticEANCode;

    //DATOS DE INTEGRATOR - VALIDATEBILLPAYMENTBYREFERENCE

    @Value("${integrador_validateBillPaymentByReference.urlTransactional}")
    private String urlIntegratorValidateByReference;

    @Value("${integrador_validateBillPaymentByReference.timeout.connection}")
    private int connectionTimeoutIntegratorValidateByReference;

    @Value("${integrador_validateBillPaymentByReference.timeout.read}")
    private int readTimeoutIntegratorValidateByReference;

    @Value("${properties.integrator.validateBillByReference.SALT}")
    private String saltReference;

    @Value("${properties.integrator.validateBillByReference.CHANEL}")
    private String chanelReference;

    @Value("${properties.integrator.validateBillByReference.ORIGIN_ADRESS}")
    private String originAdressReference;

    @Value("${properties.integrator.validateBillByReference.SYSTEM_ID}")
    private String systemIdReference;

    @Value("${properties.integrator.validateBillByReference.USERNAME}")
    private String userNameReference;

    @Value("${properties.integrator.validateBillByReference.PASSWORD}")
    private String passwordReference;

    @Value("${properties.integrator.validateBillByReference.CUSTOMER_ID}")
    private String customerIdReference;

    @Value("${properties.integrator.validateBillByReference.DEVICE_CODE}")
    private String deviceCodeReference;

    @Value("${properties.integrator.validateBillByReference.COLLECTION}")
    private String collectionManualReference;
}

