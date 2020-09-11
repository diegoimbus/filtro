package com.moviired.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */

@Data
@ConfigurationProperties(prefix = "properties.command-consult-balance")
public class CmdConsultBalanceProperties implements Serializable {

    private String typeSubscriber;
    private String typeChannel;
    private String provider;
    private String payId;


}


