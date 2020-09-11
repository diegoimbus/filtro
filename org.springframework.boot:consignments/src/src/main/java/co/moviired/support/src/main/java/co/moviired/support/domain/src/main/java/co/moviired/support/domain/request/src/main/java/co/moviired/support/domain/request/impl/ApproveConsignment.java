package co.moviired.support.domain.request.impl;

import co.moviired.support.domain.request.IApproveConsignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveConsignment implements IApproveConsignment {

    private static final long serialVersionUID = -1057243691217318721L;

    private String correlationId;
    private String usernamePortalAuthorizer;
}

