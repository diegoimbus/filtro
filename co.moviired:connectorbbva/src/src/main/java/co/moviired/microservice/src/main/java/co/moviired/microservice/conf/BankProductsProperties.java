package co.moviired.microservice.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class BankProductsProperties implements Serializable {

    //URL WS ENDPOINT SOAP
    @Value("${services.soap.enpoint.wsSeguridadBase}")
    private String wsSeguridadBase;

    @Value("${services.soap.enpoint.wsRecaudo}")
    private String wsRecaudo;

    @Value("${services.soap.enpoint.wsCargos}")
    private String wsCargos;

    @Value("${services.timeOut.read}")
    private String read;

    @Value("${services.timeOut.connect}")
    private String connect;


    //PROPIEDADES
    @Value("${bbva.properties.tipoIdentificacion}")
    private String tipoIdentificacion;

    @Value("${bbva.properties.numeroIdentificacion}")
    private String numeroIdentificacion;

    @Value("${bbva.properties.canal}")
    private String canal;

    @Value("${bbva.properties.codOperacionConsulta}")
    private String codOperacionConsulta;

    @Value("${bbva.properties.mecAutentConsulta}")
    private String mecAutentConsulta;

    @Value("${bbva.properties.codOperacionPago}")
    private String codOperacionPago;

    @Value("${bbva.properties.mecAutentPago}")
    private String mecAutentPago;

    @Value("${bbva.properties.codigoReferencia}")
    private String codigoReferencia;

    @Value("${bbva.properties.channel}")
    private String channel;

    @Value("${bbva.properties.subChannel}")
    private String subChannel;

    @Value("${bbva.properties.deviceType}")
    private String deviceType;

    @Value("${bbva.properties.version}")
    private String version;

    @Value("${bbva.properties.errorLanguage}")
    private String errorLanguage;

    @Value("${bbva.properties.terminalChannel}")
    private String terminalChannel;

    @Value("${bbva.properties.terminalSuscriber}")
    private String terminalSuscriber;

    @Value("${bbva.properties.subscriberId}")
    private String subscriberId;

    @Value("${bbva.properties.formaPago}")
    private String formaPago;

    @Value("${bbva.properties.cargoGeneradoStatus}")
    private String cargoGeneradoStatus;

    @Value("${bbva.properties.facturaValidadaStatus}")
    private String facturaValidadaStatus;

    @Value("${bbva.properties.gestorId}")
    private String gestorId;

    @Value("${bbva.properties.produccion}")
    private boolean produccion;


}

