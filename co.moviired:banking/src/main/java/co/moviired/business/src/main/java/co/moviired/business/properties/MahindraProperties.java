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
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
//@Configuration
@Component

public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${mahindra.urlTransactional}")
    private String urlTransactional;

    @Value("${mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${mahindra.timeout.read}")
    private int readTimeout;

    @Value("${properties.mahindra.dispositivoGetrax}")
    private String dispositivoGetrax;


    // BILL_PAY PAYMENT

    @Value("${properties.mahindra.billPay.PROCESSCODE}")
    private String bpProcessCode;


    @Value("${properties.mahindra.billPay.CHANNEL.TYPE}")
    private String bpTypeChannel;

    @Value("${properties.mahindra.billPay.CHANNEL.CODIGO}")
    private String bpCodigoChannel;

    @Value("${properties.mahindra.billPay.SUBSCRIBER.TYPE}")
    private String bpTypeSubscriber;

    @Value("${properties.mahindra.billPay.SUBSCRIBER.CODIGO}")
    private String bpCodigoSubscriber;

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

    @Value("${properties.mahindra.billPay.REQUESTORID}")
    private String bpRequestorId;

    @Value("${properties.mahindra.billPay.ISPINCHECKREQ}")
    private String bpIspincheckreq;

    // CASHIN

    @Value("${properties.mahindra.cashIn.TYPE}")
    private String ciType;

    @Value("${properties.mahindra.cashIn.BLOCKSMS}")
    private String ciBlockSMS;

    @Value("${properties.mahindra.cashIn.TXNMODE}")
    private String ciTxnMode;

    @Value("${bankingSwitch_cashOut.attempts}")
    private Integer attempts;

    // LOGIN USER

    @Value("${properties.mahindra.loginService.TYPE}")
    private String lsType;

    @Value("${properties.mahindra.loginService.OTPREQ}")
    private String lsOtpreq;

    @Value("${properties.mahindra.loginService.ISPINCHECKREQ}")
    private String lsIsPINCheckReq;

    @Value("${properties.mahindra.loginService.SOURCE}")
    private String lsSource;

    @Value("${properties.mahindra.loginService.PROVIDER}")
    private String lsProvider;

    //QUERY BILL BATCH

    @Value("${properties.mahindra.queryBillBatch.TYPE}")
    private String qbbType;

    @Value("${properties.mahindra.queryBillBatch.PROVIDER}")
    private String qbbProvider;

    @Value("${properties.mahindra.queryBillBatch.BLOCKSMS}")
    private String qbbBlockSms;

    @Value("${properties.mahindra.queryBillBatch.LANGUAGE1}")
    private String qbbLanguage1;

    @Value("${properties.mahindra.queryBillBatch.CELLID}")
    private String qbbCellId;

    @Value("${properties.mahindra.queryBillBatch.PAYMENT_INSTRUMENT}")
    private String qbbPaymentInstrument;

    @Value("${properties.mahindra.queryBillBatch.SOURCE}")
    private String qbbSource;

    //PAY BILL BATCH

    @Value("${properties.mahindra.payBillBatch.TYPE}")
    private String pbbType;

    @Value("${properties.mahindra.payBillBatch.PROVIDER}")
    private String pbbProvider;

    @Value("${properties.mahindra.payBillBatch.PAYMENT_INSTRUMENT}")
    private String pbbPaymentInstrument;

    @Value("${properties.mahindra.payBillBatch.PAYID}")
    private String pbbPayId;

    @Value("${properties.mahindra.payBillBatch.LANGUAGE1}")
    private String pbbLanguage1;

    @Value("${properties.mahindra.payBillBatch.REFNO}")
    private String pbbRefno;

}
