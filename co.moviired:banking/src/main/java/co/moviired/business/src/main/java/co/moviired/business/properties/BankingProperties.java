package co.moviired.business.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class BankingProperties implements Serializable {

    // PROPERTIES REFERENCE NUMBER
    @Value("${properties.referenceNumber.maxlengthManual}")
    private int maxlengthManual;

    // PROPERTIES AGREEMENTS
    @Value("${properties.agreements.soat.billerCode}")
    private String soatBillerCode;

    @Value("${properties.agreements.beps.billerCode}")
    private String bepsBillerCode;

    @Value("${properties.agreements.beps.deletePlaceChannel}")
    private String bepsDeletePlaceChannel;

    @Value("${properties.agreements.beps.deletePlaceSubscriber}")
    private String bepsDeletePlaceSubscriber;

    @Value("${properties.agreements.paynet.billerCode}")
    private String paynetBillerCode;

    @Value("${properties.agreements.paynet.deletePlaceReference}")
    private String paynetDeletePlaceReference;

    // DATOS DE BANKING MICROSERVICE - QUERY
    @Value("${bankingSwitch_query.urlTransactional}")
    private String urlBankingQuery;

    @Value("${bankingSwitch_query.timeout.connection}")
    private int connectionTimeoutBankingQuery;

    @Value("${bankingSwitch_query.timeout.read}")
    private int readTimeoutBankingQuery;

    // DATOS DE BANKING MICROSERVICE - CASHOUT
    @Value("${bankingSwitch_cashOut.urlTransactional}")
    private String urlBankingCashOut;

    @Value("${bankingSwitch_cashOut.timeout.connection}")
    private int connectionTimeoutBankingCashOut;

    @Value("${bankingSwitch_cashOut.timeout.read}")
    private int readTimeoutBankingCashOut;

    // INFORMATION CITIBANK
    @Value("${banks.citibank.gestorId}")
    private String gestorIdCitibank;

    // INFORMATION BOGOTA
    @Value("${banks.bogota.gestor_id}")
    private String gestorIdBogota;

    @Value("${banks.bogota.payment_bill_automatic}")
    private Boolean paymentBillAutomaticBogota;

    @Value("${banks.bogota.payment_bill_manual}")
    private Boolean paymentBillManualBogota;

    // INFORMATION BBVA
    @Value("${banks.bbva.app_key}")
    private String appKey;

    @Value("${banks.bbva.gestor_id}")
    private String gestorIdBBVA;

    @Value("${banks.bbva.payment_bill_automatic}")
    private Boolean paymentBillAutomaticBBVA;

    @Value("${banks.bbva.payment_bill_manual}")
    private Boolean paymentBillManualBBVA;

    // INFORMATION AGRARIO
    @Value("${banks.agrario.gestor_id}")
    private String gestorIdAgrario;

    @Value("${banks.agrario.payment_bill_automatic}")
    private Boolean paymentBillAutomaticAgrario;

    @Value("${banks.agrario.payment_bill_manual}")
    private Boolean paymentBillManualAgrario;

    @Value("${banks.bbva.BANKBBVA}")
    private String ciBankIdBbva;

    @Value("${banks.agrario.BANKAGRARIO}")
    private String ciBankIdAgrario;

}
