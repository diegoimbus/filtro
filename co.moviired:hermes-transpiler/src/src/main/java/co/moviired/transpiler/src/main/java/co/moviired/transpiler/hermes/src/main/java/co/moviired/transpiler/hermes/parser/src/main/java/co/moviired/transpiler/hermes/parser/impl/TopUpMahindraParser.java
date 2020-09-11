package co.moviired.transpiler.hermes.parser.impl;

import co.moviired.transpiler.conf.MahindraProperties;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.UtilsHelper;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.TopUpHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.TopUpHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.response.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class TopUpMahindraParser implements IMahindraParser {

    private static final long serialVersionUID = -2701788651210432721L;

    private final MahindraProperties mahindraProperties;

    public TopUpMahindraParser(MahindraProperties pmahindraProperties) {
        super();
        this.mahindraProperties = pmahindraProperties;
    }

    @Override
    public final ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) {
        // Transformar al tipo específico de IHermes Request
        TopUpHermesRequest req = (TopUpHermesRequest) hermesRequest;

        // Datos especificos de la transaccion
        co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request.Command topup = new co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request.Command();
        topup.setAmount(req.getAmount().toString());
        topup.setEancode(req.getProduct().getEanCode());
        topup.setOperatorid(req.getProduct().getId());
        topup.setOperatorname((req.getProduct().getName() == null) ? "" : req.getProduct().getName().toUpperCase());
        topup.setProductid(req.getProduct().getProductCode());
        topup.setMsisdn2(req.getRechargeNumber());
        topup.setMsisdn(req.getClient().getUsername());
        topup.setMpin(req.getClient().getPassword());
        topup.setPin(req.getClient().getPassword());
        topup.setCellid(req.getCashierId());

        // Se agrega el número de Transaccón del cliente
        topup.setFtxnid(req.getClientTxnId());

        // IMEI: 0|127.0.0.1|HERMES-TRANSPILER|{0}|{1} - {0}: número de transacción del cliente; {1}: fecha del cliente
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String imei = this.mahindraProperties.getImei().replace("{0}", req.getClientTxnId());
        imei = imei.replace("{1}", UtilsHelper.parseDate(sdf.format(new Date())));
        topup.setImei(imei);

        // Datos prestablecidos
        topup.setType(this.mahindraProperties.getType());
        topup.setPaymenttype(this.mahindraProperties.getPaymentType());
        topup.setPayid(this.mahindraProperties.getPayId());
        topup.setPayid2(this.mahindraProperties.getPayId2());
        topup.setLanguage1(this.mahindraProperties.getLanguage1());
        topup.setProvider(this.mahindraProperties.getProvider());
        topup.setProvider2(this.mahindraProperties.getProvider2());
        topup.setSource(this.mahindraProperties.getSource());

        // MerchantID | DeviceID | RequestDate
        topup.setRemarks(req.getMerchantId() + "|" + req.getDeviceId() + "|" + req.getRequestDate());

        return topup;
    }

    @Override
    public final IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse pcommand) throws ParseException {
        // Transformar al command específico
        Command command = (Command) pcommand;

        // Código y mensaje de respuesta
        ResponseHermes resp = new ResponseHermes();
        resp.setStatusCode(command.getTxnstatus().toUpperCase());
        resp.setStatusMessage(command.getMessage().toUpperCase());

        // Armar el objeto respuesta
        TopUpHermesResponse response = new TopUpHermesResponse();
        response.setRequest(hermesRequest);
        response.setResponse(resp);

        // Número de autorización
        String txnid = command.getTxnid();
        if ((txnid == null) || (txnid.trim().isEmpty())) {
            txnid = "000000000000";
        }
        String authorization = transformAuthorizationNumber(txnid.toUpperCase());
        response.setAuthorizationNumber(authorization);
        response.setTransactionCode(authorization);

        if ("200".equals(resp.getStatusCode())) {
            // NewBalance
            String balance = (command.getNewbalance() != null) ? command.getNewbalance() : "0.0";
            response.setTxnId(txnid);
            response.setTransactionDate(command.getTransactiondate());
            response.setCustomerDate(command.getCustomerdate());
            response.setNewBalance(balance);
            response.setSubProductCode(command.getSubproduct());
        } else {
            // Detalle del error
            resp.setErrorCode(command.getTxnstatus().toUpperCase());
            resp.setErrorMessage(command.getMessage().toUpperCase());
        }
        response.setResponse(resp);

        return response;
    }

}

