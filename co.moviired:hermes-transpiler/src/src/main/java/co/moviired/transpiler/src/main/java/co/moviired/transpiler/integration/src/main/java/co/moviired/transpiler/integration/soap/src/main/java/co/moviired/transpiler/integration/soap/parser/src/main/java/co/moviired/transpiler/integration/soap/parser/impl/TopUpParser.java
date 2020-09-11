package co.moviired.transpiler.integration.soap.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivation;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivationResponse;
import co.moviired.transpiler.integration.soap.dto.soap.TransactionPrepaidSale;
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
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service("topUpParserSoap")
public class TopUpParser implements IHermesParser {

    private static final long serialVersionUID = -234710003935193617L;

    // Repositories
    private final IUserRepository userRepository;
    private final IProductRepository productRepository;
    private final IProductGetraxRepository productGetraxRepository;

    public TopUpParser(IUserRepository piUserRepository, IProductRepository piProductRepository, IProductGetraxRepository piProductGetraxRepository) {
        super();
        this.userRepository = piUserRepository;
        this.productRepository = piProductRepository;
        this.productGetraxRepository = piProductGetraxRepository;
    }

    @Override
    public final IHermesRequest parseRequest(@NotNull String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            PrepaidProductsActivation topupReq = new ObjectMapper().readValue(request, PrepaidProductsActivation.class);

            // Datos especificos de la transaccion

            // Cliente (Usuario y Clave)
            String clientName = "";
            String userREQ = topupReq.getUserName();
            String passREQ = topupReq.getPassword();
            String timeZone = "";
            Optional<User> tuser = userRepository.findByGetraxUsername(AESCrypt.crypt(userREQ));
            if (tuser.isPresent()) {
                User user = tuser.get();
                // Verificar: clave y estado del usuario y del cliente
                if ((user.getGetraxPassword().equals(AESCrypt.crypt(passREQ)))
                        && (user.getStatus().equals(GeneralStatus.ENABLED))
                        && (user.getClient().getStatus().equals(GeneralStatus.ENABLED))) {

                    userREQ = AESCrypt.decrypt(user.getMahindraUsername());
                    passREQ = AESCrypt.decrypt(user.getMahindraPassword());
                    passREQ = passREQ.replaceFirst(user.getId().toString(), "");
                    clientName = user.getClient().getName();
                    timeZone = user.getClient().getTimeZone();
                }
            }
            ClientHermes clientHermes = new ClientHermes(clientName, userREQ, passREQ, timeZone);

            // Producto (id, EANCode, MSISDN2 -RechargeNumber- )
            String productCode = "0";
            String productName = "0";
            String productId = topupReq.getProduct();
            String eancode = topupReq.getProductType();
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

            // Recharge number y Monto
            String rechargeNumber = topupReq.getDestinition();
            Integer amount = Integer.parseInt(topupReq.getAmount());

            // CajeroID
            String cashierId = topupReq.getDeviceNumber();

            // MerchantID
            String merchantId = topupReq.getUserName();

            // DeviceID
            String deviceId = topupReq.getDeviceNumber();

            // RequestDate
            String requestDate = topupReq.getTransactionDate();
            requestDate = topUpParseDateSoap(requestDate);

            // Armar el CommandValidateBillByReference
            TopUpHermesRequest topup = new TopUpHermesRequest();
            topup.setOriginalRequest(request);
            topup.setClientTxnId(topupReq.getTransactionId());
            topup.setClient(clientHermes);
            topup.setProduct(productHermes);
            topup.setDate(topupReq.getTransactionDate());
            topup.setRechargeNumber(rechargeNumber);
            topup.setAmount(amount);

            topup.setMerchantId(merchantId);
            topup.setDeviceId(deviceId);
            topup.setRequestDate(requestDate);
            topup.setCashierId(cashierId);

            return topup;

        } catch (IOException e) {
            log.error("\n{}\n", e.getMessage());
            throw new ParseException(e);
        }
    }

    @Override
    public final String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        try {
            // Obtener el DTO de la respuesta
            TopUpHermesResponse response = (TopUpHermesResponse) hermesResponse;

            // Amar el objeto respuesta
            TransactionPrepaidSale topupResponse = new TransactionPrepaidSale();
            topupResponse.setAnswerCode(response.getResponse().getStatusCode());
            topupResponse.setErrorDesc(response.getResponse().getStatusMessage());
            topupResponse.setErrorDescription(response.getResponse().getErrorMessage());

            // Si la respuesta es OK, armar el detalle
            if (response.getResponse().getStatusCode().equals("200")) {
                topupResponse.setDueDate(getResponseDate(response.getTransactionDate()));
                topupResponse.setBalance(response.getNewBalance());
                topupResponse.setActivationCode(response.getAuthorizationNumber());
                topupResponse.setBillNumber(response.getTransactionCode());
                topupResponse.setTransactionId(response.getAuthorizationNumber());
                topupResponse.setAvailable1(response.getSubProductCode());

                // Establecer el código respuesta 00 al cliente
                topupResponse.setAnswerCode("00");
            }

            // Armar la respuesta completa
            PrepaidProductsActivationResponse responseDTO = new PrepaidProductsActivationResponse();
            responseDTO.setTransactionPrepaidSale(topupResponse);
            return new ObjectMapper().writeValueAsString(responseDTO);

        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    private String topUpParseDateSoap(String requestDate) {
        String topUpSoapResponse = requestDate;
        SimpleDateFormat topUpSoapInputFormat;
        Date topUpSoapDateValue;

        try {
            // Formato principal
            topUpSoapInputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            topUpSoapDateValue = topUpSoapInputFormat.parse(requestDate);

        } catch (java.text.ParseException ex) {
            topUpSoapDateValue = null;
        }

        // Si se obtiene la fecha darle el formato requerido por Mahindra
        if (topUpSoapDateValue != null) {
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            topUpSoapResponse = output.format(topUpSoapDateValue);
        }

        return topUpSoapResponse;
    }

    // Si se encontró información del biller: MOVII o GeTrax
    // Buscar en MOVII
    private Product getProduct(String productId, String eanCode) {
        Product topUpProductSoap = null;
        List<Product> topUpProductSoapList;
        if (eanCode != null) {
            topUpProductSoapList = productRepository.findByEan(eanCode, PageRequest.of(0, 1));
            if (!topUpProductSoapList.isEmpty()) {
                topUpProductSoap = topUpProductSoapList.get(0);
            }
        } else {
            topUpProductSoap = this.productRepository.getProductById(Integer.parseInt(productId));
        }

        // Buscar en ZEUS (GeTrax)
        if (topUpProductSoap == null) {
            List<ProductGetrax> productGetraxes;
            if (eanCode != null) {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByCodeAndStatus(eanCode, GeneralStatus.ENABLED);
            } else {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByIdAndStatus(Integer.parseInt(productId), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((productGetraxes != null) && (productGetraxes.size() == 1)) {
                ProductGetrax productGetrax = productGetraxes.get(0);
                topUpProductSoap = new Product();
                topUpProductSoap.setId(Integer.parseInt(productGetrax.getId()));
                topUpProductSoap.setProductCode(String.valueOf(productGetrax.getProductCode()));
                topUpProductSoap.setEanCode(productGetrax.getCode());
                topUpProductSoap.setName(productGetrax.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((topUpProductSoap != null) && (topUpProductSoap.getEanCode().length() > 13)) {
            topUpProductSoap.setEanCode(topUpProductSoap.getEanCode().substring(0, 13));
        }

        return topUpProductSoap;
    }

    private String getResponseDate(@NotNull String responseDate) {
        final SimpleDateFormat mhFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        final SimpleDateFormat soapFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = mhFormat.parse(responseDate);
        } catch (Exception e) {
            date = new Date();
        }

        return soapFormat.format(date);
    }


}

