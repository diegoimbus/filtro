package co.moviired.support.domain.request.impl;


import co.moviired.support.domain.entity.account.BarExemptionDays;
import co.moviired.support.domain.entity.account.UnbarBarHistory;
import co.moviired.support.domain.entity.redshift.Grade;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
public class Request implements Serializable {

    private static final long serialVersionUID = -5151290145486907425L;

    private Grade grade;
    private BarExemptionDays exemptionDay;
    private UnbarBarHistory history;

}

