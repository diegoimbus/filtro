package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.billpay.request.RequestBillPayDTO;
import co.moviired.transpiler.integration.rest.dto.billpay.response.Data;
import co.moviired.transpiler.integration.rest.dto.billpay.response.ResponseBillPayDTO;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.jpa.getrax.domain.Agreement;
import co.moviired.transpiler.jpa.getrax.repository.IAgreementRepository;
import co.moviired.transpiler.jpa.movii.domain.Biller;
import co.moviired.transpiler.jpa.movii.domain.BillerCategory;
import co.moviired.transpiler.jpa.movii.domain.User;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.BillPayHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.BillPayHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.repository.IBillerRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BillPayParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Repositories
    private final IUserRepository userRepository;
    private final IBillerRepository billerRepository;
    private final IAgreementRepository agreementRepository;

    public BillPayParser(IUserRepository puserRepository, IBillerRepository pbillerRepository, IAgreementRepository pagreementRepository) {
        super();
        this.userRepository = puserRepository;
        this.billerRepository = pbillerRepository;
        this.agreementRepository = pagreementRepository;
    }

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            RequestBillPayDTO billPayReq = new ObjectMapper().readValue(request, RequestBillPayDTO.class);

            // Cliente (Usuario y Clave)
            String billPayClientName = "";
            String billPayUserREQ = billPayReq.getMeta().getUserName();
            String billPayPassREQ = billPayReq.getMeta().getPasswordHash();
            String billPayTimeZone = "";
            if (billPayUserREQ == null) {
                billPayUserREQ = "";
            }

            if (billPayPassREQ == null) {
                billPayPassREQ = "";
            }
            Optional<User> billPayUser = userRepository.findByGetraxUsername(AESCrypt.crypt(billPayUserREQ));
            if (billPayUser.isPresent()) {
                User bPayUser = billPayUser.get();
                // Verificar: clave y estado del usuario y del cliente
                if ((bPayUser.getGetraxPassword().equals(AESCrypt.crypt(billPayPassREQ)))
                        && (bPayUser.getStatus().equals(GeneralStatus.ENABLED))
                        && (bPayUser.getClient().getStatus().equals(GeneralStatus.ENABLED))) {

                    billPayUserREQ = AESCrypt.decrypt(bPayUser.getMahindraUsername());
                    billPayPassREQ = AESCrypt.decrypt(bPayUser.getMahindraPassword());
                    billPayPassREQ = billPayPassREQ.replaceFirst(bPayUser.getId().toString(), "");
                    billPayClientName = bPayUser.getClient().getName();
                    billPayTimeZone = bPayUser.getClient().getTimeZone();
                }
            }
            ClientHermes billPayClientHermes = new ClientHermes(billPayClientName, billPayUserREQ, billPayPassREQ, billPayTimeZone);

            // Información del BILLER
            String billPayCode = billPayReq.getData().getBillerCode();
            String billPayEean13BillerCode = billPayReq.getData().getEan13BillerCode();
            String billPayName = "";
            String billPayId = "0";
            BillerCategory billPayCategory = null;
            String billPayProductCode = "0";
            String billPayProductDescription = "";

            // Si se encontró información del biller: MOVII o GeTrax
            Biller biller = this.getBiller(billPayCode, billPayEean13BillerCode);
            if (biller != null) {
                billPayId = (biller.getId() != null) ? biller.getId().toString() : "0";
                billPayName = biller.getName();
                billPayCategory = biller.getCategory();
                billPayCode = biller.getBillerCode();
                billPayEean13BillerCode = biller.getEanCode();
                billPayProductCode = biller.getProductCode();
                billPayProductDescription = biller.getProductDescription();
            }
            BillerHermes billerHermes = new BillerHermes(billPayId, billPayName, billPayCategory, billPayCode, billPayEean13BillerCode, billPayProductCode, billPayProductDescription);

            // Armar el HermesRequest
            BillPayHermesRequest bill = new BillPayHermesRequest();
            bill.setOriginalRequest(request);
            bill.setClient(billPayClientHermes);
            bill.setBiller(billerHermes);

            // Datos especificos de la transaccion
            bill.setDeviceCode(billPayReq.getMeta().getDeviceCode());
            bill.setCustomerId(billPayReq.getMeta().getUserName());
            bill.setRequestDate(billPayRestParseDate(billPayReq.getMeta().getRequestDate()));
            bill.setEchoData(billPayReq.getData().getEchoData());
            bill.setShortReferenceNumber(billPayReq.getData().getShortReferenceNumber());
            bill.setBillReferenceNumber(billPayReq.getData().getBillReferenceNumber());
            bill.setClientTxnId(billPayReq.getMeta().getRequestReference());

            // Valor a pagar
            String valueToPay = billPayReq.getData().getValueToPay();
            if (valueToPay != null) {
                bill.setAmount(Integer.parseInt(valueToPay));
            }

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
            BillPayHermesResponse billPayResponse = (BillPayHermesResponse) hermesResponse;

            // Estado de la transacción
            Data billPayData = null;
            Outcome billPayOutcome = new Outcome();
            billPayOutcome.setStatusCode(billPayResponse.getResponse().getStatusCode());
            billPayOutcome.setMessage(billPayResponse.getResponse().getStatusMessage());

            // Armar el ERROR
            Error billPayError = new Error();
            billPayError.setErrorType("0");
            if ((billPayResponse.getResponse().getErrorCode() != null) && (!billPayResponse.getResponse().getErrorCode().trim().isEmpty())) {
                billPayError.setErrorCode(billPayResponse.getResponse().getErrorCode());
                billPayError.setErrorMessage(billPayResponse.getResponse().getErrorMessage());
            } else {
                billPayError.setErrorCode(billPayResponse.getResponse().getStatusCode());
                billPayError.setErrorMessage(billPayResponse.getResponse().getStatusMessage());
            }
            billPayOutcome.setError(billPayError);

            // Si la respuesta es OK, armar el detalle
            if (billPayError.getErrorCode().equals("200")) {
                billPayData = new Data();
                billPayData.setBillerCode(billPayResponse.getBillercode());
                billPayData.setBankId(billPayResponse.getBankid());
                billPayData.setValueToPay(billPayResponse.getValuetopay());
                billPayData.setDevice(billPayResponse.getDevice());
                billPayData.setChargeValue(billPayResponse.getChargevalue());
                billPayData.setTransactionId(billPayResponse.getTransactionid());
                billPayData.setCommisionValue(billPayResponse.getCommisionvalue());
                billPayData.setBankTransactionId(billPayResponse.getBanktransactionid());
                billPayData.setPosId(billPayResponse.getRequest().getCustomerId());
                billPayData.setDate(String.valueOf(new Date().getTime()));
                billPayData.setEanCode(billPayResponse.getRequest().getBiller().getEanBillerCode());

                // Recaudo manual
                if (billPayResponse.getRequest().getShortReferenceNumber() != null) {
                    billPayData.setShortReferenceNumber(billPayResponse.getShortreferencenumber());
                } else {
                    // Recaudo automático
                    billPayData.setBillReferenceNumber(billPayResponse.getShortreferencenumber());
                }

                // No se tienen mapeado de dónde tomarlos. Se responden en vacío
                billPayData.setProductCode("");
                billPayData.setTransactionCode("");
                billPayData.setProcessCode("");
                billPayData.setConvCodigoInterno("");

                // Establecer el código respuesta 00 al cliente
                billPayError.setErrorCode("00");
            }

            // Armar la respuesta completa
            ResponseBillPayDTO billPayResponseDTO = new ResponseBillPayDTO();
            billPayResponseDTO.setOutcome(billPayOutcome);
            billPayResponseDTO.setData(billPayData);

            return new ObjectMapper().writeValueAsString(billPayResponseDTO);

        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    // Si se encontró información del biller: MOVII o GeTrax
    private Biller getBiller(String billCode, String ean13BillerCode) {
        // Buscar en MOVII
        Biller biller;
        if (ean13BillerCode != null) {
            biller = this.billerRepository.getByEanCode(ean13BillerCode);
        } else {
            biller = this.billerRepository.getByBillerCode(billCode);
        }

        // Buscar en ZEUS (GeTrax)
        if (biller == null) {
            List<Agreement> agreements;
            if (ean13BillerCode != null) {
                agreements = this.agreementRepository.findAgreementsByCodeAndStatus(ean13BillerCode, GeneralStatus.ENABLED);
            } else {
                agreements = this.agreementRepository.findAgreementsByIdAndStatus(Integer.parseInt(billCode), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((agreements != null) && (agreements.size() == 1)) {
                Agreement agreement = agreements.get(0);
                biller = new Biller();
                biller.setBillerCode(agreement.getId().toString());
                biller.setEanCode(agreement.getCode());
                biller.setName(agreement.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((biller != null) && (biller.getEanCode().length() > 13)) {
            biller.setEanCode(biller.getEanCode().substring(0, 12));
        }

        return biller;
    }

    private String billPayRestParseDate(String requestDate) {
        String billPayRestRetorno = requestDate;
        SimpleDateFormat billPayRestInput;
        Date billPayRestDateValue;

        try {
            // Formato principal
            billPayRestInput = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            billPayRestDateValue = billPayRestInput.parse(requestDate);

        } catch (java.text.ParseException e) {
            try {
                // Formato alterno
                billPayRestInput = new SimpleDateFormat("yyyyMMddHHmmss");
                billPayRestDateValue = billPayRestInput.parse(requestDate);

            } catch (java.text.ParseException ex) {
                billPayRestDateValue = null;
            }
        }

        // Si se obtiene la fecha darle el formato requerido por Mahindra
        if (billPayRestDateValue != null) {
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            billPayRestRetorno = output.format(billPayRestDateValue);
        }

        return billPayRestRetorno;
    }

}

