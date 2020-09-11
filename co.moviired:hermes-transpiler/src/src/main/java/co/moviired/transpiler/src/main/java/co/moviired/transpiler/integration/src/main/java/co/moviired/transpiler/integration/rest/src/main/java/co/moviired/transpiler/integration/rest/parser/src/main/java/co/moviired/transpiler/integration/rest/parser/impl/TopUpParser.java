package co.moviired.transpiler.integration.rest.parser.impl;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.integration.rest.dto.topup.request.RequestTopUpDTO;
import co.moviired.transpiler.integration.rest.dto.topup.response.Data;
import co.moviired.transpiler.integration.rest.dto.topup.response.ResponseTopUpDTO;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("topUpParserRest")
public class TopUpParser implements IHermesParser {

    private static final long serialVersionUID = 2626608527218126568L;

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

    // SERVICE METHODS

    @Override
    public final IHermesRequest parseRequest(@NotBlank String request) throws ParseException {
        try {
            // Obtener el DTO de la petición
            RequestTopUpDTO topUpRestReq = new ObjectMapper().readValue(request, RequestTopUpDTO.class);

            // Datos especificos de la transaccion

            // Cliente (Usuario y Clave)
            String tupUpRestClientName = "";
            String tupUpRestuserREQ = topUpRestReq.getMeta().getUserName();
            String tupUpRestpassREQ = topUpRestReq.getMeta().getPasswordHash();
            String tupUpResttimeZone = "";
            if (tupUpRestuserREQ == null) {
                tupUpRestuserREQ = "";
            }
            if (tupUpRestpassREQ == null) {
                tupUpRestpassREQ = "";
            }
            Optional<User> tuser = userRepository.findByGetraxUsername(AESCrypt.crypt(tupUpRestuserREQ));
            if (tuser.isPresent()) {
                User user = tuser.get();
                // Verificar: clave y estado del usuario y del cliente
                if ((user.getGetraxPassword().equals(AESCrypt.crypt(tupUpRestpassREQ)))
                        && (user.getStatus().equals(GeneralStatus.ENABLED))
                        && (user.getClient().getStatus().equals(GeneralStatus.ENABLED))) {

                    tupUpRestuserREQ = AESCrypt.decrypt(user.getMahindraUsername());
                    tupUpRestpassREQ = AESCrypt.decrypt(user.getMahindraPassword());
                    tupUpRestpassREQ = tupUpRestpassREQ.replaceFirst(user.getId().toString(), "");
                    tupUpRestClientName = user.getClient().getName();
                    tupUpResttimeZone = user.getClient().getTimeZone();
                }
            }
            ClientHermes clientHermesRest = new ClientHermes(tupUpRestClientName, tupUpRestuserREQ, tupUpRestpassREQ, tupUpResttimeZone);

            // Producto (id, EANCode, MSISDN2 -RechargeNumber- )
            String topUpRestProductId = topUpRestReq.getData().getProductId();
            String topUpRestProductCode = "0";
            String topUpRestProductName = "0";
            String topUpRestEancode = topUpRestReq.getData().getEANCode();
            ProductType topUpRestType = ProductType.OTHER;

            // Si se encontró información del producto: MOVII o GeTrax
            Product product = this.getProduct(topUpRestProductId, topUpRestEancode);
            if (product != null) {
                topUpRestEancode = product.getEanCode();
                topUpRestProductId = String.valueOf(product.getOperatorId());
                topUpRestProductCode = product.getProductCode();
                topUpRestProductName = product.getName();
                topUpRestType = product.getType();
            }
            ProductHermes productHermesRest = new ProductHermes(topUpRestProductId, topUpRestProductCode, topUpRestEancode, topUpRestProductName, topUpRestType);

            // Recharge number y Monto
            String rechargeNumber = topUpRestReq.getData().getDestinationNumber();
            Integer amount = Integer.parseInt(topUpRestReq.getData().getAmount());

            // CajeroID
            String cashierId = topUpRestReq.getMeta().getDeviceCode();
            // MerchantID
            String merchantId = topUpRestReq.getMeta().getUserName();
            // DeviceID
            String deviceId = topUpRestReq.getMeta().getDeviceCode();
            // RequestDate
            String requestDate = topUpRestReq.getMeta().getRequestDate();
            requestDate = topUpParseDateRest(requestDate);

            // Armar el CommandValidateBillByReference
            TopUpHermesRequest topup = new TopUpHermesRequest();
            topup.setOriginalRequest(request);
            topup.setClient(clientHermesRest);
            topup.setProduct(productHermesRest);
            topup.setDate(topUpRestReq.getData().getCustomerDate());
            topup.setRechargeNumber(rechargeNumber);
            topup.setAmount(amount);

            topup.setMerchantId(merchantId);
            topup.setDeviceId(deviceId);
            topup.setRequestDate(requestDate);
            topup.setCashierId(cashierId);

            // Se agrega el número de Transaccón del cliente
            topup.setClientTxnId(topUpRestReq.getData().getCustomerTxReference());

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
            TopUpHermesResponse topUpRestResponse = (TopUpHermesResponse) hermesResponse;

            // Estado de la transacción
            Data topUpRestdata = null;
            Outcome topUpRestOutcome = new Outcome();
            topUpRestOutcome.setStatusCode(topUpRestResponse.getResponse().getStatusCode());
            topUpRestOutcome.setMessage(topUpRestResponse.getResponse().getStatusMessage());

            // Armar el ERROR
            Error error = new Error();
            error.setErrorType("0");
            if ((topUpRestResponse.getResponse().getErrorCode() != null) && (!topUpRestResponse.getResponse().getErrorCode().trim().isEmpty())) {
                error.setErrorCode(topUpRestResponse.getResponse().getErrorCode());
                error.setErrorMessage(topUpRestResponse.getResponse().getErrorMessage());
            } else {
                error.setErrorCode(topUpRestResponse.getResponse().getStatusCode());
                error.setErrorMessage(topUpRestResponse.getResponse().getStatusMessage());
            }
            topUpRestOutcome.setError(error);

            // Si la respuesta es OK, armar el detalle
            if (error.getErrorCode().equals("200")) {
                topUpRestdata = new Data();
                topUpRestdata.setCustomerDate(topUpRestResponse.getCustomerDate());
                topUpRestdata.setTransactionDate(topUpRestResponse.getTransactionDate());
                topUpRestdata.setAuthorizationCode(topUpRestResponse.getAuthorizationNumber());
                topUpRestdata.setTransactionCode(topUpRestResponse.getTransactionCode());
                topUpRestdata.setCustomerBalance(topUpRestResponse.getNewBalance());
                topUpRestdata.setExpirationDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
                topUpRestdata.setInvoiceNumber((topUpRestResponse.getTxnId() != null) ? topUpRestResponse.getTxnId().toUpperCase() : "");
                topUpRestdata.setSubProductCode(topUpRestResponse.getSubProductCode());

                // Establecer el código respuesta 00 al cliente
                error.setErrorCode("00");
            }

            // Armar la respuesta completa
            ResponseTopUpDTO topUpRestresponseDTO = new ResponseTopUpDTO();
            topUpRestresponseDTO.setOutcome(topUpRestOutcome);
            topUpRestresponseDTO.setData(topUpRestdata);

            return new ObjectMapper().writeValueAsString(topUpRestresponseDTO);

        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    private String topUpParseDateRest(String requestDate) {
        String topUpRestRetorno = requestDate;
        SimpleDateFormat topUpRestInput;
        Date topUpRestDateValue;

        try {
            // Formato principal
            topUpRestInput = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            topUpRestDateValue = topUpRestInput.parse(requestDate);

        } catch (java.text.ParseException e) {
            try {
                // Formato alterno
                topUpRestInput = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                topUpRestDateValue = topUpRestInput.parse(requestDate);

            } catch (java.text.ParseException ex) {
                topUpRestDateValue = null;
            }
        }

        // Si se obtiene la fecha darle el formato requerido por Mahindra
        if (topUpRestDateValue != null) {
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            topUpRestRetorno = output.format(topUpRestDateValue);
        }

        return topUpRestRetorno;
    }

    // Si se encontró información del biller: MOVII o GeTrax
    // Buscar en MOVII
    private Product getProduct(String productId, String eanCode) {
        Product topUpRestProduct = null;
        List<Product> topUpRestProductList;
        if (eanCode != null) {
            topUpRestProductList = productRepository.findByEan(eanCode, PageRequest.of(0, 1));
            if (!topUpRestProductList.isEmpty()) {
                topUpRestProduct = topUpRestProductList.get(0);
            }
        } else {
            topUpRestProduct = this.productRepository.getProductById(Integer.parseInt(productId));
        }

        // Buscar en ZEUS (GeTrax)
        if (topUpRestProduct == null) {
            List<ProductGetrax> productGetraxes;
            if (eanCode != null) {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByCodeAndStatus(eanCode, GeneralStatus.ENABLED);
            } else {
                productGetraxes = this.productGetraxRepository.findProductGetraxesByIdAndStatus(Integer.parseInt(productId), GeneralStatus.ENABLED);
            }

            // Se verifica que sólo se consigua un elemento. si hay más de uno no se toma valor
            if ((productGetraxes != null) && (productGetraxes.size() == 1)) {
                ProductGetrax productGetrax = productGetraxes.get(0);
                topUpRestProduct = new Product();
                topUpRestProduct.setId(Integer.parseInt(productGetrax.getId()));
                topUpRestProduct.setProductCode(String.valueOf(productGetrax.getProductCode()));
                topUpRestProduct.setEanCode(productGetrax.getCode());
                topUpRestProduct.setName(productGetrax.getName());
            }
        }

        // Verificar el EAN a 13 digitos
        if ((topUpRestProduct != null) && (topUpRestProduct.getEanCode().length() > 13)) {
            topUpRestProduct.setEanCode(topUpRestProduct.getEanCode().substring(0, 13));
        }

        return topUpRestProduct;
    }

}

