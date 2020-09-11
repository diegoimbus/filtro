package co.moviired.support.domain.request.impl;


import co.moviired.support.domain.request.IConsignmentRegistry;
import lombok.Data;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
public class ConsignmentReject implements IConsignmentRegistry {


    private static final long serialVersionUID = 5662266870709046462L;

    private String correlationId;

    private String reason;

    private String usernamePortalAuthorizer;


}

