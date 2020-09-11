package co.moviired.transpiler.integration.iso.parser.impl;

import co.moviired.transpiler.conf.DigitalContentProperties;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.helper.ErrorHelper;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.iso.service.IsoHelper;
import co.moviired.transpiler.jpa.getrax.domain.ProductGetrax;
import co.moviired.transpiler.jpa.getrax.repository.IProductGetraxRepository;
import co.moviired.transpiler.jpa.movii.domain.Product;
import co.moviired.transpiler.jpa.movii.domain.User;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ProductHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.DigitalContentHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.DigitalContentHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DigitalContentParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Services/Repositories
    private final IUserRepository iUserRepository;
    private final IProductRepository productRepository;
    private final IProductGetraxRepository productGetraxRepository;
    private final DigitalContentProperties digitalContentProperties;
    private final ErrorHelper errorHelper;

    // Iso8583 Packer definitions
    private final transient ISOBasePackager packager;

    public DigitalContentParser(
            @NotNull String xmlPackager,
            @NotNull IUserRepository piUserRepository,
            @NotNull IProductRepository piProductRepository,
            @NotNull IProductGetraxRepository piProductGetraxRepository,
            @NotNull ErrorHelper errorHelper,
            @NotNull DigitalContentProperties digitalContentProperties) throws IOException, ISOException {
        super();
        this.iUserRepository = piUserRepository;
        this.digitalContentProperties = digitalContentProperties;
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
            Map<Integer, String> digitalContentIsoFields = IsoHelper.getDataIsoFieldFromMessage(this.packager, request);

            // Cliente (Usuario y Clave)
            String digitalContentClientName = "";
            String digitalContentUserREQ = digitalContentIsoFields.get(42).trim();
            String digitalContentPassREQ = digitalContentIsoFields.get(52).trim();
            String digitalContentTimeZone = "";
            Optional<User> tuser = iUserRepository.findByGetraxUsername(AESCrypt.crypt(digitalContentUserREQ));
            if (tuser.isPresent()) {
                User user = tuser.get();
                if (user.getGetraxPassword().equals(AESCrypt.crypt(digitalContentPassREQ))) {
                    digitalContentUserREQ = AESCrypt.decrypt(user.getMahindraUsername());
                    digitalContentPassREQ = AESCrypt.decrypt(user.getMahindraPassword());
                    digitalContentPassREQ = digitalContentPassREQ.replaceFirst(user.getId().toString(), "");
                    digitalContentClientName = user.getClient().getName();
                    digitalContentTimeZone = user.getClient().getTimeZone();
                }
            }
            ClientHermes clientHermes = new ClientHermes(digitalContentClientName, digitalContentUserREQ, digitalContentPassREQ, digitalContentTimeZone);

            // Producto (id, EANCode, MSISDN2 -RechargeNumber- )
            String dataField35 = digitalContentIsoFields.get(35);
            StringTokenizer tokens = new StringTokenizer(dataField35, "|");
            String productId = digitalContentIsoFields.get(2);
            String productCode = "0";
            String productName = "0";
            String eancode = tokens.nextToken();
            ProductType type = ProductType.OTHER;

            // Si se encontró información del producto: MOVII o GeTrax
            Product product = this.getProduct(productId, eancode);
            if (product != null) {
                eancode = product.getEanCode();
                productId = String.valueOf(product.getOperatorId());
                productCode = product.getProductCode();
                productName = product.getName();
                type = product.getType();
            }
            ProductHermes productHermes = new ProductHermes(productId, productCode, eancode, productName, type);

            // Monto
            String valor = digitalContentIsoFields.get(4);
            Integer amount = Integer.parseInt(valor.substring(0, 10));

            String userId;
            if (tuser.isEmpty()) {
                userId = digitalContentIsoFields.get(32);
            } else {
                userId = digitalContentIsoFields.get(42);
            }
            // DeviceID
            String deviceId = digitalContentIsoFields.get(43).substring(0, 25);

            // Armar el CommandValidateBillByReference
            DigitalContentHermesRequest topup = new DigitalContentHermesRequest();
            topup.setOriginalRequest(request);
            topup.setOperation(digitalContentIsoFields.get(3));
            String cardNumber = tokens.nextToken().replace(eancode, "");

            if (digitalContentProperties.getCodeActivate().contains(digitalContentIsoFields.get(3))) {
                topup.setCorrelationId(digitalContentIsoFields.get(11) + cardNumber);
            } else if (digitalContentProperties.getCodeInactivate().contains(digitalContentIsoFields.get(3))) {
                topup.setCorrelationId(digitalContentIsoFields.get(37));
                topup.setCorrelationIdR(digitalContentIsoFields.get(11) + cardNumber);
            } else if (digitalContentProperties.getCodePinesSale().contains(digitalContentIsoFields.get(3))) {
                topup.setCorrelationId(digitalContentIsoFields.get(11));
            } else if (digitalContentProperties.getCodePinesInactivate().contains(digitalContentIsoFields.get(3))) {
                topup.setCorrelationId(digitalContentIsoFields.get(11));
            }

            topup.setIssueDate(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
            topup.setIssuerName(digitalContentIsoFields.get(42));
            topup.setPhoneNumber(digitalContentIsoFields.get(61));
            topup.setIp("");
            topup.setSource("CHANNEL");
            topup.setProductId(productCode);
            topup.setEanCode(eancode);
            topup.setCardSerialNumber(cardNumber);
            topup.setAmount(amount);
            topup.setEmail(digitalContentIsoFields.get(62));
            topup.setUsename(userId);
            topup.setClient(clientHermes);
            topup.setDeviceId(deviceId);
            topup.setProduct(productHermes);

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
            DigitalContentHermesResponse digitalContentResponse = (DigitalContentHermesResponse) hermesResponse;

            // Obtener los fields del ISOMessage Topup
            Map<Integer, String> isoFieldsRequest = IsoHelper.getDataIsoFieldFromMessage(this.packager, digitalContentResponse.getRequest().getOriginalRequest());

            // Número de autorización
            String autorizacion = digitalContentResponse.getAuthorizationCode();
            if (autorizacion == null) {
                autorizacion = "00000000000";
            }

            // Fecha de respuesta
            String field07 = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

            // Número de autorización
            String field38 = "000000";

            // Código respuesta
            String field39 = "99";
            String[] r = errorHelper.getError(digitalContentResponse.getResponse().getStatusCode());
            String mensajeRespuesta = r[0] + "|" + r[1];

            // Montos
            String field54 = "000";

            String fechaCalculada = new SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime());

            //PIN
            String pin = (digitalContentResponse.getPin() == null) ? "PIN0000000000000" : digitalContentResponse.getPin();

            // Veriicar el estado de la transaccion: OK
            if ("00".equals(digitalContentResponse.getErrorCode())) {
                // Número de autorización OK
                field38 = autorizacion.substring(autorizacion.length() - 6);

                // Respuesta OK
                field39 = "00";
                r = errorHelper.getError(digitalContentResponse.getErrorCode(), "Transaccion Exitosa");
                mensajeRespuesta = r[0] + "|" + r[1];
                field54 = "-" + isoFieldsRequest.get(4);
            }

            String field63 = digitalContentResponse.getAuthorizationNumber()
                    + "|0|" + mensajeRespuesta + "|14|" + pin
                    + "|170|" + field54 + "|" + fechaCalculada
                    + "|0|0|00000";

            // Crear el mensaje de respuesta
            ISOMsg isoMessage = new ISOMsg(OperationType.TOPUP_RESPONSE.getCode());
            isoMessage.setPackager(this.packager);
            isoMessage.set(new ISOField(2, isoFieldsRequest.get(2)));
            isoMessage.set(new ISOField(3, isoFieldsRequest.get(3)));
            isoMessage.set(new ISOField(4, isoFieldsRequest.get(4)));
            isoMessage.set(new ISOField(7, field07));
            isoMessage.set(new ISOField(11, isoFieldsRequest.get(11)));
            if (isoFieldsRequest.get(32) != null) {
                isoMessage.set(new ISOField(32, isoFieldsRequest.get(32)));
            }
            isoMessage.set(new ISOField(35, isoFieldsRequest.get(35)));
            isoMessage.set(new ISOField(37, isoFieldsRequest.get(37)));
            isoMessage.set(new ISOField(38, field38));
            isoMessage.set(new ISOField(39, field39));
            isoMessage.set(new ISOField(41, isoFieldsRequest.get(41)));
            isoMessage.set(new ISOField(42, isoFieldsRequest.get(42)));
            isoMessage.set(new ISOField(43, isoFieldsRequest.get(43)));
            isoMessage.set(new ISOField(54, pin));
            isoMessage.set(new ISOField(63, field63));
            isoMessage.recalcBitMap();

            // Generar la trama de respuesta
            byte[] b = isoMessage.pack();
            return new String(b, Charset.defaultCharset());

        } catch (Exception e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    // Si se encontró información del biller: MOVII o GeTrax
    // Buscar en MOVII
    private Product getProduct(String productId, String eanCode) {
        Product digitalContentProductIso = null;
        List<Product> digitalContentProductIsoList;
        if (eanCode != null) {
            digitalContentProductIsoList = productRepository.findByEan(eanCode, PageRequest.of(0, 1));
            if (!digitalContentProductIsoList.isEmpty()) {
                digitalContentProductIso = digitalContentProductIsoList.get(0);
            }
        } else {
            digitalContentProductIso = this.productRepository.getProductById(Integer.parseInt(productId));
        }

        // Buscar en ZEUS (GeTrax)
        if (digitalContentProductIso == null) {
            List<ProductGetrax> productGetraxes;
            if (eanCode != null) {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByCodeAndStatus(eanCode, GeneralStatus.ENABLED);
            } else {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByIdAndStatus(Integer.parseInt(productId), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((productGetraxes != null) && (productGetraxes.size() == 1)) {
                ProductGetrax productGetrax = productGetraxes.get(0);
                digitalContentProductIso = new Product();
                digitalContentProductIso.setId(Integer.parseInt(productGetrax.getId()));
                digitalContentProductIso.setProductCode(String.valueOf(productGetrax.getProductCode()));
                digitalContentProductIso.setEanCode(productGetrax.getCode());
                digitalContentProductIso.setName(productGetrax.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((digitalContentProductIso != null) && (digitalContentProductIso.getEanCode().length() > 13)) {
            digitalContentProductIso.setEanCode(digitalContentProductIso.getEanCode().substring(0, 13));
        }

        return digitalContentProductIso;
    }

}

