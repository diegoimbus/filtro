package co.moviired.microservice.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BankProperties {

    // CONNECTION
    @Value("${client.citiBank.url}")
    private String urlConnection;

    @Value("${client.citiBank.timeout.connection}")
    private int connectiontimeout;

    @Value("${client.citiBank.timeout.read}")
    private int readTimeout;

    @Value("${client.citiBank.delayReverse}")
    private long delayReverse;

    // PRODUCTS
    @Value("${properties.citiBank.products.debtRequest}")
    private String debtRequest;

    @Value("${properties.citiBank.products.debtPayment}")
    private String debtPayment;

    // GLOBAL
    @Value("${properties.citiBank.global.gestorId}")
    private String gestorId;

    @Value("${properties.citiBank.global.countryCode}")
    private String countryCode;

    @Value("${properties.citiBank.global.paymentCurrency}")
    private String paymentCurrency;

    // PROPERTIES PAYBILL
    @Value("${properties.citiBank.global.payBill.branch}")
    private String branch;

    @Value("${properties.citiBank.global.payBill.useAdditionalText}")
    private String useAdditionalText;

    @Value("${properties.citiBank.global.payBill.collectionItemsQuantity}")
    private String collectionItemsQuantity;

    @Value("${properties.citiBank.global.payBill.paymentInstrumentsQuantity}")
    private String paymentInstrumentsQuantity;

    @Value("${properties.citiBank.global.payBill.paymentInstrumentType}")
    private String paymentInstrumentType;

    // PROPERTIES CHANNEL
    @Value("${properties.citiBank.channel.txAvailable}")
    private boolean txAvailableChannel;

    @Value("${properties.citiBank.channel.networkExtensionCode}")
    private String networkExtensionCodeChannel;

    // PROPERTIES SUBSCRIBER
    @Value("${properties.citiBank.subscriber.txAvailable}")
    private boolean txAvailableSubscriber;

    @Value("${properties.citiBank.subscriber.networkExtensionCode}")
    private String networkExtensionCodeSubscriber;

}

