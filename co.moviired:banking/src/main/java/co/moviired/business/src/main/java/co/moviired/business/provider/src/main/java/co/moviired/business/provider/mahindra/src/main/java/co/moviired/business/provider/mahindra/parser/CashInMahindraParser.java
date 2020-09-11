package co.moviired.business.provider.mahindra.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.business.provider.IParser;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.IResponse;
import co.moviired.business.provider.mahindra.request.CommandCashInRequest;
import co.moviired.business.provider.mahindra.response.CommandCashInResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class CashInMahindraParser implements IParser {

    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;
    private final MahindraProperties mahindraProperties;

    public CashInMahindraParser(@NotNull MahindraProperties pmahindraProperties,
                                @NotNull StatusCodeConfig statusCodeConfig,
                                @NotNull BankingProperties pbankingProperties) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.statusCodeConfig = statusCodeConfig;
        this.bankingProperties = pbankingProperties;
    }

    @Override
    public final IRequest parseRequest(@NotNull RequestFormatBanking req, Response resp) {

        // Datos especificos de la transaccion
        CommandCashInRequest cashIn = new CommandCashInRequest();

        // Específicos
        cashIn.setAmount(req.getAmount());
        cashIn.setCellid(req.getTercId() + "|" + req.getHomologateBankId() + "|" + req.getAccountType());
        cashIn.setRemarks(req.getIssuerId() + "|" + req.getPosId() + "|" + req.getIssueDate() + "|" + req.getIssuerName() + "|" + req.getServiceCode() + "|" + req.getReferenceNumber() + "|" + req.getUpcId());
        cashIn.setFtxnId(req.getCorrelationId());

        if (this.bankingProperties.getGestorIdAgrario().equals(req.getGestorId())) {
            cashIn.setBankId(this.bankingProperties.getCiBankIdAgrario());

        } else if (this.bankingProperties.getGestorIdBBVA().equals(req.getGestorId())) {
            cashIn.setBankId(this.bankingProperties.getCiBankIdBbva());
        }

        cashIn.setBlocksms(this.mahindraProperties.getCiBlockSMS());
        cashIn.setReferenceId(resp.getTransactionId());
        cashIn.setTxnMode(this.mahindraProperties.getCiTxnMode());

        // Usuario
        cashIn.setMsisdn(req.getMsisdn1());

        // Datos prestablecidos
        cashIn.setType(this.mahindraProperties.getCiType());

        return cashIn;
    }

    @Override
    public final Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandCashInResponse command = (CommandCashInResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        response.setErrorCode(command.getTxnstatus());


        if ("200".equals(command.getTxnstatus())) {
            response.setErrorCode("00");
            response.setAmount(Integer.parseInt(command.getAmount().replace(".", "")));
            response.setTransferId(command.getTxnid());
            response.setErrorType("");
            response.setErrorMessage("OK");
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus());
            String mensajeRespuesta = statusCode.getMessage();
            response.setErrorMessage(mensajeRespuesta);
        }

        return response;
    }

}

