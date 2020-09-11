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
@ConfigurationProperties(prefix = "properties.command-approve-consignment")
public class CmdApproveConsignmentProperties implements Serializable {

    private static final long serialVersionUID = -5818221489111278213L;

    private String type;

}

