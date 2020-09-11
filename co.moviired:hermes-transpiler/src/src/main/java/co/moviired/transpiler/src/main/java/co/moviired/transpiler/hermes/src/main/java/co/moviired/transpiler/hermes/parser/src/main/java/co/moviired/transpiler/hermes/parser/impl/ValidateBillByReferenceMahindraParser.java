package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.conf.GeTraxProperties;
import co.moviired.transpiler.helper.SignatureBuilder;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByReferenceHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByReferenceHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.DataValidateBillByReference;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.Meta;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.RequestSignature;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandValidateBillByReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class ValidateBillByReferenceMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final GeTraxProperties geTraxProperties;

    public ValidateBillByReferenceMahindraParser(GeTraxProperties pgeTraxProperties) {
        super();
        this.geTraxProperties = pgeTraxProperties;
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) throws JsonProcessingException {
        // Transformar al tipo específico de IHermes Request
        ValidateBillByReferenceHermesRequest req = (ValidateBillByReferenceHermesRequest) hermesRequest;

        // META
        Meta meta = new Meta();
        meta.setChannel(req.getChannel());
        meta.setOriginAddress(req.getOriginAddress());
        meta.setRequestDate(req.getRequestDate());
        meta.setRequestReference(req.getRequestReference());
        meta.setSystemId(req.getSystemId());
        meta.setRequestSource(geTraxProperties.getVbrImei());
        meta.setUserName(geTraxProperties.getVbrUserName());
        meta.setPasswordHash(geTraxProperties.getVbrPassword());
        meta.setCustomerId(geTraxProperties.getVbrCustomerId());
        meta.setDeviceCode(geTraxProperties.getVbrDevice());

        // DATA
        DataValidateBillByReference data = new DataValidateBillByReference();
        data.setBillerCode(req.getBillerCode());
        data.setShortReferenceNumber(req.getShortReferenceNumber());
        data.setValueToPay(req.getValueToPay());

        // Calcular la firma de la petición
        RequestSignature signature = new RequestSignature();
        String systemSignature = new SignatureBuilder()
                .append(meta)
                .append(data)
                .append(this.geTraxProperties.getVbrSalt())
                .build();
        signature.setSystemSignature(systemSignature);

        // Datos especificos de la transaccion
        co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByReference byReference = new co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByReference();
        byReference.setMeta(meta);
        byReference.setData(data);
        byReference.setRequestSignature(signature);

        return byReference;
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse pcommand) {
        // Transformar al command específico
        CommandValidateBillByReference command = (CommandValidateBillByReference) pcommand;

        // Código y mensaje de respuesta
        ResponseHermes resp = new ResponseHermes();
        resp.setStatusCode(command.getOutcome().getStatusCode());
        resp.setStatusMessage(command.getOutcome().getMessage());

        // Armar el objeto respuesta
        ValidateBillByReferenceHermesRequest billRequest = (ValidateBillByReferenceHermesRequest) hermesRequest;
        ValidateBillByReferenceHermesResponse billResponse = new ValidateBillByReferenceHermesResponse();
        billResponse.setRequest(billRequest);

        if (("200".equals(resp.getStatusCode())) && (command.getData() != null)) {
            billResponse.setBankId(command.getData().getBankId());
            billResponse.setBillDueDate(command.getData().getBillDueDate());
            billResponse.setBillerCode(command.getData().getBillerCode());
            billResponse.setEchoData(command.getData().getEchoData());
            billResponse.setMinPaymentValue(command.getData().getMinPaymentValue());
            billResponse.setMaxPaymentValue(command.getData().getMaxPaymentValue());
            billResponse.setShortReferenceNumber(command.getData().getShortReferenceNumber());
            billResponse.setValueToPay(command.getData().getValueToPay());
            billResponse.setTransactionCode(command.getData().getTransactionCode());
            billResponse.setEanCode(command.getData().getEanCode());
            billResponse.setDate(command.getData().getDate());
            billResponse.setHashEchoData(command.getData().getEchoData());
            billResponse.setAuthorizationCode(command.getData().getAuthorizationCode());
            billResponse.setMinValueToPay(command.getData().getMinValueToPay());
            billResponse.setUserId(command.getData().getUserId());
            billResponse.setPosId(command.getData().getPosId());
            billResponse.setMultiple(command.getData().getMultiple());
            billResponse.setResponseCode(command.getData().getResponseCode());
            billResponse.setAuthExternalCode(command.getData().getAuthExternalCode());
            billResponse.setProductCode(command.getData().getProductCode());
            billResponse.setPartialPayment(command.getData().getPartialPayment());
            billResponse.setTransactionType(command.getData().getTransactionType());
            billResponse.setProcessCode(command.getData().getProcessCode());
            billResponse.setDevice(command.getData().getDevice());
            billResponse.setLabelRef(command.getData().getLabelRef());
        }

        // Detalle del error
        resp.setErrorCode(command.getOutcome().getError().getErrorCode());
        resp.setErrorMessage(command.getOutcome().getError().getErrorMessage());
        billResponse.setResponse(resp);

        return billResponse;
    }
}

