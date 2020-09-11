package co.moviired.topups.mahindra.parser;

import co.moviired.topups.exception.ParseException;
import co.moviired.topups.model.domain.dto.mahindra.ICommandRequest;
import co.moviired.topups.model.domain.dto.mahindra.ICommandResponse;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationHeaderRequest;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationRequest;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationResponse;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
public interface IMahindraParser extends Serializable {

    ICommandRequest parseRequest(@NotNull String logIdent, @NotNull IRechargeIntegrationRequest rechargeIntegrationRequest, @NotNull IRechargeIntegrationHeaderRequest rechargeHeaderRequest) throws ParseException;

    IRechargeIntegrationResponse parseResponse(@NotNull String logIdent,
                                               @NotNull ICommandRequest iCommandMahindraRequest,
                                               @NotNull ICommandResponse iCommandMahindraResponse,
                                               String... extraFields) throws ParseException;

}

