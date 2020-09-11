package co.moviired.support.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "properties.command-consult-balance")
public class CmdConsultBalanceProperties implements Serializable {

    private String typeSubscriber;
    private String typeChannel;
    private String provider;
    private String payId;

}


