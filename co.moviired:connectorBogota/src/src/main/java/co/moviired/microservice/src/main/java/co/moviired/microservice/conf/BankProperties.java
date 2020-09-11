package co.moviired.microservice.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class BankProperties implements Serializable {

    //DATOS DE CONEXIÓN
    @Value("${client.bancoBogota.ip}")
    private String bogotaIP;

    @Value("${client.bancoBogota.port}")
    private String bogotaPort;

    @Value("${client.bancoBogota.timeout.connection}")
    private Integer bogotaTimeoutConnection;

    @Value("${client.bancoBogota.timeout.read}")
    private Integer bogotaTimeoutRead;

    //CODIGO POR TIPO DE PROCESO
    @Value("${properties.bogota.queryProcessCode}")
    private String queryProcessCode;

    @Value("${properties.bogota.payBillProcessCode}")
    private String payBillProcessCode;

    //PROPIEDADES GLOBALES
    @Value("${properties.bogota.global.gestorId}")
    private String gestorId;

    @Value("${properties.bogota.global.postEntryMode}")
    private String postEntryMode;

    @Value("${properties.bogota.global.acquiringInstCode}")
    private String acquiringInstCode;

    @Value("${properties.bogota.global.bogotaId}")
    private String bogotaId;

    @Value("${properties.bogota.global.movilRedId}")
    private String movilRedId;

    @Value("${properties.bogota.global.cvv}")
    private String cvv;

    @Value("${properties.bogota.global.trxCurrencyCode}")
    private String trxCurrencyCode;

    @Value("${properties.bogota.global.pin}")
    private String pin;

    @Value("${properties.bogota.global.terminalCoding}")
    private String terminalCoding;

    @Value("${properties.bogota.global.acquiringBank}")
    private String acquiringBank;

    @Value("${properties.bogota.global.clearingDate}")
    private String clearingDate;

    @Value("${properties.bogota.global.cityCountry}")
    private String cityCountry;

    @Value("${properties.bogota.global.movilRedAccount}")
    private String movilRedAccount;

    //PROPIEDADES CHANNEL
    @Value("${properties.bogota.channel.tercIdChannel}")
    private String tercIdChannel;

    //PROPIEDADES SUBSCRIBER
    @Value("${properties.bogota.subscriber.txSubscriber}")
    private boolean txSubscriber;

    @Value("${properties.bogota.subscriber.tercIdSubscriber}")
    private String tercIdSubscriber;

    @Value("${properties.bogota.subscriber.errorMessageSubscriberTx}")
    private String errorMessageSubscriberTx;

}

