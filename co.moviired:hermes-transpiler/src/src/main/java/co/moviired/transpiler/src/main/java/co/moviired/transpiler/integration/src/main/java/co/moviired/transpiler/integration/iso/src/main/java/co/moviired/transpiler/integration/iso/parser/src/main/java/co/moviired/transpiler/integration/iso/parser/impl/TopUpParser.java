package co.moviired.transpiler.integration.iso.parser.impl;

import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.helper.ErrorHelper;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.iso.model.TopUpResponse;
import co.moviired.transpiler.integration.iso.service.IsoHelper;
import co.moviired.transpiler.jpa.getrax.domain.ProductGetrax;
import co.moviired.transpiler.jpa.getrax.repository.IProductGetraxRepository;
import co.moviired.transpiler.jpa.movii.domain.Product;
import co.moviired.transpiler.jpa.movii.domain.User;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ProductHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.TopUpHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.TopUpHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

@Slf4j
public class TopUpParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Services/Repositories
    private final IUserRepository iUserRepository;
    private final IProductRepository productRepository;
    private final IProductGetraxRepository productGetraxRepository;
    private final ErrorHelper errorHelper;

    // Iso8583 Packer definitions
    private final transient ISOBasePackager packager;

    public TopUpParser(
            @NotNull String xmlPackager,
            @NotNull IUserRepository piUserRepository,
            @NotNull IProductRepository piProductRepository,
            @NotNull IProductGetraxRepository piProductGetraxRepository,
            @NotNull ErrorHelper errorHelper) throws IOException, ISOException {
        super();
        this.iUserRepository = piUserRepository;
        this.productRepository = piProductRepository;
        this.productGetraxRepository = piProductGetraxRepository;
        this.errorHelper = errorHelper;
        this.packager = new GenericPackager(new ClassPathResource(xmlPackager).getInputStream());
    }

    // SERVICE METHODS
    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener los fields del ISOMessage Topup
            Map<Integer, String> topUpIsoFields = IsoHelper.getDataIsoFieldFromMessage(this.packager, request);

            // Cliente (Usuario y Clave)
            String topUpClientName = "";
            String topUpUserREQ = topUpIsoFields.get(42).trim();
            String topUpPassREQ = topUpIsoFields.get(52).trim();
            String topUpTimeZone = "";
            Optional<User> topUpUser = iUserRepository.findByGetraxUsername(AESCrypt.crypt(topUpUserREQ));
            if (topUpUser.isPresent()) {
                User user = topUpUser.get();

                // Verificar: clave y estado del usuario y del cliente
                if ((user.getGetraxPassword().equals(AESCrypt.crypt(topUpPassREQ)))
                        && (user.getStatus().equals(GeneralStatus.ENABLED))
                        && (user.getClient().getStatus().equals(GeneralStatus.ENABLED))) {

                    topUpUserREQ = AESCrypt.decrypt(user.getMahindraUsername());
                    topUpPassREQ = AESCrypt.decrypt(user.getMahindraPassword());
                    topUpPassREQ = topUpPassREQ.replaceFirst(user.getId().toString(), "");
                    topUpClientName = user.getClient().getName();
                    topUpTimeZone = user.getClient().getTimeZone();
                }
            }
            ClientHermes clientHermes = new ClientHermes(topUpClientName, topUpUserREQ, topUpPassREQ, topUpTimeZone);

            // Producto (id, EANCode, MSISDN2 -RechargeNumber- )
            String dataField35 = topUpIsoFields.get(35);
            StringTokenizer tokens = new StringTokenizer(dataField35, "|");
            String topUpProductId = "0";
            String topUpProductCode = "0";
            String topUpProductName = "0";
            String topUpEancode = tokens.nextToken();
            ProductType topUpType = ProductType.OTHER;

            // Si se encontró información del producto: MOVII o GeTrax
            Product topUpProduct = this.getProduct(topUpProductId, topUpEancode);
            if (topUpProduct != null) {
                topUpEancode = topUpProduct.getEanCode();
                topUpProductId = String.valueOf(topUpProduct.getOperatorId());
                topUpProductCode = topUpProduct.getProductCode();
                topUpProductName = topUpProduct.getName();
                topUpType = topUpProduct.getType();
            }
            ProductHermes productHermes = new ProductHermes(topUpProductId, topUpProductCode, topUpEancode, topUpProductName, topUpType);

            // Recharge number y Monto
            String rechargeNumber = tokens.nextToken();
            String valor = topUpIsoFields.get(4);
            Integer amount = Integer.parseInt(valor.substring(0, 10));

            // CajeroID
            String cashierId = topUpIsoFields.get(41);

            // MerchantID
            String merchantId;
            if (topUpUser.isEmpty()) {
                merchantId = topUpIsoFields.get(32);
            } else {
                merchantId = topUpIsoFields.get(42);
            }

            // DeviceID
            String deviceId = topUpIsoFields.get(43).substring(0, 24);

            // RequestDate
            String requestDate = Year.now().getValue() + topUpIsoFields.get(7);
            requestDate = toUpParseDateIso(requestDate);

            // Armar el CommandValidateBillByReference
            TopUpHermesRequest topup = new TopUpHermesRequest();
            topup.setOriginalRequest(request);
            topup.setClient(clientHermes);
            topup.setProduct(productHermes);
            topup.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            topup.setRechargeNumber(rechargeNumber);
            topup.setAmount(amount);

            topup.setMerchantId(merchantId.trim());
            topup.setDeviceId(deviceId.trim());
            topup.setRequestDate(requestDate);
            topup.setCashierId(cashierId);

            // Se agrega el número de Transaccón del cliente
            topup.setClientTxnId(topUpIsoFields.get(37));

            return topup;

        } catch (ISOException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Transformar a la respuesta específica del parser
            TopUpHermesResponse topUpResponse = (TopUpHermesResponse) hermesResponse;

            // Número de autorización
            String autorizacion = topUpResponse.getAuthorizationNumber();
            if (autorizacion == null) {
                autorizacion = "00000000000";
            }

            // Fecha de respuesta
            Date field07 = Calendar.getInstance().getTime();

            // Número de autorización
            String field38 = "000000";

            // Código respuesta
            String field39 = "99";
            String[] r = errorHelper.getError(topUpResponse.getResponse().getStatusCode());
            String mensajeRespuesta = r[0] + "|" + r[1];

            // Montos
            String field54 = "0000000000000000";

            // Nuevo Saldo
            String nuevoSaldo = "0";

            // Veriicar el estado de la transaccion: OK
            if (topUpResponse.getResponse().getStatusCode().equals("200")) {
                // Número de autorización OK
                field38 = autorizacion.substring(autorizacion.length() - 6);

                // Respuesta OK
                field39 = "00";
                r = errorHelper.getError(topUpResponse.getResponse().getStatusCode());
                mensajeRespuesta = r[0] + "|" + r[1];

                // New Balance
                nuevoSaldo = topUpResponse.getNewBalance();
            }

            // Código de authenticación
            String field63 = autorizacion
                    + "|0|" + mensajeRespuesta + "|14|"
                    + field54
                    + "|000|"
                    + nuevoSaldo
                    + "|000000|0|0|00000";

            // Campos del ISOMessage Request
            Map<Integer, String> isoFieldsRequest = getMessage(topUpResponse.getRequest().getOriginalRequest());
            if (isoFieldsRequest == null) {
                throw new ParseException("TRAMA INVÁLIDA");
            }

            // Armar el objeto de la respuesta
            TopUpResponse tpResp = new TopUpResponse();
            tpResp.setAccountNumber((isoFieldsRequest.get(2) != null) ? isoFieldsRequest.get(2) : "");
            tpResp.setProcessingCode((isoFieldsRequest.get(3) != null) ? isoFieldsRequest.get(3) : "");
            tpResp.setAmount((isoFieldsRequest.get(4) != null) ? Integer.parseInt(isoFieldsRequest.get(4).trim()) : 0);
            tpResp.setTransactionCode((isoFieldsRequest.get(11) != null) ? Integer.parseInt(isoFieldsRequest.get(11).trim()) : 0);
            tpResp.setNit((isoFieldsRequest.get(32) != null) ? isoFieldsRequest.get(32) : "");
            tpResp.setTrackNumber((isoFieldsRequest.get(35) != null) ? isoFieldsRequest.get(35) : "");
            tpResp.setRetrievalReferenceNumber((isoFieldsRequest.get(37) != null) ? Long.parseLong(isoFieldsRequest.get(37).trim()) : 0L);
            tpResp.setCardAcceptor((isoFieldsRequest.get(41) != null) ? isoFieldsRequest.get(41) : "");
            tpResp.setTerminalID((isoFieldsRequest.get(42) != null) ? isoFieldsRequest.get(42) : "");
            tpResp.setAdditionalAmount((isoFieldsRequest.get(54) != null) ? isoFieldsRequest.get(54) : field54);

            // Número de recarga
            String rechargeNumber = isoFieldsRequest.getOrDefault(43, " ");
            tpResp.setRechargeNumber(StringUtils.rightPad(rechargeNumber, 40, " "));

            // Campos de la respuesta
            tpResp.setDateTime(field07);
            tpResp.setAuthorizationNumber(field38);
            tpResp.setResponseCode(field39);
            tpResp.setReservedPrivate(field63);

            // Generar la trama de respuesta
            ISOMsg isoMessage = ISOMsgHelper.of(OperationType.TOPUP_RESPONSE.getCode(), tpResp, this.packager);
            byte[] b = isoMessage.pack();
            String isoResponse = new String(b, Charset.defaultCharset());

            return StringUtils.leftPad(Integer.toHexString(isoResponse.length()), 3, '0') + isoResponse;

        } catch (ISOException | ParseException | IllegalAccessException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    private String toUpParseDateIso(String requestDate) {
        String topUpIsoRetorno = requestDate;
        SimpleDateFormat topUpIsoInput;
        Date topUpIsoDateValue;

        try {
            // Formato principal
            topUpIsoInput = new SimpleDateFormat("yyyyMMddHHmmss");
            topUpIsoDateValue = topUpIsoInput.parse(requestDate);

        } catch (java.text.ParseException ex) {
            topUpIsoDateValue = null;
        }

        // Si se obtiene la fecha darle el formato requerido por Mahindra
        if (topUpIsoDateValue != null) {
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            topUpIsoRetorno = output.format(topUpIsoDateValue);
        }

        return topUpIsoRetorno;
    }

    // Si se encontró información del biller: MOVII o GeTrax
    // Buscar en MOVII
    private Product getProduct(String productId, String eanCode) {
        Product topUpProductIso = null;
        List<Product> topUpProductIsoList;
        if (eanCode != null) {
            topUpProductIsoList = productRepository.findByEan(eanCode, PageRequest.of(0, 1));
            if (!topUpProductIsoList.isEmpty()) {
                topUpProductIso = topUpProductIsoList.get(0);
            }
        } else {
            topUpProductIso = this.productRepository.getProductById(Integer.parseInt(productId));
        }

        // Buscar en ZEUS (GeTrax)
        if (topUpProductIso == null) {
            List<ProductGetrax> productGetraxes;
            if (eanCode != null) {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByCodeAndStatus(eanCode, GeneralStatus.ENABLED);
            } else {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByIdAndStatus(Integer.parseInt(productId), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((productGetraxes != null) && (productGetraxes.size() == 1)) {
                ProductGetrax productGetrax = productGetraxes.get(0);
                topUpProductIso = new Product();
                topUpProductIso.setId(Integer.parseInt(productGetrax.getId()));
                topUpProductIso.setProductCode(String.valueOf(productGetrax.getProductCode()));
                topUpProductIso.setEanCode(productGetrax.getCode());
                topUpProductIso.setName(productGetrax.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((topUpProductIso != null) && (topUpProductIso.getEanCode().length() > 13)) {
            topUpProductIso.setEanCode(topUpProductIso.getEanCode().substring(0, 13));
        }

        return topUpProductIso;
    }

    private Map<Integer, String> getMessage(String request) {
        // Cargar el ISO packager
        ISOBasePackager isoPackager;
        Map<Integer, String> isoFields = null;
        try {
            isoFields = IsoHelper.getDataIsoFieldFromMessage(this.packager, request);

        } catch (Exception e) {
            // NO SE PUDO MAPEAR CON EL DE TOPUP
            try {
                isoPackager = new GenericPackager(new ClassPathResource("iso/exitoPackager.xml").getInputStream());
                isoFields = IsoHelper.getDataIsoFieldFromMessage(isoPackager, request);
            } catch (ISOException | IOException e1) {
                log.error(e1.getMessage());
            }
        }

        return isoFields;
    }

}
