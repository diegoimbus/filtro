package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.conf.MahindraProperties;
import co.moviired.transpiler.helper.UtilsHelper;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.BillerHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.BillPayHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.BillPayHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.response.CommandBillPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class BillPayMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final MahindraProperties mahindraProperties;

    public BillPayMahindraParser(MahindraProperties pmahindraProperties) {
        super();
        this.mahindraProperties = pmahindraProperties;
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) {
        // Transformar al tipo específico de IHermes Request
        BillPayHermesRequest req = (BillPayHermesRequest) hermesRequest;

        // Datos especificos de la transaccion
        co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request.CommandBillPay billPay = new co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request.CommandBillPay();

        // Biller
        BillerHermes biller = req.getBiller();
        if (biller != null) {
            billPay.setEan13billercode(biller.getEanBillerCode());
            billPay.setBillercode(biller.getBillerCode());
            billPay.setBname((biller.getName() == null) ? "" : biller.getName().toUpperCase());
        }

        // Específicos
        billPay.setAmount(req.getAmount().toString());
        billPay.setEchodata(req.getEchoData());
        billPay.setCellId(req.getDeviceCode());

        // REMARKS: MerchantID | DeviceID | RequestDate
        String remarks = req.getCustomerId() + "|" + req.getDeviceCode() + "|" + req.getRequestDate();
        billPay.setRemarks(remarks);

        // Usuario
        billPay.setMsisdn1(req.getClient().getUsername());
        billPay.setMsisdn2(req.getClient().getUsername());
        billPay.setMpin(req.getClient().getPassword());
        billPay.setPin(req.getClient().getPassword());

        // IMEI: 0|127.0.0.1|HERMES-TRANSPILER|{0}|{1} - {0}: número de transacción del cliente; {1}: fecha del cliente
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String imei = this.mahindraProperties.getImei().replace("{0}", req.getClientTxnId());
        imei = imei.replace("{1}", UtilsHelper.parseDate(sdf.format(new Date())));
        billPay.setImei(imei);

        // Datos prestablecidos
        billPay.setType(this.mahindraProperties.getBpType());
        billPay.setSubtype(this.mahindraProperties.getBpSubtype());
        billPay.setPayid(this.mahindraProperties.getBpPayId());
        billPay.setPaymentInstrument(this.mahindraProperties.getBpPaymentInstrument());
        billPay.setLanguage1(this.mahindraProperties.getBpLanguage1());
        billPay.setProvider(this.mahindraProperties.getBpProvider());
        billPay.setBprovider(this.mahindraProperties.getBpBprovider());
        billPay.setSource(this.mahindraProperties.getBpSource());

        // TIPO DE PAGO
        if (req.getShortReferenceNumber() != null) {
            // Pago Manual
            billPay.setShortreferencenumber(req.getShortReferenceNumber());
            billPay.setEan13billercode(null);
        } else {
            // Pago Automático
            billPay.setBillreferencenumber(req.getBillReferenceNumber());
            billPay.setBillercode(null);
        }

        return billPay;
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse pcommand) {
        // Transformar al command específico
        CommandBillPay command = (CommandBillPay) pcommand;

        // Código y mensaje de respuesta
        ResponseHermes resp = new ResponseHermes();
        resp.setStatusCode(command.getTxnstatus().toUpperCase());
        resp.setStatusMessage(command.getMessage().toUpperCase());

        // Detalle del error
        resp.setErrorCode(command.getTxnstatus().toUpperCase());
        resp.setErrorMessage(command.getMessage().toUpperCase());

        // Armar el objeto respuesta
        BillPayHermesRequest billRequest = (BillPayHermesRequest) hermesRequest;
        BillPayHermesResponse billResponse = new BillPayHermesResponse();
        billResponse.setRequest(billRequest);
        billResponse.setResponse(resp);

        if ("200".equals(resp.getStatusCode())) {
            billResponse.setDevice(billRequest.getDeviceCode());
            billResponse.setCommission(command.getCommission());
            billResponse.setNewbalance(command.getNewbalance());
            billResponse.setTransactionid(command.getTransactionid());
            billResponse.setBankid(command.getBankid());
            billResponse.setBanktransactionid(command.getBanktransactionid());
            billResponse.setBillercode(command.getBillercode());
            billResponse.setShortreferencenumber(command.getShortreferencenumber());
            billResponse.setValuetopay(command.getValuetopay());
            billResponse.setChargevalue(command.getChargevalue());
            billResponse.setCommisionvalue(command.getCommisionvalue());
            billResponse.setTxnid(command.getTxnid());
            billResponse.setMessage(command.getMessage());
            billResponse.setTrid(command.getTrid());
        }

        return billResponse;
    }

}

