package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.conf.GeTraxProperties;
import co.moviired.transpiler.helper.SignatureBuilder;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByEanHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByEanHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.DataValidateBillByEan;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.Meta;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.RequestSignature;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandValidateBillByEan;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class ValidateBillByEanMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final GeTraxProperties geTraxProperties;

    public ValidateBillByEanMahindraParser(GeTraxProperties pgeTraxProperties) {
        super();
        this.geTraxProperties = pgeTraxProperties;
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) throws JsonProcessingException {
        // Transformar al tipo específico de IHermes Request
        ValidateBillByEanHermesRequest req = (ValidateBillByEanHermesRequest) hermesRequest;

        // META
        Meta meta = new Meta();
        meta.setChannel(req.getChannel());
        meta.setOriginAddress(req.getOriginAddress());
        meta.setRequestDate(req.getRequestDate());
        meta.setRequestReference(req.getRequestReference());
        meta.setSystemId(req.getSystemId());
        meta.setRequestSource(geTraxProperties.getVbeSource());
        meta.setUserName(geTraxProperties.getVbeUserName());
        meta.setPasswordHash(geTraxProperties.getVbePassword());
        meta.setCustomerId(geTraxProperties.getVbeCustomerId());
        meta.setDeviceCode(geTraxProperties.getVbeDevice());

        // DATA
        DataValidateBillByEan data = new DataValidateBillByEan();
        data.setEan128FullCode(req.getEan128FullCode());

        // Calcular la firma de la petición
        RequestSignature signature = new RequestSignature();
        String systemSignature = new SignatureBuilder()
                .append(meta)
                .append(data)
                .append(this.geTraxProperties.getVbeSalt())
                .build();
        signature.setSystemSignature(systemSignature);

        // Datos especificos de la transaccion
        co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByEan byEan = new co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByEan();
        byEan.setMeta(meta);
        byEan.setData(data);
        byEan.setRequestSignature(signature);

        return byEan;
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse pcommand) {
        // Transformar al command específico
        CommandValidateBillByEan command = (CommandValidateBillByEan) pcommand;

        // Código y mensaje de respuesta
        ResponseHermes resp = new ResponseHermes();
        resp.setStatusCode(command.getOutcome().getStatusCode());
        resp.setStatusMessage(command.getOutcome().getMessage());

        // Armar el objeto respuesta
        ValidateBillByEanHermesRequest billRequest = (ValidateBillByEanHermesRequest) hermesRequest;
        ValidateBillByEanHermesResponse billResponse = new ValidateBillByEanHermesResponse();
        billResponse.setRequest(billRequest);

        if (("200".equals(resp.getStatusCode())) && (command.getData() != null)) {
            billResponse.setBankId(command.getData().getBankId());
            billResponse.setBillDueDate(command.getData().getBillDueDate());
            billResponse.setBillerCode(command.getData().getBillerCode());
            billResponse.setEchoData(command.getData().getEchoData());
            billResponse.setMinPaymentValue(command.getData().getMinPaymentValue());
            billResponse.setMaxPaymentValue(command.getData().getMaxPaymentValue());
            billResponse.setBillReferenceNumber(command.getData().getBillReferenceNumber());
            billResponse.setEan13BillerCode(command.getData().getEan13BillerCode());
            billResponse.setValueToPay(command.getData().getValueToPay());
            billResponse.setMinPartialPayment(command.getData().getMinPartialPayment());
            billResponse.setHashEchoData(command.getData().getHashEchoData());
            billResponse.setPayAfterDueDate(command.getData().getPayAfterDueDate());
            billResponse.setBillerName(command.getData().getBillerName());
            billResponse.setPartialPayment(command.getData().getPartialPayment());
            billResponse.setHelpOnline(command.getData().getHelpOnline());
            billResponse.setMultiple(command.getData().getMultiple());
            billResponse.setLabelRef(command.getData().getLabelRef());
        }

        // Detalle del error
        resp.setErrorCode(command.getOutcome().getError().getErrorCode());
        resp.setErrorMessage(command.getOutcome().getError().getErrorMessage());
        billResponse.setResponse(resp);

        return billResponse;
    }
}

