package co.moviired.microservice.conf;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class BankProductsProperties implements Serializable {

    //DATOS DE CONEXIÓN
    @Value("${client.bancoBogota.ip}")
    private String bogotaIP;

    @Value("${client.bancoBogota.port}")
    private String bogotaPort;

    @Value("${client.bancoBogota.timeout.connection}")
    private Integer bogotaTimeout;

    //PROPIEDADES
    @Value("${properties.query.processCode}")
    private String processCodeQuery;

    @Value("${properties.payBill.processCode}")
    private String processCodeDeposit;

    @Value("${properties.payBill.tercIdMahindra}")
    private String tercIdPayBill;

    @Value("${bogota.properties.gestorId}")
    private String gestorId;

    @Value("${bogota.properties.postEntryMode}")
    private String postEntryMode;

    @Value("${bogota.properties.acquiringInstCode}")
    private String acquiringInstCode;

    @Value("${bogota.properties.bogotaId}")
    private String bogotaId;

    @Value("${bogota.properties.movilRedId}")
    private String movilRedId;

    @Value("${bogota.properties.cvv}")
    private String cvv;

    @Value("${bogota.properties.cityCountry}")
    private String cityCountry;

    @Value("${bogota.properties.trxCurrencyCode}")
    private String trxCurrencyCode;

    @Value("${bogota.properties.pin}")
    private String pin;

    @Value("${bogota.properties.terminalCoding}")
    private String terminalCoding;

    @Value("${bogota.properties.clearingDate}")
    private String clearingDate;

    @Value("${bogota.properties.acquiringBank}")
    private String acquiringBank;

    @Value("${bogota.properties.movilRedAccount}")
    private String movilRedAccount;

    @Value("${bogota.properties.moviiTransactions}")
    private String moviiTransactions;

    @Value("${bogota.properties.errorMessageMoviiTx}")
    private String errorMessageMoviiTx;

}

