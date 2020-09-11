package co.moviired.digitalcontent.business.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CommandHelper;
import co.moviired.base.util.Security;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.digitalcontent.business.conf.StatusCodeConfig;
import co.moviired.digitalcontent.business.domain.StatusCode;
import co.moviired.digitalcontent.business.domain.dto.DigitalContentValidator;
import co.moviired.digitalcontent.business.domain.dto.ValidatorFactory;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.domain.entity.*;
import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import co.moviired.digitalcontent.business.domain.enums.SellerChannel;
import co.moviired.digitalcontent.business.domain.repository.*;
import co.moviired.digitalcontent.business.exception.EncryptionException;
import co.moviired.digitalcontent.business.helper.AESCrypt;
import co.moviired.digitalcontent.business.helper.PinHelper;
import co.moviired.digitalcontent.business.helper.Utilidades;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import co.moviired.digitalcontent.business.provider.*;
import co.moviired.digitalcontent.business.provider.integrator.response.ResponseIntegrator;
import co.moviired.digitalcontent.business.provider.mahindra.request.Command;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandLoginServiceResponse;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DigitalContentService implements Serializable {
    private static final long serialVersionUID = 3693234807565984299L;

    private static final Integer DEFAULT_CONFIG = 1;
    private static final String TRANSACTION_OK = "200";
    private static final String LOG_FORMATTED_2 = "{} {}";
    private static final String LBL_VALUE = "Value [{}]";
    private static final String LBL_REQUEST_START = "Solicitud de Transacción [{}]";

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;
    private final ValidatorFactory validatorFactory;
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final StatusCodeConfig statusCodeConfig;
    private final MahindraProperties mahindraProperties;

    private final PinHelper pinHelper;
    private final IUserRepository userRepository;
    private final IIncommConfigRepository incommRepository;
    private final IConciliacionRepository conciliacionRepository;
    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;
    private final ITypeOperatorRepository typeOperatorRepository;
    private final ISubtypeOperatorRepository subtypeOperatorRepository;
    private final IHomologationIncommRepository homologationIncommRepository;

    public DigitalContentService(
            @NotNull ClientFactory clientFactory,
            @NotNull ParserFactory parserFactory,
            @NotNull ValidatorFactory validatorFactory,
            @NotNull StatusCodeConfig statusCodeConfig,
            @NotNull MahindraProperties mahindraProperties,
            @NotNull IUserRepository userRepository,
            @NotNull IIncommConfigRepository incommRepository,
            @NotNull ICategoryRepository categoryRepository,
            @NotNull IConciliacionRepository conciliacionRepository,
            @NotNull IProductRepository productRepository,
            @NotNull IHomologationIncommRepository homologationIncommRepository,
            @NotNull PinHelper pinHelper,
            @NotNull ITypeOperatorRepository typeOperatorRepository,
            @NotNull ISubtypeOperatorRepository subtypeOperatorRepository) {
        super();
        this.parserFactory = parserFactory;
        this.clientFactory = clientFactory;
        this.validatorFactory = validatorFactory;
        this.statusCodeConfig = statusCodeConfig;
        this.pinHelper = pinHelper;

        this.mahindraProperties = mahindraProperties;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.incommRepository = incommRepository;
        this.conciliacionRepository = conciliacionRepository;
        this.homologationIncommRepository = homologationIncommRepository;
        this.typeOperatorRepository = typeOperatorRepository;
        this.subtypeOperatorRepository = subtypeOperatorRepository;

        // JSON/XML Mapper
        this.xmlMapper = new XmlMapper();
        this.objectMapper = new ObjectMapper();
    }

    // ECHO: Alive Method!!!
    final Mono<String> getEcho() {
        log.info("I'm alive!!!");
        return Mono.just("OK");
    }

    //METHODS SERVICE

    public Mono<DigitalContentResponse> service(@NotNull ServerHttpRequest httpRequest,
                                                @NotNull OperationType opType,
                                                @NotNull Mono<DigitalContentRequest> brequest,
                                                String userpass,
                                                String merchantId,
                                                String posId) {

        AtomicReference<DigitalContentRequest> requestFormatDigitalContent = new AtomicReference<>();
        AtomicReference<Conciliacion> conciliacionAtomicReference = new AtomicReference<>();

        return brequest.flatMap(request -> {
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            if (request.getIp() == null && httpRequest.getRemoteAddress() != null) {
                request.setIp(httpRequest.getRemoteAddress().getHostString());
            }
            this.asignarCorrelativo(request);
            requestFormatDigitalContent.set(request);

            /* 1 */
            // VALIDATE PARAM INPUT
            try {

                log.info(LBL_REQUEST_START, opType.name());
                String requestIn = printHeader(userpass, merchantId, posId) + " -> Request: " + Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), "pin", "mpin", "otp", "password");
                log.info(LBL_VALUE, requestIn);

            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }

            // A. Validar datos obligatorios
            return validateParamInput(opType, request, userpass, merchantId, posId);

        }).flatMap(request -> {
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(requestFormatDigitalContent.get());

            /* 2 */
            // B. Se guarda la petición para conciliar
            requestFormatDigitalContent.set(request);

            if ((opType.equals(OperationType.DIGITAL_CONTENT_PINES_INACTIVATE)) || (opType.equals(OperationType.DIGITAL_CONTENT_CARD_INACTIVATE))) {
                // B-1. Se genera un nuevo correlativo.
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                Conciliacion conciliacion = new Conciliacion();
                conciliacion.setCorrelationId(request.getCorrelationId());
                conciliacion.setCorrelationIdR(request.getCorrelationIdR());
                conciliacion.setCliente(request.getMsisdn1());
                conciliacion.setIdTransacion(String.valueOf(timestamp.getTime()).substring(1));

                try {
                    conciliacion = this.conciliacionRepository.save(conciliacion);
                    conciliacionAtomicReference.set(conciliacion);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                request.setCorrelationId(conciliacion.getIdTransacion());
            }

            return Mono.just(request);

        }).flatMap(request -> {
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(requestFormatDigitalContent.get());

            /* 3 */
            // C. Conservar petición original
            requestFormatDigitalContent.set(request);

            // D. Realizar autenticación contra Mahindra
            return authMahindra(request);

        }).flatMap(responseMahindraAuntentication -> {
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(requestFormatDigitalContent.get());

            /* 4 */
            //  E. Ejecutar petición al proveedor (Mahindra)
            return excecuteProvider(requestFormatDigitalContent.get(), opType, responseMahindraAuntentication);

        }).flatMap(resp -> {
            // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
            this.asignarCorrelativo(requestFormatDigitalContent.get());

            /* 5 */
            Conciliacion conciliacion = conciliacionAtomicReference.get();
            DigitalContentRequest data = requestFormatDigitalContent.get();
            try {
                // F. Homologar Respuesta
                StatusCode statusCode = statusCodeConfig.of(resp.getErrorCode(), resp.getErrorMessage());
                if (conciliacion != null && StatusCode.Level.FAIL.equals(statusCode.getLevel())) {
                    // F-1. Actualizar en conciliación error devuelto por Integrador
                    conciliacion.setMessageIc(resp.getErrorMessage());
                    conciliacion.setStatusIc(statusCode.getCode());
                    this.conciliacionRepository.save(conciliacion);
                    throw new DataException(statusCode.getExtCode(), statusCode.getMessage());
                }

                // G. Procesamiento adicional por tipo de petición.
                ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                switch (opType) {
                    case DIGITAL_CONTENT_PINES_SALE:
                        if (resp.getErrorCode().equalsIgnoreCase("00")) {
                            taskThread.submit(() -> executeResponseProviderPines(data, resp));
                        }
                        break;

                    case DIGITAL_CONTENT_CARD_INACTIVATE:
                    case DIGITAL_CONTENT_PINES_INACTIVATE:
                        if (resp.getErrorCode().equalsIgnoreCase("00")) {
                            taskThread.submit(() -> {
                                if (conciliacion != null) {
                                    conciliacion.setMessageIc("Success");
                                    conciliacion.setStatusIc(statusCode.getCode());
                                    this.conciliacionRepository.save(conciliacion);

                                    processReverseTransactionMahindra(data, conciliacion);
                                }
                            });
                        }
                        break;

                    default:
                        log.debug("Operación: {}", opType);
                }

                log.info(LOG_FORMATTED_2, "Respuesta del servicio:", Security.printIgnore(this.objectMapper.writer().writeValueAsString(resp), "pin", "mpin", "otp", "password"));


            } catch (ServiceException | JsonProcessingException e) {
                return Mono.error(e);
            }

            return Mono.just(resp);

        }).onErrorResume(e -> {
            StatusCode statusCode = new StatusCode();
            statusCode.setLevel(StatusCode.Level.FAIL);

            if (e instanceof ServiceException) {
                statusCode = statusCodeConfig.of(((ServiceException) e).getCode(), e.getMessage());

            } else {
                statusCode.setCode(StatusCode.Level.FAIL.value());
                statusCode.setMessage(e.getMessage());
            }

            DigitalContentResponse resp = new DigitalContentResponse(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING);
            try {

                log.error(LOG_FORMATTED_2, "Respuesta del servicio:", Security.printIgnore(this.objectMapper.writer().writeValueAsString(resp), "pin", "mpin", "otp", "password"));

            } catch (JsonProcessingException ex) {
                return Mono.just(resp);
            }

            return Mono.just(resp);
        });

    }

    /* 1 */
    private Mono<DigitalContentRequest> validateParamInput(@NotNull OperationType opType,
                                                           @NotNull DigitalContentRequest brequest,
                                                           String userpass,
                                                           String merchantId,
                                                           String posId) {
        try {
            // Validar datos obligatorios
            DigitalContentValidator validator = validatorFactory.getValidator(opType);
            validator.validationInput(brequest, merchantId, posId, userpass);

            // Busca el productCode de BBDD Mahindra
            Product p = productRepository.findTopByEanCodeStartingWith(brequest.getEanCode());
            if (p == null) {
                throw new DataException("-2", "No se encontró producto relacionado al eanCode");
            }
            brequest.setProductId(p.getProductCode());

            // Homologa el incommCode
            if (brequest.getIncommCode() == null || brequest.getIncommCode().isEmpty()) {
                // Establecer la RED
                String network = brequest.getSource();
                if ((brequest.getNetwork() != null) && (!brequest.getNetwork().trim().isEmpty())) {
                    network = brequest.getNetwork().trim();
                }

                // Buscar la homologación en BD
                HomologationIncomm hIncomm = homologationIncommRepository.findByNetwork(network);
                if (hIncomm == null) {
                    throw new DataException("-2", "No se encontró el campo incommCode");
                }
                brequest.setIncommCode(hIncomm.getIncommCode());
            }

            return Mono.just(brequest);

        } catch (ParsingException | DataException e) {
            return Mono.error(new DataException(e.getMessage(), e));
        }
    }

    /* 2 */
    private Mono<CommandLoginServiceResponse> authMahindra(DigitalContentRequest request) {
        try {
            // D. Realizar autenticación contra Mahindra
            ReactiveConnector clientLogin = clientFactory.getClient(OperationType.LOGIN_USER);
            IParser loginParser = this.parserFactory.getParser(OperationType.LOGIN_USER);
            IRequest mhRequest = loginParser.parseRequest(request);
            log.info(LOG_FORMATTED_2, "Request Login Mahindra:", CommandHelper.printIgnore(this.xmlMapper.writeValueAsString(mhRequest).toUpperCase().replace("\n", "").replace("\r", ""), "MPIN", "PIN", "OTP"));

            String mhCommand = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
            return clientLogin.post(mhCommand, String.class, MediaType.APPLICATION_XML, null)
                    .flatMap(responseMahindraAuntentication -> {
                        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
                        this.asignarCorrelativo(request);
                        return validateAuthMahindra((String) responseMahindraAuntentication);
                    });
        } catch (ParsingException | JsonProcessingException e) {
            return Mono.error(new DataException(e.getMessage(), e));
        }

    }

    /* 2 */
    private Mono<CommandLoginServiceResponse> validateAuthMahindra(String responseMahindraAuntentication) {
        CommandLoginServiceResponse mhResponse;
        try {
            // E. Transformar XML response a Mahindra response
            String mhr = responseMahindraAuntentication.replace("\n", "").replace("\r", "");

            mhResponse = this.xmlMapper.readValue(responseMahindraAuntentication, CommandLoginServiceResponse.class);
            log.info(LOG_FORMATTED_2, "Response Login Mahindra: ", mhr);

            if (!TRANSACTION_OK.equals(mhResponse.getTxnstatus())) {
                throw new DataException("10", "Error autenticando el servicio.");
            }

        } catch (Exception | DataException e) {
            return Mono.error(e);
        }

        return Mono.just(mhResponse);
    }

    /* 3 */
    private Mono<DigitalContentResponse> excecuteProvider(DigitalContentRequest digitalContentRequest,
                                                          OperationType opType,
                                                          CommandLoginServiceResponse respAuthMH) {
        try {
            ReactiveConnector provider = clientFactory.getClient(opType);
            IParser parser = this.parserFactory.getParser(opType);
            IRequest irequest = parser.parseRequest(digitalContentRequest, respAuthMH);
            log.info(LOG_FORMATTED_2, "Request 'UONLNBP' Mahindra:", CommandHelper.printIgnore(this.xmlMapper.writeValueAsString(irequest).toUpperCase().replace("\n", "").replace("\r", ""), "MPIN", "PIN", "OTP"));

            return provider.post(getRequestString(opType, irequest), String.class, getMediaType(opType), null)
                    .flatMap(responseProvider -> {
                        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
                        this.asignarCorrelativo(digitalContentRequest);
                        digitalContentRequest.setPersonName(respAuthMH.getFirstname());
                        return validateExecuteProvider(digitalContentRequest, (String) responseProvider, opType);
                    });
        } catch (ParsingException | JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    /* 3 */
    private Mono<DigitalContentResponse> validateExecuteProvider(DigitalContentRequest data, String obResp, OperationType opType) {
        DigitalContentResponse resp;
        try {
            IParser parser = parserFactory.getParser(opType);
            // Transformar XML response a  response
            log.info(LOG_FORMATTED_2, "Response 'UONLNBP' Mahindra:", CommandHelper.printIgnore(obResp.replace("\n", "").replace("\r", "").toUpperCase(), "TRANSACTIONCODE"));
            IResponse mhResponse = readValue(opType, obResp);

            // Transformar response especifica a Banking response
            resp = parser.parseResponse(data, mhResponse);

        } catch (IOException | ParsingException e) {
            return Mono.error(e);
        }

        return Mono.just(resp);
    }

    /* 4 */
    private void executeResponseProviderPines(DigitalContentRequest data, DigitalContentResponse resp) {
        IncommConfig incommConfig = null;
        try {
            // Obtener configuración del cliente
            Optional<User> user = userRepository.findFirstByUsernameAndPassword(AESCrypt.crypt(data.getMsisdn1()), AESCrypt.crypt(data.getMpin()));
            Optional<IncommConfig> optUserConf = Optional.empty();
            if (user.isPresent()) {
                optUserConf = incommRepository.findByUser(user.get());
            }

            // Se verifica que el cliente este configurado en BD,  sino se le asigna una configuracion generica
            if (optUserConf.isPresent()) {
                incommConfig = optUserConf.get();
            } else {
                Optional<IncommConfig> config = incommRepository.findById(DEFAULT_CONFIG);
                if (config.isPresent()) {
                    incommConfig = config.get();
                }
            }

            // Se guarda el pin en el historial
            this.pinHelper.guardarHistorialPin(resp, data, incommConfig);

            // Se desencripta el pin encode sistema
            resp.setPin(this.pinHelper.desencriptarPinUsuario(resp.getPin()));

            // Notificar
            this.pinHelper.notificarPin(resp, data, incommConfig);

            // Encriptar pin encode usuaria
            if (incommConfig != null && incommConfig.isSendPin() && incommConfig.isEncryptPin()) {
                this.pinHelper.encriptarPinUsuario(resp, incommConfig);
            }

        } catch (EncryptionException e) {
            log.error("ERROR en 'ExecuteResponseProviderPines'. Cause: {}", e.getMessage());
        }
    }

    private void processReverseTransactionMahindra(DigitalContentRequest data, Conciliacion conciliacion) {
        Command command = new Command();
        ResponseEntity<String> response;
        // Armar la petición
        try {
            int retries = this.mahindraProperties.getRetries();
            int delay = this.mahindraProperties.getDelay();
            IRequest mhRequest = command.parseRequestReverseTransactionMahindra(data, this.mahindraProperties);
            log.info("Se generaran " + retries + " intentos de reversión.");

            for (int i = 0; i < retries; ++i) {
                log.info("Intento de Reversión número: #" + (i + 1));
                response = mhReverse(mhRequest, conciliacion);
                if (response != null) {
                    log.info(LOG_FORMATTED_2, "Response 'REFUNDW2W' Mahindra:", CommandHelper.printIgnore(this.xmlMapper.writer().writeValueAsString(response).replace("\n", "").replace("\r", "").toUpperCase(), "pin", "mpin", "otp", "TRANSACTIONCODE"));
                    CommandResponse responseMh = this.xmlMapper.readValue(response.getBody(), CommandResponse.class);

                    if (TRANSACTION_OK.equals(responseMh.getTxnstatus())) {
                        log.info("Reversión Exitosa");
                        conciliacion.setTransferId(responseMh.getTxnidOrg());
                        conciliacion.setStatusMh(responseMh.getTxnstatus());
                        conciliacion.setMessageMh(responseMh.getMessage());
                        conciliacion.setTransferIdRevert(responseMh.getTxnid());
                        this.conciliacionRepository.save(conciliacion);
                        break;
                    } else {
                        conciliacion.setMessageMh(responseMh.getMessage());
                        conciliacion.setStatusMh(responseMh.getTxnstatus());
                        this.conciliacionRepository.save(conciliacion);
                    }

                    log.info("Reversion Fallida");
                }

                this.sleep(delay);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // Leer la respuesta especifica en el COMMAND general
    private IResponse readValue(@NotNull OperationType opType, @NotNull String mhr) throws ParsingException, IOException {
        IResponse response;

        switch (opType) {
            case DIGITAL_CONTENT_CARD_ACTIVATE:
            case DIGITAL_CONTENT_PINES_SALE:
                response = this.xmlMapper.readValue(mhr, CommandResponse.class);
                break;

            case LOGIN_USER:
                response = this.xmlMapper.readValue(mhr, CommandLoginServiceResponse.class);
                break;

            case DIGITAL_CONTENT_CARD_INACTIVATE:
                response = this.objectMapper.readValue(mhr, ResponseIntegrator.class);
                break;

            default:
                throw new ParsingException("99", "Operación inválida");
        }
        return response;
    }

    // GET CATEGORY

    public final Mono<DigitalContentResponse> getCategories(SellerChannel source, SubtypeOperator subType) {
        Mono<DigitalContentResponse> response = Mono.empty();
        try {
            DigitalContentResponse resp = new DigitalContentResponse();

            // Generar el identificador único de operación
            asignarCorrelativo("");

            log.info(LBL_REQUEST_START, "GET CATEGORY");
            log.info(LBL_VALUE, "CATEGORIES - SubType: " + subType.getSubtype());

            // Buscar las categorias
            List<Category> categories = categoryRepository.findAllByStatus(GeneralStatus.ENABLED);
            if (categories.isEmpty()) {
                resp.setCategories(null);
                resp.setErrorCode(ErrorType.DATA.name());
                resp.setErrorMessage("No existen categorias registradas");
                log.info("<== RESPONSE - GET CATEGORIES ALL [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response), "pin", "mpin", "otp", "password") + "]");
                response = Mono.just(resp);
            }

            // Quitar las categorías no activas
            categories = categories
                    .stream()
                    .filter(c -> (source == null || c.getSellers() == null || c.getSellers().isEmpty() || c.getSellers().contains(source)))
                    .sorted(Comparator.comparing(Category::getName))
                    .collect(Collectors.toList());

            // Buscar los productos x categoría
            for (Category category : categories) {
                List<Product> products = this.productRepository.getProductByCategoryAndTsubType(category, subType.getId());
                if (products != null && !products.isEmpty()) {
                    // Ordenar x Monto
                    products = products
                            .stream()
                            .filter(p -> GeneralStatus.ENABLED.equals(p.getStatus()))
                            .filter(p -> (source == null || p.getSellers() == null || p.getSellers().isEmpty() || p.getSellers().contains(source)))
                            .sorted(Comparator.comparing(Product::getMinValue))
                            .map(p -> {
                                try {
                                    Optional<TypeOperator> type = this.typeOperatorRepository.findById(p.getTtype());
                                    type.ifPresent(typeOperator -> p.setType(typeOperator.getType()));
                                    Optional<SubtypeOperator> stype = this.subtypeOperatorRepository.findById(p.getTsubType());
                                    stype.ifPresent(subtypeOperator -> p.setSubType(subtypeOperator.getSubtype()));
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                }
                                return p;
                            })
                            .collect(Collectors.toList());
                }
                category.setProducts(products);
                category.toPublic();
            }

            // Quitar las categorías sin productos
            categories = categories
                    .stream()
                    .filter(c -> !c.getProducts().isEmpty())
                    .collect(Collectors.toList());

            // Obtener la respuesta
            resp.setCategories(categories);
            resp.setErrorCode(TRANSACTION_OK);
            resp.setErrorMessage("SUCESSFUL");

            log.info("<== RESPONSE - GET CATEGORIES ALL [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response), "pin", "mpin", "otp", "password") + "]");
            response = Mono.just(resp);

        } catch (Exception e) {
            log.error(e.getMessage());
            StatusCode statusCode = statusCodeConfig.newInstance(StatusCode.Level.FAIL, StatusCode.Level.FAIL.value(), e.getMessage());
            response = Mono.just(new DigitalContentResponse(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING));
        }

        return response;
    }

    public final DigitalContentResponse getCategory(SellerChannel source, @NotNull Integer id, SubtypeOperator subType) {
        // Decodificar el Header
        DigitalContentResponse response = new DigitalContentResponse();
        try {
            // Generar el identificador único de operación
            asignarCorrelativo("");

            log.info(LBL_REQUEST_START, "GET CATEGORY");
            log.info(LBL_VALUE, id);

            // Enviar la petición
            Optional<Category> ocategory = categoryRepository.findByIdAndStatus(id, GeneralStatus.ENABLED);
            if (!ocategory.isPresent()) {
                response.setCategory(null);
                response.setErrorCode(ErrorType.DATA.name());
                response.setErrorMessage("La categoría indicada no existe o no está habilitada");

                return response;
            }

            Category category = ocategory.get();
            if (category.getSellers() != null && !category.getSellers().isEmpty() && !category.getSellers().contains(source)) {
                response.setCategory(null);
                response.setErrorCode(ErrorType.DATA.name());
                response.setErrorMessage("La categoría indicada no existe o no está habilitada");
                return response;
            }

            List<Product> products = this.productRepository.getProductByCategoryAndTsubType(category, subType.getId());
            if (products != null && !products.isEmpty()) {
                // Ordenar x Monto
                products = products
                        .stream()
                        .filter(p -> GeneralStatus.ENABLED.equals(p.getStatus()))
                        .filter(p -> (source == null || p.getSellers() == null || p.getSellers().isEmpty() || p.getSellers().contains(source)))
                        .sorted(Comparator.comparing(Product::getMinValue))
                        .map(p -> {
                            try {
                                Optional<TypeOperator> type = this.typeOperatorRepository.findById(p.getTtype());
                                type.ifPresent(typeOperator -> p.setType(typeOperator.getType()));
                                Optional<SubtypeOperator> stype = this.subtypeOperatorRepository.findById(p.getTsubType());
                                stype.ifPresent(subtypeOperator -> p.setSubType(subtypeOperator.getSubtype()));
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                            return p;
                        })
                        .collect(Collectors.toList());
            }
            category.setProducts(products);
            category.toPublic();

            response.setCategory(category);
            response.setErrorCode(TRANSACTION_OK);
            response.setErrorMessage("SUCESSFUL");

            log.info("==> RESPONSE - GET CATEGORY [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response), "pin", "mpin", "otp", "password") + "]");

        } catch (Exception e) {
            log.error(e.getMessage());
            StatusCode statusCode = statusCodeConfig.newInstance(StatusCode.Level.FAIL, StatusCode.Level.FAIL.value(), e.getMessage());
            response = new DigitalContentResponse(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING);
        }

        return response;
    }

    // Devolver la petición en su representación String: XML, JSON; según el tipo de operación
    private String getRequestString(OperationType opType, IRequest mhRequest) throws JsonProcessingException {
        String hr = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
        if (opType.equals(OperationType.DIGITAL_CONTENT_CARD_INACTIVATE)) {
            hr = this.objectMapper.writer().writeValueAsString(mhRequest);
        }

        return hr;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    private MediaType getMediaType(OperationType opType) {
        MediaType type = MediaType.APPLICATION_XML;
        if (opType.equals(OperationType.DIGITAL_CONTENT_CARD_INACTIVATE)) {
            type = MediaType.APPLICATION_JSON;
        }

        return type;
    }

    // Crear el CorrelationID
    private String asignarCorrelativo(String correlation) {
        String cId = correlation;
        if (correlation == null || correlation.isEmpty()) {
            cId = Utilidades.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
        return cId;
    }

    private void asignarCorrelativo(@NotNull DigitalContentRequest request) {
        request.setCorrelationId(asignarCorrelativo(request.getCorrelationId()));
        log.debug("Se genera correlativo----> {}", request.getCorrelationId());
        MDC.putCloseable("correlation-id", request.getCorrelationId());
    }

    private String printHeader(String userpass, String merchantId, String posId) {
        return "Header: "
                .concat("{\"userpass\":\"")
                .concat(userpass.contains(":") ? userpass.substring(0, userpass.indexOf(':')) : userpass)
                .concat("\", \"merchantId\":\"")
                .concat(merchantId)
                .concat("\", \"posId\":\"")
                .concat(posId)
                .concat("\"}");
    }

    private ResponseEntity<String> mhReverse(@NotNull IRequest mhRequest, @NotNull Conciliacion conciliacion) {
        ResponseEntity<String> response = null;
        try {
            // Emascar el usuario y la clave en el LOG
            log.info(LOG_FORMATTED_2, "Request 'REFUNDW2W' Mahindra:", CommandHelper.printIgnore(this.xmlMapper.writeValueAsString(mhRequest).toUpperCase().replace("\n", "").replace("\r", ""), "MPIN", "PIN", "OTP"));

            // Enviar la petición
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<String> request = new HttpEntity<>(this.xmlMapper.writeValueAsString(mhRequest), headers);
            response = restTemplate.postForEntity(mahindraProperties.getUrlTransactional(), request, String.class);

        } catch (Exception e) {
            log.error("Error invocando a mahindra 'REFUNDW2W'. Causa: {}", e.getMessage());
            conciliacion.setMessageMh("Error invocando a mahindra 'REFUNDW2W'");
            conciliacion.setStatusMh("-2");
            this.conciliacionRepository.save(conciliacion);
        }

        return response;
    }

}


