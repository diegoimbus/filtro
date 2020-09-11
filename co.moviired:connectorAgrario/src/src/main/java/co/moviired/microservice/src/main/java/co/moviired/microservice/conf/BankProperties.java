package co.moviired.microservice.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BankProperties {

    //DATOS DE CONEXIÓN
    @Value("${client.bancoAgrario.ip}")
    private String agrarioIp;

    @Value("${client.bancoAgrario.port}")
    private String agrarioPort;

    @Value("${client.bancoAgrario.timeout.connection}")
    private Integer agrarioConnectionTimeout;

    @Value("${client.bancoAgrario.timeout.read}")
    private Integer agrarioReadTimeout;

    //PROPIEDADES GLOBALES
    @Value("${properties.agrario.global.gestorId}")
    private String gestorId;

    @Value("${properties.agrario.global.processingCode}")
    private String processingCode;

    @Value("${properties.agrario.global.inputMode1}")
    private String inputMode1;

    @Value("${properties.agrario.global.tradeCode}")
    private String tradeCode;

    @Value("${properties.agrario.global.trxId}")
    private String trxId;

    @Value("${properties.agrario.global.country}")
    private String country;

    @Value("${properties.agrario.global.location}")
    private String location;

    @Value("${properties.agrario.global.currencyCode}")
    private String currencyCode;

    @Value("${properties.agrario.global.correspondentAccount}")
    private String correspondentAccount;

    @Value("${properties.agrario.global.accountType}")
    private String accountType;

    @Value("${properties.agrario.global.deviceId}")
    private String deviceId;

    // PROPIEDADES DE CHANNEL
    @Value("${properties.agrario.channel.txWithoutHomologation}")
    private boolean txWithoutHomologation;

    @Value("${properties.agrario.channel.tercIdChannel}")
    private String tercIdChannel;

    @Value("${properties.agrario.channel.homologateBankChannel}")
    private String homologateBankChannel;

    // PROPIEDADES DE SUBSCRIBER
    @Value("${properties.agrario.subscriber.moviiTransactions}")
    private boolean moviiTransactions;

    @Value("${properties.agrario.subscriber.tercIdSubscriber}")
    private String tercIdSubscriber;

    @Value("${properties.agrario.subscriber.homologateBankSubscriber}")
    private String homologateBankSubscriber;

}

