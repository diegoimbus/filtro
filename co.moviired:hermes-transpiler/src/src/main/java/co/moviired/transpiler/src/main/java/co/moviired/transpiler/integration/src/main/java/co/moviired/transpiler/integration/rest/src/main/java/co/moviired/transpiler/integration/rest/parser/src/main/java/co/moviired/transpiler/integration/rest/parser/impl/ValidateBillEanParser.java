package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.common.response.DataValidateByEan;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.integration.rest.dto.validatebill.request.RequestValidateBillByEanDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.response.ResponseValidateBillByEanDTO;
import co.moviired.transpiler.jpa.movii.domain.Biller;
import co.moviired.transpiler.jpa.movii.domain.BillerCategory;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.ValidateBillByEanHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByEanHermesResponse;
import co.moviired.transpiler.jpa.movii.repository.IBillerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@Service
public class ValidateBillEanParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    private static final int EAN13_BEGIN_INDEX = 4;
    private static final int EAN13_FINAL_INDEX = EAN13_BEGIN_INDEX + 13 - 1;

    // Repositories
    private final IBillerRepository billerRepository;

    // Repositories

    public ValidateBillEanParser(IBillerRepository pbillerRepository) {
        super();
        this.billerRepository = pbillerRepository;
    }

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            RequestValidateBillByEanDTO billReq = new ObjectMapper().readValue(request, RequestValidateBillByEanDTO.class);

            // Cliente (Usuario y Clave)
            String customerId = billReq.getMeta().getCustomerId();
            String userREQ = billReq.getMeta().getUserName();
            if (userREQ == null) {
                userREQ = "";
            }
            String passREQ = billReq.getMeta().getPasswordHash();
            if (passREQ == null) {
                passREQ = "";
            }
            ClientHermes clientHermes = new ClientHermes("", userREQ, passREQ, "");

            // Información del BILLER
            String ean13BillerCode = billReq.getData().getEan128FullCode();
            ean13BillerCode = ean13BillerCode.substring(EAN13_BEGIN_INDEX, EAN13_FINAL_INDEX);
            String name = "";
            String id = "0";
            BillerCategory category = null;
            Biller biller = this.billerRepository.getByEanCode(ean13BillerCode);
            if (biller != null) {
                id = biller.getId().toString();
                name = biller.getName();
                category = biller.getCategory();
                ean13BillerCode = biller.getEanCode();
            }
            BillerHermes billerHermes = new BillerHermes(id, name, category, null, ean13BillerCode, null, null);

            // Armar el HermesRequest
            ValidateBillByEanHermesRequest bill = new ValidateBillByEanHermesRequest();
            bill.setOriginalRequest(request);
            bill.setClient(clientHermes);
            bill.setBiller(billerHermes);

            // Datos especificos de la transaccion: META
            bill.setCustomerId(customerId);
            bill.setPosId(billReq.getMeta().getMac());
            bill.setRequestDate(billReq.getMeta().getRequestDate());
            bill.setCustomerId(billReq.getMeta().getCustomerId());
            bill.setChannel(billReq.getMeta().getChannel());
            bill.setSystemId(billReq.getMeta().getSystemId());
            bill.setOriginAddress(billReq.getMeta().getOriginAddress());
            bill.setDeviceCode(billReq.getMeta().getDeviceCode());
            bill.setRequestReference(billReq.getMeta().getRequestReference());
            bill.setRequestSource(billReq.getMeta().getRequestSource());

            // Datos especificos de la transaccion: DATA
            bill.setEan128FullCode(billReq.getData().getEan128FullCode());

            // Datos especificos de la transaccion: SIGNATURE
            bill.setSystemSignature(billReq.getRequestSignature().getSystemSignature());

            return bill;

        } catch (IOException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Obtener el DTO de la respuesta
            ValidateBillByEanHermesResponse response = (ValidateBillByEanHermesResponse) hermesResponse;

            // Estado de la transacción
            DataValidateByEan data = null;
            Outcome outcome = new Outcome();
            outcome.setStatusCode(response.getResponse().getStatusCode());
            outcome.setMessage(response.getResponse().getStatusMessage());

            // Armar el ERROR
            Error error = new Error();
            error.setErrorType("0");
            if ((response.getResponse().getErrorCode() != null) && (!response.getResponse().getErrorCode().trim().isEmpty())) {
                error.setErrorCode(response.getResponse().getErrorCode());
                error.setErrorMessage(response.getResponse().getErrorMessage());
            } else {
                error.setErrorCode(response.getResponse().getStatusCode());
                error.setErrorMessage(response.getResponse().getStatusMessage());
            }
            outcome.setError(error);

            // Si la respuesta es OK, armar el data
            if (outcome.getStatusCode().equals("200")) {
                data = new DataValidateByEan();
                data.setBillDueDate(response.getBillDueDate());
                data.setBillerCode(response.getBillerCode());
                data.setEchoData(response.getEchoData());
                data.setMinPaymentValue(response.getMinPaymentValue());
                data.setMaxPaymentValue(response.getMaxPaymentValue());
                data.setBankId(response.getBankId());
                data.setValueToPay(response.getValueToPay());
                data.setPartialPayment(response.getPartialPayment());
                data.setBillReferenceNumber(response.getBillReferenceNumber());
                data.setMinPartialPayment(response.getMinPartialPayment());
                data.setEan13BillerCode(response.getEan13BillerCode());
                data.setBillerName(response.getBillerName());
                data.setPayAfterDueDate(response.getPayAfterDueDate());
                data.setHashEchoData(response.getHashEchoData());
                data.setMultiple(response.getMultiple());
                data.setHelpOnline(response.getHelpOnline());
                data.setLabelRef(response.getLabelRef());

                // Establecer el código respuesta 00 al cliente
                error.setErrorCode("00");
            }

            // Armar la respuesta completa
            ResponseValidateBillByEanDTO responseDTO = new ResponseValidateBillByEanDTO();
            responseDTO.setOutcome(outcome);
            responseDTO.setData(data);

            return new ObjectMapper().writeValueAsString(responseDTO);

        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

}

