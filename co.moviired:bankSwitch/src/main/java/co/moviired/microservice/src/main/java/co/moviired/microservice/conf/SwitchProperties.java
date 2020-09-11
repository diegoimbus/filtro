package co.moviired.microservice.conf;
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
import java.util.Map;

@Data
@Component
public class SwitchProperties implements Serializable {

    /**** SOCKET SWITCH ***/

    // DATOS DE CONEXIÓN

    @Value("${client.socket.ip}")
    private String socketIP;
    @Value("${client.socket.port}")
    private Integer socketPuerto;

    // TIMEOUTS
    @Value("${client.socket.timeout.connection}")
    private Integer conexionTimeout;
    @Value("${client.socket.timeout.read}")
    private Integer peticionTimeout;

    private Map<String, String> errors;

    // DATOS DEL CIENTE PARA DEPOSITO AGRARIO

    @Value("${properties.deposit.txType}")
    private String txType;

    @Value("${properties.deposit.idTransaction}")
    private String idTransaction;

    // DATOS DEL CIENTE PARA CONSULTA BBVA

    @Value("${properties.query.txType}")
    private String txTypeQuery;

    @Value("${properties.query.processCode}")
    private String processCodeQuery;

    @Value("${properties.query.idTransaction}")
    private String idTransactionQuery;

    @Value("${properties.query.userNameQuery}")
    private String userNameQuery;

    @Value("${properties.query.tercIDMahindra}")
    private String tercIDMahindra;

    // DATOS DEL CIENTE PARA RETIRO BBVA

    @Value("${properties.cashOut.txType}")
    private String txTypeCashOut;

    @Value("${properties.cashOut.processCode}")
    private String processCodeCashOut;

    @Value("${properties.cashOut.idTransaction}")
    private String idTransactionCashOut;

}

