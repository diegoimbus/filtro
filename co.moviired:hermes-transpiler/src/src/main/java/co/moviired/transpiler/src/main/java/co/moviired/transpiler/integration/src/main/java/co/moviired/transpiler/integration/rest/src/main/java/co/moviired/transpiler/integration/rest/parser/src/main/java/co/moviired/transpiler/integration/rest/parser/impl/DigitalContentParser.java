package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.integration.rest.dto.digitalcontent.request.RequestDigitalContentDTO;
import co.moviired.transpiler.integration.rest.dto.digitalcontent.response.Data;
import co.moviired.transpiler.integration.rest.dto.digitalcontent.response.ResponseDigitalContentDTO;
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
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service("digitalContnetParser")
public class DigitalContentParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

    // Repositories
    private final IUserRepository userRepository;
    private final IProductRepository productRepository;
    private final IProductGetraxRepository productGetraxRepository;

    public DigitalContentParser(IUserRepository piUserRepository, IProductRepository piProductRepository, IProductGetraxRepository piProductGetraxRepository) {
        super();
        this.userRepository = piUserRepository;
        this.productRepository = piProductRepository;
        this.productGetraxRepository = piProductGetraxRepository;
    }

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            RequestDigitalContentDTO digitalContentReq = new ObjectMapper().readValue(request, RequestDigitalContentDTO.class);

            // Cliente (Usuario y Clave)
            String digitalContentClientName = "";
            String digitalContentUserREQ = digitalContentReq.getMeta().getUserName();
            String digitalContentPassREQ = digitalContentReq.getMeta().getPasswordHash();
            String digitalContentTimeZone = "";
            if (digitalContentUserREQ == null) {
                digitalContentUserREQ = "";
            }
            if (digitalContentPassREQ == null) {
                digitalContentPassREQ = "";
            }
            Optional<User> digitalContentUser = userRepository.findByGetraxUsername(AESCrypt.crypt(digitalContentUserREQ));
            if (digitalContentUser.isPresent()) {
                User dContentUser = digitalContentUser.get();
                if (dContentUser.getGetraxPassword().equals(AESCrypt.crypt(digitalContentPassREQ))) {
                    digitalContentUserREQ = AESCrypt.decrypt(dContentUser.getMahindraUsername());
                    digitalContentPassREQ = AESCrypt.decrypt(dContentUser.getMahindraPassword());
                    digitalContentPassREQ = digitalContentPassREQ.replaceFirst(dContentUser.getId().toString(), "");
                    digitalContentClientName = dContentUser.getClient().getName();
                    digitalContentTimeZone = dContentUser.getClient().getTimeZone();
                }
            }

            ClientHermes digitalContentClientHermes = new ClientHermes(digitalContentClientName, digitalContentUserREQ, digitalContentPassREQ, digitalContentTimeZone);

            // Producto (id, EANCode, MSISDN2 -RechargeNumber- )
            String productId = digitalContentReq.getData().getProductId();
            String productCode = "0";
            String productName = "0";
            String eancode = digitalContentReq.getData().getEANCode();
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

            // Generar el identificador único de operación
            String uuidOperation = UUID.randomUUID().toString().replace("-", "");

            // Armar el CommandValidateBillByReference
            DigitalContentHermesRequest digitalContent = new DigitalContentHermesRequest();
            digitalContent.setOriginalRequest(request);
            digitalContent.setOperation(digitalContentReq.getData().getOperation());
            digitalContent.setCorrelationId(uuidOperation);
            digitalContent.setIssueDate(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
            digitalContent.setIssuerName(digitalContentReq.getMeta().getUserName());
            digitalContent.setPhoneNumber(digitalContentReq.getData().getPhoneNumber());
            digitalContent.setIp(digitalContentReq.getMeta().getOriginAddress());
            digitalContent.setSource("");
            digitalContent.setProductId(productCode);
            digitalContent.setEanCode(eancode);
            digitalContent.setCardSerialNumber(digitalContentReq.getData().getCardSerialNumber());
            digitalContent.setAmount(digitalContentReq.getData().getAmount());
            digitalContent.setEmail(digitalContentReq.getData().getEmail());
            digitalContent.setUsename(digitalContentReq.getMeta().getUserName());
            digitalContent.setCustomerId(digitalContentReq.getMeta().getCustomerId());
            digitalContent.setClient(digitalContentClientHermes);
            digitalContent.setDeviceId(digitalContentReq.getMeta().getDeviceCode());
            digitalContent.setMerchantId(digitalContentReq.getMeta().getCustomerId());
            digitalContent.setProduct(productHermes);

            return digitalContent;

        } catch (IOException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Obtener el DTO de la respuesta
            DigitalContentHermesResponse digitalContent = (DigitalContentHermesResponse) hermesResponse;

            // Estado de la transacción
            Data data = null;
            Outcome outcome = new Outcome();
            outcome.setStatusCode(digitalContent.getErrorCode());
            outcome.setMessage(digitalContent.getErrorMessage());

            // Armar el ERROR
            Error error = new Error();
            error.setErrorType("0");
            error.setErrorCode(digitalContent.getErrorCode());
            error.setErrorMessage(digitalContent.getErrorMessage());
            outcome.setError(error);


            RequestDigitalContentDTO requestOriginal = new ObjectMapper().readValue(digitalContent.getRequest().getOriginalRequest(), RequestDigitalContentDTO.class);


            // Si la respuesta es OK, armar el detalle
            if (error.getErrorCode().equals("00")) {
                data = new Data();
                data.setIdMessage("0210");
                data.setExpirationDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
                data.setMessage("TRANSACCION EXITOSA");
                data.setTransactionCode(digitalContent.getAuthorizationCode());
                data.setProductCode(requestOriginal.getData().getProductId());
                data.setCustomerBalance("0");
                data.setCode("00");
                data.setCode2("00");
                data.setShortReferenceNumber(digitalContent.getRequest().getEanCode());
                data.setDate(new SimpleDateFormat("ddMMHHmmss").format(Calendar.getInstance().getTime()));
                data.setTermsAndConditions(digitalContent.getTermAndConditions());
                data.setAuthorizationCode(digitalContent.getAuthorizationCode());
                data.setTransactionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                data.setProductId(requestOriginal.getData().getProductId());
                data.setAmount(digitalContent.getAmount());
                data.setValueToPay(setValueToPayMethod(digitalContent.getAmount()));
                data.setProcessCode(requestOriginal.getData().getOperation());
                data.setAdditionalInformation("");
                data.setDevice(requestOriginal.getMeta().getUserName());
                data.setPosId(digitalContent.getRequest().getCustomerId());
                data.setSubProductCode("0");
                data.setUser(requestOriginal.getMeta().getUserName());
                data.setPin(digitalContent.getPin());
                data.setInvoiceNumber(digitalContent.getInvoiceNumber());

                // Establecer el código respuesta 00 al cliente
                error.setErrorCode("00");
                error.setErrorMessage("TRANSACCION EXITOSA");
                outcome.setStatusCode("200");
                outcome.setMessage("SUCCESSFUL TRANSACTION Indica que la petición fue realizada correctamente (SUCESS)");
            }

            // Armar la respuesta completa
            ResponseDigitalContentDTO responseDTO = new ResponseDigitalContentDTO();
            responseDTO.setOutcome(outcome);
            responseDTO.setData(data);

            return new ObjectMapper().writeValueAsString(responseDTO);

        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    // Método que devuelve el monto a pagar
    private String setValueToPayMethod(String amount) {
        try {
            return ISOUtil.zeropad(amount.concat("00"), 12);
        } catch (ISOException e) {
            return amount;
        }
    }

    // Si se encontró información del biller: MOVII o GeTrax
    // Buscar en MOVII
    private Product getProduct(String productId, String eanCode) {
        Product digitalContentProductRest = null;
        List<Product> digitalContentProductListRest;
        if (eanCode != null) {
            digitalContentProductListRest = productRepository.findByEan(eanCode, PageRequest.of(0, 1));
            if (!digitalContentProductListRest.isEmpty()) {
                digitalContentProductRest = digitalContentProductListRest.get(0);
            }
        } else {
            digitalContentProductRest = this.productRepository.getProductById(Integer.parseInt(productId));
        }

        // Buscar en ZEUS (GeTrax)
        if (digitalContentProductRest == null) {
            List<ProductGetrax> productGetraxes;
            if (eanCode != null) {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByCodeAndStatus(eanCode, GeneralStatus.ENABLED);
            } else {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByIdAndStatus(Integer.parseInt(productId), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((productGetraxes != null) && (productGetraxes.size() == 1)) {
                ProductGetrax productGetrax = productGetraxes.get(0);
                digitalContentProductRest = new Product();
                digitalContentProductRest.setId(Integer.parseInt(productGetrax.getId()));
                digitalContentProductRest.setProductCode(String.valueOf(productGetrax.getProductCode()));
                digitalContentProductRest.setEanCode(productGetrax.getCode());
                digitalContentProductRest.setName(productGetrax.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((digitalContentProductRest != null) && (digitalContentProductRest.getEanCode().length() > 13)) {
            digitalContentProductRest.setEanCode(digitalContentProductRest.getEanCode().substring(0, 13));
        }

        return digitalContentProductRest;
    }

}

