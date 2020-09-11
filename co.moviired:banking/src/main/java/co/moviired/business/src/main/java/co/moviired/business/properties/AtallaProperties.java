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
public class AtallaProperties implements Serializable {

    @Value("${banks.agrario.use_atalla}")
    private boolean useAtalla;

    @Value("${banks.agrario.atalla_ip}")
    private String atallaIp;

    @Value("${banks.agrario.atalla_port}")
    private String atallaPuerto;

    @Value("${banks.agrario.atalla_timeout}")
    private String atallaTimeout;

    @Value("${banks.agrario.comando_atalla}")
    private String comandoAtalla;

    @Value("${banks.agrario.atalla_codigo_exitoso}")
    private String atallaCodigoExitoso;

    @Value("${banks.agrario.bind_agrario}")
    private String bindAgrario;

    private String keinHeader;
    private String keinCryptogram;
    private String keinMac;


}

