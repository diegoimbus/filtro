package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class GlobalProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    // Documentos de identidad
    @Value("${properties.parameters.documentTypes.aliasCC}")
    private String aliasCC;

    @Value("${properties.parameters.documentTypes.descripcionCC}")
    private String descripcionCC;

    @Value("${properties.parameters.documentTypes.aliasCE}")
    private String aliasCE;

    @Value("${properties.parameters.documentTypes.descripcionCE}")
    private String descripcionCE;

    @Value("${properties.parameters.documentTypes.aliasPEP}")
    private String aliasPEP;

    @Value("${properties.parameters.documentTypes.descripcionPEP}")
    private String descripcionPEP;

    @Value("${properties.parameters.documentTypes.aliasPAS}")
    private String aliasPAS;

    @Value("${properties.parameters.documentTypes.descripcionPAS}")
    private String descripcionPAS;

    //Topes trimestral, semestral y anual
    @Value("${properties.parameters.tops.quarterly}")
    private int quarterly;

    @Value("${properties.parameters.tops.biannual}")
    private int biannual;

    @Value("${properties.parameters.tops.annual}")
    private int annual;

    //Topes por transaccion trimestral, semestral y anual
    @Value("${properties.parameters.tops.txnQuarterly}")
    private int txnQuarterly;

    @Value("${properties.parameters.tops.txnBiannual}")
    private int txnBiannual;

    @Value("${properties.parameters.tops.txnAnnual}")
    private int txnAnnual;

    //Mahindra Cologar giro
    @Value("${properties.parameters.mahindra.typePlaceGiro}")
    private String typePlaceGiro;

    //Mahindra pagar o retirar giro
    @Value("${properties.parameters.mahindra.typePayGiro}")
    private String typePayGiro;

    //Mahindra reversar giro
    @Value("${properties.parameters.mahindra.typeReverseGiro}")
    private String typeReverseGiro;

    //Mahindra Listado giro pendientes
    @Value("${properties.parameters.mahindra.typeListPendingGiro}")
    private String typeListPendingGiro;

    //Mahindra envio dinero cta a cta
    @Value("${properties.parameters.mahindra.typeRtmreqGiro}")
    private String typeRtmreqGiro;

    //Mahindra default user operations
    @Value("${properties.parameters.mahindra.userOperationGiro}")
    private String userOperationGiro;

    @Value("${properties.parameters.mahindra.passOpetationGiro}")
    private String passOpetationGiro;

    @Value("${properties.parameters.mahindra.sndIdTypeGiro}")
    private String sndIdTypeGiro;

    @Value("${properties.parameters.mahindra.sndIdnoGiro}")
    private String sndIdnoGiro;

    @Value("${properties.parameters.mahindra.sndNameGiro}")
    private String sndNameGiro;

    @Value("${properties.parameters.mahindra.sndMsisdnGiro}")
    private String sndMsisdnGiro;

    @Value("${properties.parameters.mahindra.providerGiro}")
    private String providerGiro;

    @Value("${properties.parameters.mahindra.payIdGiro}")
    private String payIdGiro;

    @Value("${properties.parameters.mahindra.languageGiro}")
    private String languageGiro;

    @Value("${properties.parameters.mahindra.emailDefault}")
    private String emailDefault;

    @Value("${properties.parameters.mahindra.userTypeAuth}")
    private String userTypeAuth;

    @Value("${properties.parameters.mahindra.numberAttempts}")
    private int numberAttempts;


    @Value("${properties.parameters.topAmount}")
    private Integer topAmount;

    @Value("${properties.parameters.typeSource}")
    private String typeSource;

}

