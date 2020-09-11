package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.common.response.DataValidateByReference;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.integration.rest.dto.validatebill.request.RequestValidateBillByReferenceDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.response.ResponseValidateBillByReferenceDTO;
import co.moviired.transpiler.jpa.movii.domain.Biller;
import co.moviired.transpiler.jpa.movii.domain.BillerCategory;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByReferenceHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByReferenceHermesResponse;
import co.moviired.transpiler.jpa.movii.repository.IBillerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class ValidateBillByRefenceParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Repositories
    private final IBillerRepository billerRepository;

    public ValidateBillByRefenceParser(IBillerRepository pbillerRepository) {
        super();
        this.billerRepository = pbillerRepository;
    }

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            RequestValidateBillByReferenceDTO validateBillReq = new ObjectMapper().readValue(request, RequestValidateBillByReferenceDTO.class);

            // Cliente (Usuario y Clave)
            String validateBillCustomerId = validateBillReq.getMeta().getCustomerId();
            String validateBillUserREQ = validateBillReq.getMeta().getUserName();
            String validateBillPassREQ = validateBillReq.getMeta().getPasswordHash();
            if (validateBillUserREQ == null) {
                validateBillUserREQ = "";
            }
            if (validateBillPassREQ == null) {
                validateBillPassREQ = "";
            }
            ClientHermes clientHermes = new ClientHermes("", validateBillUserREQ, validateBillPassREQ, "");

            // Información del BILLER
            String validateBillCode = validateBillReq.getData().getBillerCode();
            String validateBillName = "";
            String validateBillId = "0";
            BillerCategory validateBillCategory = null;
            Biller validateBiller = this.billerRepository.getByBillerCode(validateBillCode);
            if (validateBiller != null) {
                validateBillId = validateBiller.getId().toString();
                validateBillName = validateBiller.getName();
                validateBillCategory = validateBiller.getCategory();
            }
            BillerHermes validateBillerHermes = new BillerHermes(validateBillId, validateBillName, validateBillCategory, validateBillCode, null, null, null);

            // Armar el HermesRequest
            ValidateBillByReferenceHermesRequest validateBill = new ValidateBillByReferenceHermesRequest();
            validateBill.setOriginalRequest(request);
            validateBill.setClient(clientHermes);
            validateBill.setBiller(validateBillerHermes);

            // Datos especificos de la transaccion: META
            validateBill.setCustomerId(validateBillCustomerId);
            validateBill.setPosId(validateBillReq.getMeta().getMac());
            validateBill.setRequestDate(validateBillReq.getMeta().getRequestDate());
            validateBill.setChannel(validateBillReq.getMeta().getChannel());
            validateBill.setSystemId(validateBillReq.getMeta().getSystemId());
            validateBill.setOriginAddress(validateBillReq.getMeta().getOriginAddress());
            validateBill.setDeviceCode(validateBillReq.getMeta().getDeviceCode());
            validateBill.setRequestReference(validateBillReq.getMeta().getRequestReference());
            validateBill.setRequestSource(validateBillReq.getMeta().getRequestSource());

            // Datos especificos de la transaccion: DATA
            validateBill.setBillerCode(validateBillReq.getData().getBillerCode());
            validateBill.setShortReferenceNumber(validateBillReq.getData().getShortReferenceNumber());
            validateBill.setValueToPay(validateBillReq.getData().getValueToPay());

            // Datos especificos de la transaccion: SIGNATURE
            validateBill.setSystemSignature(validateBillReq.getRequestSignature().getSystemSignature());

            return validateBill;

        } catch (IOException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Obtener el DTO de la respuesta
            ValidateBillByReferenceHermesResponse validateBillResponse = (ValidateBillByReferenceHermesResponse) hermesResponse;

            // Estado de la transacción
            DataValidateByReference validateBillData = null;
            Outcome validateBillOutcome = new Outcome();
            validateBillOutcome.setStatusCode(validateBillResponse.getResponse().getStatusCode());
            validateBillOutcome.setMessage(validateBillResponse.getResponse().getStatusMessage());

            // Armar el ERROR
            Error validateBillError = new Error();
            validateBillError.setErrorType("0");
            if ((validateBillResponse.getResponse().getErrorCode() != null) && (!validateBillResponse.getResponse().getErrorCode().trim().isEmpty())) {
                validateBillError.setErrorCode(validateBillResponse.getResponse().getErrorCode());
                validateBillError.setErrorMessage(validateBillResponse.getResponse().getErrorMessage());
            } else {
                validateBillError.setErrorCode(validateBillResponse.getResponse().getStatusCode());
                validateBillError.setErrorMessage(validateBillResponse.getResponse().getStatusMessage());
            }
            validateBillOutcome.setError(validateBillError);

            // Si la respuesta es OK, armar el data
            if (validateBillOutcome.getStatusCode().equals("200")) {
                validateBillData = new DataValidateByReference();
                validateBillData.setBillDueDate(validateBillResponse.getBillDueDate());
                validateBillData.setTransactionCode(validateBillResponse.getTransactionCode());
                validateBillData.setEanCode(validateBillResponse.getEanCode());
                validateBillData.setShortReferenceNumber(validateBillResponse.getShortReferenceNumber());
                validateBillData.setBillerCode(validateBillResponse.getBillerCode());
                validateBillData.setDate(String.valueOf(new Date().getTime()));
                validateBillData.setHashEchoData(validateBillResponse.getHashEchoData());
                validateBillData.setAuthorizationCode(validateBillResponse.getAuthorizationCode());
                validateBillData.setUserId(validateBillResponse.getUserId());
                validateBillData.setPosId(validateBillResponse.getPosId());
                validateBillData.setMultiple(validateBillResponse.getMultiple());
                validateBillData.setEchoData(validateBillResponse.getEchoData());
                validateBillData.setMinPaymentValue(validateBillResponse.getMinPaymentValue());
                validateBillData.setResponseCode(validateBillResponse.getResponseCode());
                validateBillData.setAuthExternalCode(validateBillResponse.getAuthExternalCode());
                validateBillData.setProductCode(validateBillResponse.getProductCode());
                validateBillData.setMaxPaymentValue(validateBillResponse.getMaxPaymentValue());
                validateBillData.setBankId(validateBillResponse.getBankId());
                validateBillData.setValueToPay(validateBillResponse.getValueToPay());
                validateBillData.setPartialPayment(validateBillResponse.getPartialPayment());
                validateBillData.setTransactionType(validateBillResponse.getTransactionType());
                validateBillData.setProcessCode(validateBillResponse.getProcessCode());
                validateBillData.setDevice(validateBillResponse.getDevice());

                // Establecer el código respuesta 00 al cliente
                validateBillError.setErrorCode("00");
            }

            // Armar la respuesta completa
            ResponseValidateBillByReferenceDTO validateBillResponseDTO = new ResponseValidateBillByReferenceDTO();
            validateBillResponseDTO.setOutcome(validateBillOutcome);
            validateBillResponseDTO.setData(validateBillData);

            return new ObjectMapper().writeValueAsString(validateBillResponseDTO);

        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

}

