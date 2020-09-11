package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.DigitalContentHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.DigitalContentHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.DigitalContentRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandDigitalContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class DigitalContentMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = 8488946390611766156L;


    public DigitalContentMahindraParser() {
        super();
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) {
        // Transformar al tipo específico de IHermes Request
        DigitalContentHermesRequest req = (DigitalContentHermesRequest) hermesRequest;
        DigitalContentRequest request = new DigitalContentRequest();

        request.setCorrelationId(req.getCorrelationId());
        request.setIssueDate(req.getIssueDate());
        request.setIssuerName(req.getIssuerName());
        request.setPhoneNumber(req.getPhoneNumber());
        request.setIp(req.getIp());
        request.setSource(req.getSource());
        request.setAmount(req.getAmount());
        request.setEanCode(req.getEanCode());
        request.setEmail(req.getEmail());
        request.setProductId(req.getProductId());
        request.setOperation(req.getOperation());
        request.setCardSerialNumber(req.getCardSerialNumber());
        request.setCorrelationIdR(req.getCorrelationIdR());

        return request;
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse pcommand) throws ParseException {
        // Transformar al command específico
        CommandDigitalContent command = (CommandDigitalContent) pcommand;

        // Código y mensaje de respuesta
        ResponseHermes resp = new ResponseHermes();
        resp.setStatusCode(command.getErrorCode());
        resp.setStatusMessage(command.getErrorMessage());

        // Armar el objeto respuesta
        DigitalContentHermesResponse response = new DigitalContentHermesResponse();
        response.setRequest(hermesRequest);
        response.setResponse(resp);

        if ("00".equals(resp.getStatusCode())) {
            // NO LLEGA EL TXNID EN LA RESPUESTA DEL INACTIVAR TARJETA, YA Q NO SE INVOCA A MAHINDRA SINO AL INTEGRADOR.
            String txnid = "000000000";
            if (command.getTransactionId() != null) {
                txnid = command.getTransactionId().toUpperCase();
            }
            String authorization = transformAuthorizationNumber(txnid.toUpperCase());
            response.setPin(command.getPin());
            response.setAgentCode(command.getAgentCode());
            response.setAmount(command.getAmount());
            response.setAuthorizationCode(authorization);
            response.setErrorCode(command.getErrorCode());
            response.setErrorMessage(command.getErrorMessage());
            response.setErrorType(command.getErrorType());
            response.setCashInId(command.getCashInId());
            response.setName(command.getName());
            response.setPhoneNumber(command.getPhoneNumber());
            response.setAuthorizationNumber(authorization);
            response.setTermAndConditions(command.getTermAndConditions());
            response.setTransactionDate(command.getTransactionDate());
            response.setTransactionId(command.getTransactionId());
            response.setTransactionTime(command.getTransactionTime());
            response.setInvoiceNumber(command.getInvoiceNumber());
        } else {
            String authorization = "0";
            if (command.getTransactionId() != null) {
                String txnid = command.getTransactionId().toUpperCase();
                authorization = transformAuthorizationNumber(txnid.toUpperCase());
            }
            response.setAuthorizationCode(authorization);
            response.setAuthorizationNumber(authorization);
            response.setErrorCode(command.getErrorCode());
            response.setErrorMessage(command.getErrorMessage());
        }
        response.setResponse(resp);

        return response;
    }
}

