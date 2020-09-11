package co.moviired.acquisition.service;

import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.model.dto.ResponseStatus;
import co.moviired.acquisition.common.model.exceptions.ComponentThrowable;
import co.moviired.acquisition.common.provider.mahindra.MahindraConnector;
import co.moviired.acquisition.common.service.IService;
import co.moviired.acquisition.common.util.AESGCMHelper;
import co.moviired.acquisition.common.util.StatusCodesHelper;
import co.moviired.acquisition.common.util.UtilsHelper;
import co.moviired.acquisition.config.ComponentProperties;
import co.moviired.acquisition.model.ComponentUser;
import co.moviired.acquisition.model.dto.*;
import co.moviired.acquisition.model.entity.ProductCode;
import co.moviired.acquisition.model.entity.Transaction;
import co.moviired.acquisition.model.enums.*;
import co.moviired.acquisition.model.enums.TimeZone;
import co.moviired.acquisition.repository.RepositoryContainer;
import co.moviired.acquisition.util.ContainerSecurityHelper;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Generator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.constraints.NotNull;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.*;
import static co.moviired.acquisition.util.ConstantsHelper.*;
import static co.moviired.acquisition.util.IncommStatusHelper.*;

@Slf4j
@Service
public class AcquisitionService extends IService {

    private final MahindraConnector mahindraConnector;
    private final ComponentProperties componentProperties;

    private final RepositoryContainer repositoryContainer;
    private final ContainerSecurityHelper containerSecurityHelper;

    private final SimpleDateFormat dateTimeFormat;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    private final String cryptoKey;

    public AcquisitionService(@NotNull GlobalProperties globalProperties,
                              @NotNull StatusCodeConfig statusCodeConfig,
                              @NotNull ComponentProperties componentPropertiesI,
                              @NotNull MahindraConnector mahindraConnectorI,
                              @NotNull RepositoryContainer repositoryContainerI,
                              @NotNull ContainerSecurityHelper containerSecurityHelperI) {
        super(globalProperties, statusCodeConfig);

        this.componentProperties = componentPropertiesI;
        this.mahindraConnector = mahindraConnectorI;

        this.repositoryContainer = repositoryContainerI;
        this.containerSecurityHelper = containerSecurityHelperI;

        this.dateTimeFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
        this.dateFormat = new SimpleDateFormat(YYYYMMDD);
        this.timeFormat = new SimpleDateFormat(HHMMSS);

        this.cryptoKey = PIN_CRYPTO_KEY;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AcquisitionService)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AcquisitionService that = (AcquisitionService) o;
        return Objects.equals(componentProperties, that.componentProperties);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), componentProperties);
    }

    // PRIMARY METHODS *************************************************************************************************

    final Mono<IComponentDTO> incommRequest(@NotNull String correlative, @NotNull String authorizationHeader, @NotNull AcquisitionDTO req) {
        // Validate user
        AcquisitionDTO response = new AcquisitionDTO();
        return Mono.just(req)
                .flatMap(request -> startResponse(correlative, request, response))
                // Do login
                .flatMap(resp -> authentication(correlative, UtilsHelper.getMethodName(), authorizationHeader, response, true))
                .flatMap(resp -> {
                    if (response.getStatus() != null) {
                        return Mono.error(new ComponentThrowable(ErrorType.PROCESSING, getStatusCodeConfig().of(INVALID_USER).getMessage(),
                                INVALID_USER, getGlobalProperties().getName()));
                    }

                    // Validate request
                    return validateRequest(Mono.just(req), response)
                            // Execute method
                            .flatMap(res -> selectMethod(correlative, Mono.just(req), response));
                })
                .flatMap(resp -> endResponse(req, response, null))
                .onErrorResume(error -> endResponse(req, response, error));
    }

    final Mono<IComponentDTO> productValidation(@NotNull String correlative, @NotNull String authorizationHeader, @NotNull String productIdentifier, @NotNull AcquisitionDTO req) {
        return productValidateOrRedeem(correlative, authorizationHeader, productIdentifier, req, false, UtilsHelper.getMethodName());
    }

    final Mono<IComponentDTO> productRedeem(@NotNull String correlative, @NotNull String authorizationHeader, @NotNull String productIdentifier, @NotNull AcquisitionDTO req) {
        return productValidateOrRedeem(correlative, authorizationHeader, productIdentifier, req, true, UtilsHelper.getMethodName());
    }

    final Mono<IComponentDTO> productCodesCreation(@NotNull String correlative, @NotNull String authorizationHeader, @NotNull String productIdentifier, @NotNull AcquisitionDTO req) {
        // Validate user
        return Mono.just(req).flatMap(request -> authentication(correlative, UtilsHelper.getMethodName(), authorizationHeader, new AcquisitionDTO(), true))
                .flatMap(response -> {
                    if (response.getStatus() != null) {
                        return Mono.just(response);
                    }

                    // Execute method
                    return createCodes(correlative, productIdentifier, Mono.just(req), response);
                });
    }

    final Mono<IComponentDTO> getLotsIdentifiers(@NotNull String correlative, @NotNull String authorizationHeader) {
        // Validate user
        return Mono.just(correlative).flatMap(c -> authentication(correlative, UtilsHelper.getMethodName(), authorizationHeader, new AcquisitionDTO(), true))
                .flatMap(response -> {
                    if (response.getStatus() != null) {
                        return Mono.just(response);
                    }

                    // Execute method
                    return getLotsIdentifiers(correlative, response);
                });
    }

    final Mono<IComponentDTO> getProductCodes(@NotNull String correlative, @NotNull String authorizationHeader, @NotNull AcquisitionDTO req) {
        // Validate user
        return Mono.just(req).flatMap(request -> authentication(correlative, UtilsHelper.getMethodName(), authorizationHeader, new AcquisitionDTO(), true))
                .flatMap(response -> {
                    if (response.getStatus() != null) {
                        return Mono.just(response);
                    }

                    // Execute method
                    return getProductCodes(correlative, Mono.just(req), response);
                });
    }

    // INCOMM REQUEST **************************************************************************************************

    private Mono<AcquisitionDTO> validateRequest(@NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            log.info(VALIDATING_REQUEST);
            ComponentThrowable error = getComponentThrowableProcessing(INVALID_REQUEST);

            if (request.getServiceProviderTxn() == null
                    // First level parameters
                    || request.getServiceProviderTxn().getRequest() == null
                    || request.getServiceProviderTxn().getRequest().getMsgType() == null
                    || request.getServiceProviderTxn().getRequest().getMsgType().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo() == null
                    || request.getServiceProviderTxn().getRequest().getIncommRefNum() == null
                    || request.getServiceProviderTxn().getRequest().getIncommRefNum().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getOrigin() == null
                    || request.getServiceProviderTxn().getRequest().getProduct() == null
                    // Date Time Info parameters
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getDateValue() == null
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getDateValue().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeValue() == null
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeValue().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue() == null
                    || request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue().trim().isEmpty()
                    || (!request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue().equals(TimeZone.EST.name())
                    && !request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue().equals(TimeZone.EDT.name()))
                    // Origin parameters
                    || request.getServiceProviderTxn().getRequest().getOrigin().getMerchName() == null
                    || request.getServiceProviderTxn().getRequest().getOrigin().getMerchName().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo() == null
                    || request.getServiceProviderTxn().getRequest().getOrigin().getMerchRefNum() == null
                    || request.getServiceProviderTxn().getRequest().getOrigin().getMerchRefNum().trim().isEmpty()
                    // Store Info parameters
                    || request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getStoreID() == null
                    || request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getStoreID().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getTermID() == null
                    || request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getTermID().trim().isEmpty()
                    // Product Parameters
                    || request.getServiceProviderTxn().getRequest().getProduct().getUpc() == null
                    || request.getServiceProviderTxn().getRequest().getProduct().getUpc().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getProduct().getSpNumber() == null
                    || request.getServiceProviderTxn().getRequest().getProduct().getSpNumber().trim().isEmpty()
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue() == null
                    // Value parameters
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney() == null
                    // Money parameters
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getAmount() == null
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getAmount() <= ZERO_DOUBLE
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getCurrencyCode() == null
                    || request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getCurrencyCode().trim().isEmpty()
            ) {
                return Mono.error(error);
            }

            if (!request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getCurrencyCode().equals(CurrencyCode.COP.name())) {
                return Mono.error(getComponentThrowableProcessing(INVALID_CURRENCY_CODE));
            }

            log.info(REQUEST_VALIDATION_IS_SUCCESS);
            return Mono.just(response);
        });
    }

    // INCOMM METHODS **************************************************************************************************

    private Mono<AcquisitionDTO> selectMethod(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return validateProductCodeByCardCode(correlative, req)
                .flatMap(request -> {
                    log.info(SELECTING_TRANSACTION_TYPE);
                    switch (request.getTransactionType()) {
                        case ACTIVATION_PRE_AUTHORIZATION:
                            return activationPreAuthorization(correlative, Mono.just(request), response);
                        case ACTIVATION_AUTHORIZATION:
                            return activationAuthorization(correlative, Mono.just(request), response);
                        case ACTIVATION_REVERSAL:
                            return activationReversal(correlative, Mono.just(request), response);
                        case DEACTIVATION:
                            return deactivation(correlative, Mono.just(request), response);
                        case DEACTIVATION_REVERSAL:
                            return deactivationReversal(correlative, Mono.just(request), response);
                        default:
                            return Mono.error(getComponentThrowableProcessing(ACTION_NOT_SUPPORTED));
                    }
                });
    }

    private Mono<AcquisitionDTO> validateProductCodeByCardCode(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req) {
        return req
                .flatMap(this::getProductByProductCode)
                .flatMap(request -> startTransactionValidations(correlative, request, request.getServiceProviderTxn().getRequest().getMsgType(), false))
                .flatMap(request -> {
                    log.info(VALIDATING_CURRENCY_CODE);
                    if (!request.getProductCode().getProduct().getCurrencyCode().name()
                            .equals(request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getCurrencyCode())) {
                        return Mono.error(getComponentThrowableProcessing(INVALID_AMOUNT));
                    }

                    log.info(VALIDATING_AMOUNT);
                    if (!request.getProductCode().getProduct().getValue().equals(
                            request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getAmount())) {
                        return Mono.error(getComponentThrowableProcessing(INVALID_AMOUNT));
                    }

                    try {
                        validateProductTransactions(request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }

                    return Mono.just(request);
                });
    }

    private Mono<AcquisitionDTO> startTransactionValidations(@NotNull String correlative, @NotNull AcquisitionDTO request,
                                                             @NotNull String transaction, boolean findByPin) {

        // Find Product Code
        ProductCode productCode = getProductCode(request, findByPin, request.getProduct());
        if (productCode == null) {
            if (findByPin) {
                return Mono.error(getComponentThrowableProcessing(NOT_FOUND_CODE));
            } else {
                return Mono.error(getComponentThrowableProcessing(CARD_OR_ACCOUNT_IS_INVALID));
            }
        }
        request.setProductCode(productCode);


        log.info(VALIDATING_PRODUCT_CODE_WITH_ID, productCode.getId());

        // Validating signature
        if (isInvalidSignature(productCode)) {
            return Mono.error(getComponentThrowableProcessing(SUSPECT_FRAUD));
        }

        // Validating action
        log.info(VALIDATING_ACTION_METHOD, transaction);
        TransactionType transactionType = TransactionType.getByValue(transaction);
        if (transactionType.equals(TransactionType.UNKNOWN)) {
            log.info(ACTION_METHOD_NOT_FOUND, transaction);
            return Mono.error(getComponentThrowableProcessing(ACTION_NOT_SUPPORTED));
        }
        request.setTransactionType(transactionType);

        // Creating transaction
        if (findByPin) {
            request.setTransaction(createMoviiTransaction(correlative, productCode, transactionType));
        } else {
            request.setTransaction(createIncommTransaction(correlative, request, productCode));
        }

        return Mono.just(request);
    }

    private Mono<AcquisitionDTO> activationPreAuthorization(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return activation(correlative, req, response, false);
    }

    private Mono<AcquisitionDTO> activationAuthorization(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return activation(correlative, req, response, true);
    }

    private Mono<AcquisitionDTO> activation(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response, boolean isAuthorization) {
        return req.flatMap(request -> {
            if (isAuthorization) {
                log.info(DOING_ACTIVATION_AUTHORIZATION_CODE_WITH_ID, request.getProductCode().getId());
            } else {
                log.info(DOING_ACTIVATION_PRE_AUTHORIZATION_CODE_WITH_ID, request.getProductCode().getId());
            }
            assignCorrelative(correlative);
            switch (request.getProductCode().getStatus()) {
                case INACTIVE:
                    try {
                        validateLastTransaction(isAuthorization ? TransactionType.ACTIVATION_AUTHORIZATION : TransactionType.ACTIVATION_PRE_AUTHORIZATION, request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }
                    if (isAuthorization) {
                        changeProductCodeState(request, ProductState.ACTIVE);
                    }
                    return Mono.just(response);
                case ACTIVE:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_ALREADY_ACTIVE));
                case REDEEMED:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_REDEEMED));
                default:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
            }
        });
    }

    private Mono<AcquisitionDTO> activationReversal(@NotNull String
                                                            correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            log.info(DOING_ACTIVATION_REVERSAL_CODE_WITH_ID, request.getProductCode().getId());
            assignCorrelative(correlative);

            switch (request.getProductCode().getStatus()) {
                case ACTIVE:
                    try {
                        returnsExtraValidations(TransactionType.ACTIVATION_REVERSAL, request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }
                    changeProductCodeState(request, ProductState.INACTIVE);
                    return Mono.just(response);
                case INACTIVE:
                    return Mono.error(getComponentThrowableProcessing(CARD_IS_ALREADY_INACTIVE));
                case REDEEMED:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_REDEEMED));
                default:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
            }
        });
    }


    private Mono<AcquisitionDTO> deactivation(@NotNull String
                                                      correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            log.info(DOING_DEACTIVATION_CODE_WITH_ID, request.getProductCode().getId());
            assignCorrelative(correlative);
            switch (request.getProductCode().getStatus()) {
                case ACTIVE:
                    try {
                        returnsExtraValidations(TransactionType.DEACTIVATION, request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }
                    changeProductCodeState(request, ProductState.RETURNED);
                    return Mono.just(response);
                case REDEEMED:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_REDEEMED));
                default:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
            }
        });
    }

    private Mono<AcquisitionDTO> deactivationReversal(@NotNull String
                                                              correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            log.info(DOING_DEACTIVATION_REVERSAL_CODE_WITH_ID, request.getProductCode().getId());
            assignCorrelative(correlative);
            switch (request.getProductCode().getStatus()) {
                case RETURNED:
                    try {
                        returnsExtraValidations(TransactionType.DEACTIVATION_REVERSAL, request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }
                    changeProductCodeState(request, ProductState.ACTIVE);
                    return Mono.just(response);
                case ACTIVE:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_ALREADY_ACTIVE));
                case REDEEMED:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_REDEEMED));
                default:
                    return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
            }
        });
    }

    private void returnsExtraValidations(TransactionType transactionType, AcquisitionDTO request) throws ComponentThrowable {
        String startLogFormat = EMPTY_STRING;
        String endLogFormat = EMPTY_STRING;
        switch (transactionType) {
            case ACTIVATION_REVERSAL:
                startLogFormat = EXECUTING_ACTIVATION_REVERSAL_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID;
                endLogFormat = CODE_ACTIVATION_IS_NOT_REVERSIBLE_FOR_TIME_OUT_FOR_CODE_WITH_ID;
                break;
            case DEACTIVATION:
                startLogFormat = EXECUTING_DEACTIVATION_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID;
                endLogFormat = CODE_IS_NOT_DEACTIVATING_FOR_TIME_OUT_FOR_CODE_WITH_ID;
                break;
            case DEACTIVATION_REVERSAL:
                startLogFormat = EXECUTING_DEACTIVATION_REVERSAL_EXTRA_VALIDATIONS_FOR_CODE_WITH_ID;
                endLogFormat = CODE_DEACTIVATION_IS_NOT_REVERSIBLE_FOR_TIME_OUT_FOR_CODE_WITH_ID;
                break;
            default:
                break;
        }
        log.info(startLogFormat, request.getProductCode().getId());
        if (request.getProductCode().getUpdateDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(request.getProductCode().getUpdateDate());

            if (calendar.getTime().getTime() > new Date().getTime()) {
                log.info(endLogFormat, request.getProductCode().getId());
                throw getComponentThrowableProcessing(NOT_REVERSIBLE);
            }
        }

        validateLastTransaction(transactionType, request);
    }

    private void validateLastTransaction(TransactionType transactionType, AcquisitionDTO request) throws ComponentThrowable {
        log.info(VALIDATING_LAST_TRANSACTION_FOR_PRODUCT, request.getProductCode().getId());
        List<TransactionType> notValidTransactionTypes = new ArrayList<>();
        notValidTransactionTypes.add(TransactionType.ACTIVATION_PRE_AUTHORIZATION);
        notValidTransactionTypes.add(TransactionType.PRODUCT_VALIDATION);

        Optional<Transaction> optionalLastTransaction = this.repositoryContainer.getTransactionRepository()
                .findFirstByProductCodeAndStateAndTransactionTypeNotInOrderByDateTransactionDesc
                        (request.getProductCode(), TransactionState.SUCCESS, notValidTransactionTypes);
        Transaction lastTransaction = null;
        if (optionalLastTransaction.isPresent()) {
            lastTransaction = optionalLastTransaction.get();
        }

        if (lastTransaction == null) {
            if (!request.getProductCode().getStatus().equals(ProductState.INACTIVE)) {
                log.info(LAST_TRANSACTION_NOT_FOUND_FOR_PRODUCT, request.getProductCode().getId());
                throw getComponentThrowableProcessing(ORIGINAL_TRANSACTION_NOT_FOUND);
            } else {
                return;
            }
        }

        validateSignature(lastTransaction);

        boolean isInvalidTransaction = false;
        switch (transactionType) {
            case ACTIVATION_REVERSAL:
                isInvalidTransaction = !lastTransaction.getTransactionType().equals(TransactionType.ACTIVATION_AUTHORIZATION);
                break;
            case DEACTIVATION:
            case REDEEM:
                isInvalidTransaction = !lastTransaction.getTransactionType().equals(TransactionType.ACTIVATION_AUTHORIZATION)
                        && !lastTransaction.getTransactionType().equals(TransactionType.DEACTIVATION_REVERSAL);
                break;
            case DEACTIVATION_REVERSAL:
                isInvalidTransaction = !lastTransaction.getTransactionType().equals(TransactionType.DEACTIVATION);
                break;
            default:
                break;
        }

        if (isInvalidTransaction) {
            log.info(THE_LAST_TRANSACTION_NOT_IS_VALID_FOR_PROCESS, request.getProductCode().getId());
            throw getComponentThrowableProcessing(ORIGINAL_TRANSACTION_NOT_FOUND);
        }
    }

    private void validateSignature(Transaction transaction) throws ComponentThrowable {
        boolean isInvalidSignature = false;
        try {
            if (Boolean.TRUE.equals(transaction.getIsAltered())) {
                isInvalidSignature = true;
            }
            this.containerSecurityHelper.getSignatureHelper().validate(transaction);
        } catch (DataException e) {
            log.error(SIGNATURE_ALTERED_FOR_TRANSACTION, transaction.getId());
            transaction.setIsAltered(true);
            this.repositoryContainer.getTransactionRepository().save(transaction);
            isInvalidSignature = true;
        }

        if (isInvalidSignature) {
            throw getComponentThrowableProcessing(ORIGINAL_TRANSACTION_NOT_FOUND);
        }
    }

    // INCOMM RESPONSE *************************************************************************************************

    private Mono<AcquisitionDTO> startResponse(@NotNull String correlative, @NotNull AcquisitionDTO
            request, @NotNull AcquisitionDTO response) {
        log.info(CREATING_BASE_RESPONSE);
        request.setTransactionDate(new Date());
        Date responseDate = request.getTransactionDate();
        if (request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue().equals(TimeZone.EDT.name())) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, ONE_INT);
            responseDate = calendar.getTime();
        }
        response.setServiceProviderTxn(ServiceProviderTxn.builder()
                .version(request.getServiceProviderTxn().getVersion())
                .response(AcquisitionData.builder()
                        .msgType(request.getServiceProviderTxn().getRequest().getMsgType())
                        .dateTimeInfo(DateTimeInfo.builder()
                                .dateValue(this.dateFormat.format(responseDate))
                                .timeValue(this.timeFormat.format(responseDate))
                                .timeZoneValue(request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue())
                                .build())
                        .serviceProviderRefNum(correlative)
                        .product(Product.builder()
                                .fee(ZERO_DOUBLE)
                                .status(ProductState.UNKNOWN.getValue())
                                .balance(Value.builder()
                                        .money(Money.builder()
                                                .amount(ZERO_DOUBLE)
                                                .currencyCode(CurrencyCode.COP.name())
                                                .build())
                                        .build())
                                .build())
                        .originalRequest(request.getServiceProviderTxn().getRequest())
                        .extension(request.getServiceProviderTxn().getRequest().getExtension())
                        .build())
                .build());
        log.info(BASE_RESPONSE_CREATED);
        return Mono.just(response);
    }

    private Mono<IComponentDTO> endResponse(@NotNull AcquisitionDTO request, @NotNull AcquisitionDTO
            response, Throwable error) {
        log.info(MAPPING_END_OF_RESPONSE);
        String statusCode;
        if (error != null) {
            if (error instanceof ComponentThrowable) {
                // Controlled error
                statusCode = ((ComponentThrowable) error).getCode();
            } else {
                // Not controlled error
                statusCode = SYSTEM_ERROR;
                log.error(AN_ERROR_OCCURRED, error.getMessage());
            }
        } else if (response.getStatus() != null) {
            // Controlled error
            statusCode = response.getStatus().getCode();
        } else {
            // success response
            statusCode = SUCCESS_00;
        }

        if (request.getTransaction() != null) {
            changeTransactionState(request.getTransaction(), statusCode);
        }

        response.setStatus(null);
        response.getServiceProviderTxn().getResponse().setRespCode(statusCode);
        response.getServiceProviderTxn().getResponse().setRespMsg(getStatusCodeConfig().of(statusCode).getMessage());

        log.info(THE_TRANSACTION_HAS_RESP_CODE_AND_MESSAGE, response.getServiceProviderTxn().getResponse().getRespCode(),
                response.getServiceProviderTxn().getResponse().getRespMsg());

        if (request.getProductCode() != null) {
            response.getServiceProviderTxn().getResponse().getProduct().setStatus(request.getProductCode().getStatus().getValue());
            response.getServiceProviderTxn().getResponse().getProduct().getBalance().getMoney().setCurrencyCode(request.getProductCode().getProduct().getCurrencyCode().name());
            response.getServiceProviderTxn().getResponse().getProduct().getBalance().getMoney().setAmount(request.getProductCode().getProduct().getValue());
        }
        return Mono.just(response);
    }

    // MOVII METHODS ***************************************************************************************************

    private Mono<IComponentDTO> productValidateOrRedeem(@NotNull String correlative, @NotNull String
            authorizationHeader, @NotNull String productIdentifier, @NotNull AcquisitionDTO req, boolean isRedeem, String
                                                                methodName) {
        // Validate user
        return Mono.just(req).flatMap(request -> authentication(correlative, methodName, authorizationHeader, new AcquisitionDTO(), false))
                .flatMap(response -> {
                    if (response.getStatus() != null) {
                        return Mono.just(response);
                    }

                    // Execute method
                    return validateOrRedeemCode(correlative, productIdentifier, Mono.just(req), response, isRedeem)
                            .flatMap(resp -> consumingCode(Mono.just(req), resp))
                            .flatMap(resp -> endRedeemResponse(correlative, req, response, null, isRedeem))
                            .onErrorResume(error -> endRedeemResponse(correlative, req, response, error, isRedeem));
                });
    }

    private Mono<AcquisitionDTO> consumingCode(@NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> Mono.just(response));
    }

    private Mono<AcquisitionDTO> validateOrRedeemCode(@NotNull String correlative, @NotNull String
            productIdentifier, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response, boolean isRedeem) {
        return validateRedeemRequest(req, response)
                .flatMap(request -> validateProductPin(correlative, req, productIdentifier, isRedeem))
                .flatMap(request -> {
                    assignCorrelative(correlative);
                    switch (request.getProductCode().getStatus()) {
                        case ACTIVE:
                            try {
                                validateLastTransaction(isRedeem ? TransactionType.REDEEM : TransactionType.PRODUCT_VALIDATION, request);
                            } catch (ComponentThrowable e) {
                                return Mono.error(e);
                            }
                            if (isRedeem) {
                                changeProductCodeState(request, ProductState.REDEEMED);
                            }
                            response.setProductCode(request.getProductCode());
                            return Mono.just(response);
                        case REDEEMED:
                            return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_REDEEMED));
                        case INACTIVE:
                            changeProductCodeState(request, ProductState.CANCELLED);
                            return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
                        default:
                            return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
                    }
                });
    }

    private Mono<IComponentDTO> reverseRedeem(@NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            if (request.getProductCode() != null && request.getProductCode().getStatus() != null && request.getProductCode().getStatus() == ProductState.REDEEMED) {
                changeProductCodeState(request, ProductState.ACTIVE);
                response.setProductCode(request.getProductCode());
                return Mono.just(response);
            }
            return Mono.error(getComponentThrowableProcessing(CARD_OR_CODE_IS_IN_INVALID_STATE));
        });
    }

    private Mono<AcquisitionDTO> validateRedeemRequest(@NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            log.info(VALIDATING_REQUEST);
            StatusCode statusCode = getStatusCodeConfig().of(BAD_REQUEST_CODE);
            String errorMessage = validateRequestRedeem(request, statusCode);
            if (errorMessage != null) {
                return Mono.error(new ComponentThrowable(ErrorType.DATA, errorMessage, BAD_REQUEST_CODE, getGlobalProperties().getName()));
            }
            return Mono.just(response);
        });
    }

    private String validateRequestRedeem(AcquisitionDTO request, StatusCode statusCode) {
        if (request.getPin() == null || request.getPin().trim().isEmpty()) {
            return statusCode.getMessage() + FIELD_PIN_IS_REQUIRED;
        }
        return null;
    }

    private Mono<AcquisitionDTO> validateProductPin(@NotNull String
                                                            correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull String productIdentifier, @NotNull boolean isRedeem) {
        return req
                .flatMap(request -> getProductByIdentifier(request, productIdentifier))
                .flatMap(request -> startTransactionValidations(correlative, request,
                        isRedeem ? TransactionType.REDEEM.getValue() : TransactionType.PRODUCT_VALIDATION.getValue(), true))
                .flatMap(request -> {
                    try {
                        validateProductTransactions(request);
                    } catch (ComponentThrowable e) {
                        return Mono.error(e);
                    }
                    return Mono.just(request);
                });
    }

    private Mono<IComponentDTO> createCodes(@NotNull String correlative, @NotNull String
            productIdentifier, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req.flatMap(request -> {
            assignCorrelative(correlative);
            if (request.getNumberOfCodesToCreate() == null || request.getNumberOfCodesToCreate() < ONE_INT) {
                log.info(NUMBER_OF_CODES_TO_CREATE_IS_INVALID);
                response.setStatus(getErrorResponseByCode(BAD_REQUEST_CODE));
                return Mono.just(response);
            }

            log.info(FINDING_PRODUCT_FOR_IDENTIFIER, productIdentifier);
            Optional<co.moviired.acquisition.model.entity.Product> optionalProduct =
                    this.repositoryContainer.getProductRepository().findByIdentifier(productIdentifier);

            if (optionalProduct.isEmpty()) {
                log.info(PRODUCT_NOT_FOUND_FOR_IDENTIFIER, productIdentifier);
                response.setStatus(getErrorResponseByCode(NOT_FOUND_CODE));
                return Mono.just(response);
            }

            co.moviired.acquisition.model.entity.Product product = optionalProduct.get();

            log.info(STARTING_CREATION_OF_CODES_FOR_PRODUCT_WITH_IDENTIFIER, productIdentifier);
            response.setLotIdentifier(correlative);
            long codesCreated = createCodes(correlative, product, request.getNumberOfCodesToCreate(), new Date());
            response.setCodesCreated(codesCreated);
            return Mono.just(response);
        });
    }

    private long createCodes(String correlative, co.moviired.acquisition.model.entity.Product product, long numberOfCodesToCreate, Date date) {
        long codesCreated = ZERO_INT;
        for (long i = ZERO_INT; i < numberOfCodesToCreate; i++) {
            try {
                createCode(correlative, product, date);
                codesCreated++;
                log.info(CREATING_CODES, codesCreated, numberOfCodesToCreate);
            } catch (Exception e) {
                log.error(ERROR_CREATING_ONE_CODE, e.getMessage());
            }
        }
        return codesCreated;
    }

    private void createCode(String correlative, co.moviired.acquisition.model.entity.Product product, Date date) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        log.debug(CREATING_CARD_CODE_PAN);
        String cardCode = getValidCode(componentProperties.getCardCode().getLength(), componentProperties.getCardCode().getIsAlphaNumeric(), product);
        log.debug(CREATING_PIN);
        String pin = getValidCode(componentProperties.getPin().getLength(), componentProperties.getPin().getIsAlphaNumeric(), product);

        log.debug(SAVING_CARD_CODES);
        ProductCode productCode = ProductCode.builder()
                .cardCode(cardCode)
                .pinHash(UtilsHelper.cipherSha256(pin))
                .pin(AESGCMHelper.encrypt(pin, cryptoKey + this.componentProperties.getSecurityCryptoPinSecret()))
                .lotIdentifier(correlative)
                .status(ProductState.INACTIVE)
                .creationDate(date)
                .isAltered(false)
                .product(product)
                .build();

        productCode.setSignature(this.containerSecurityHelper.getSignatureHelper()
                .sign(this.containerSecurityHelper.getSignatureHelper().assignSignatureVersion(productCode)));
        this.repositoryContainer.getProductCodeRepository().save(productCode);
        log.debug(SAVE_SUCCESSFUL);
    }

    @SuppressWarnings(SAME_PARAMETER_VALUE)
    private String getValidCode(int codeLength, boolean isAlphaNumeric, co.moviired.acquisition.model.entity.Product product) {
        while (true) {
            try {
                Optional<ProductCode> optionalProductCode;
                String code;
                if (isAlphaNumeric) {
                    code = UtilsHelper.randomAlphaNumeric(codeLength);
                    optionalProductCode = this.repositoryContainer.getProductCodeRepository().findFirstByPinHashAndProduct(UtilsHelper.cipherSha256(code), product);
                } else {
                    code = Generator.pin(codeLength, false);
                    optionalProductCode = this.repositoryContainer.getProductCodeRepository().findFirstByCardCodeAndProduct(code, product);
                }

                if (optionalProductCode.isEmpty()) {
                    return code;
                }
                log.debug(CODE_ALREADY_EXIST_RETRYING);
            } catch (Exception e) {
                log.error(ERROR_GENERATING_CODE_RETRYING, e.getMessage());
            }
        }
    }

    private Mono<IComponentDTO> endRedeemResponse(@NotNull String correlative, @NotNull AcquisitionDTO
            request, @NotNull AcquisitionDTO response, Throwable error, @NotNull boolean isRedeem) {
        String statusCode;
        String statusMessage;
        if (error != null) {
            if (error instanceof ComponentThrowable) {
                // Controlled error
                statusCode = ((ComponentThrowable) error).getCode();
                statusMessage = error.getMessage();
            } else {
                log.error(AN_UNEXPECTED_ERROR_OCCURRED, error.getMessage());
                // Not controlled error
                statusCode = SERVER_ERROR_CODE;
                statusMessage = getStatusCodeConfig().of(SERVER_ERROR_CODE).getMessage();
            }
        } else if (response.getStatus() != null) {
            // Controlled error
            statusCode = response.getStatus().getCode();
            statusMessage = response.getStatus().getMessage();
        } else {
            // success response
            statusCode = SUCCESS_CODE;
            statusMessage = getStatusCodeConfig().of(SUCCESS_CODE).getMessage();
        }

        if (request.getTransaction() != null) {
            changeTransactionState(request.getTransaction(), statusCode);
        }

        response.setCorrelative(correlative);
        response.setTransaction(request.getTransaction());
        response.setStatus(ResponseStatus.builder()
                .code(statusCode)
                .message(statusMessage)
                .component(getGlobalProperties().getName())
                .build());

        if (isRedeem && response.getStatus().getCode().equals(StatusCodesHelper.ERROR)) {
            return reverseRedeem(Mono.just(request), response);
        }

        return Mono.just(response);
    }

    private Mono<IComponentDTO> getLotsIdentifiers(@NotNull String correlative, @NotNull AcquisitionDTO response) {
        return Mono.just(correlative)
                .flatMap(c -> {
                    assignCorrelative(correlative);
                    log.info(FINDING_LOTS);
                    response.setLotIdentifiersList(this.repositoryContainer.getProductCodeRepository().getLotsIdentifiers());
                    response.setStatus(getSuccessResponse());
                    return Mono.just((IComponentDTO) response);
                }).onErrorResume(error -> {
                    response.setStatus(getErrorResponse());
                    return Mono.just((IComponentDTO) response);
                });
    }

    private Mono<IComponentDTO> getProductCodes(@NotNull String correlative, @NotNull Mono<AcquisitionDTO> req, @NotNull AcquisitionDTO response) {
        return req
                .flatMap(request -> {
                    assignCorrelative(correlative);

                    if (request.getFileName() == null || request.getFileName().trim().isEmpty()
                            || request.getLotIdentifiers() == null || request.getLotIdentifiers().isEmpty()) {
                        response.setStatus(getErrorResponseByCode(BAD_REQUEST_CODE));
                        return Mono.just(response);
                    }

                    List<ProductCode> productCodes = this.repositoryContainer.getProductCodeRepository().findByLotIdentifierIn(request.getLotIdentifiers());

                    StringBuilder document = new StringBuilder()
                            .append(UtilsHelper.toStringCSV(request.getUseQuotes(), request.getDelimiter(), UPC, PAN, PIN));

                    for (ProductCode productCode : productCodes) {
                        try {
                            document.append(UtilsHelper.toStringCSV(request.getUseQuotes(),
                                    request.getDelimiter(),
                                    productCode.getProduct().getProductCode(),
                                    productCode.getCardCode(),
                                    AESGCMHelper.decrypt(productCode.getPin(), cryptoKey + this.componentProperties.getSecurityCryptoPinSecret())));
                        } catch (Exception e) {
                            log.error(ERROR_MAPPING_CODES, e.getMessage());
                        }
                    }

                    response.setProductCodesCSV(document.toString().getBytes());
                    response.setStatus(getSuccessResponse());

                    return Mono.just((IComponentDTO) response);
                }).onErrorResume(error -> {
                    response.setStatus(getErrorResponse());
                    return Mono.just((IComponentDTO) response);
                });
    }

    // DATABASE ********************************************************************************************************

    // Product

    private Mono<AcquisitionDTO> getProductByProductCode(@NotNull AcquisitionDTO request) {
        log.info(FINDING_PRODUCT_BY_PRODUCT_CODE, request.getServiceProviderTxn().getRequest().getProduct().getUpc());
        Optional<co.moviired.acquisition.model.entity.Product> optionalProduct = this.repositoryContainer.getProductRepository()
                .findByProductCode(request.getServiceProviderTxn().getRequest().getProduct().getUpc());

        if (optionalProduct.isPresent()) {
            request.setProduct(optionalProduct.get());
            log.info(PRODUCT_FOUND_AND_HAS_ID, request.getProduct().getId());
            return Mono.just(request);
        } else {
            log.info(PRODUCT_NOT_FOUND_WITH_CODE, request.getServiceProviderTxn().getRequest().getProduct().getUpc());
            return Mono.error(getComponentThrowableProcessing(INVALID_REQUEST));
        }
    }

    private Mono<AcquisitionDTO> getProductByIdentifier(@NotNull AcquisitionDTO request, String productIdentifier) {
        log.info(FINDING_PRODUCT_BY_IDENTIFIER, productIdentifier);
        Optional<co.moviired.acquisition.model.entity.Product> optionalProduct = this.repositoryContainer.getProductRepository().findByIdentifier(productIdentifier);

        if (optionalProduct.isPresent()) {
            request.setProduct(optionalProduct.get());
            log.info(PRODUCT_FOUND_AND_HAS_ID, request.getProduct().getId());
            return Mono.just(request);
        } else {
            log.info(PRODUCT_NOT_FOUND_WITH_IDENTIFIER, productIdentifier);
            return Mono.error(getComponentThrowableProcessing(NOT_FOUND_CODE));
        }
    }

    // ProductCode

    private ProductCode getProductCode(@NotNull AcquisitionDTO request, @NotNull boolean findByPin,
                                       @NotNull co.moviired.acquisition.model.entity.Product product) {
        log.info(FINDING_PRODUCT_CODE_IN_PRODUCT_WITH_NAME, product.getName());

        Optional<ProductCode> optionalProductCode;

        if (findByPin) {
            log.info(FINDING_FOR_PIN_AND_PRODUCT_ID, product.getId());
            try {
                log.info(DECODING_PIN_OF_TRANSACTION);
                request.setPin(this.containerSecurityHelper.getCryptoHelper().decoder(request.getPin()));
            } catch (ParsingException e) {
                log.error(AN_ERROR_OCCURRED_DECODING_PIN, e.getMessage());
                return null;
            }
            optionalProductCode = this.repositoryContainer.getProductCodeRepository().findFirstByPinHashAndProduct(UtilsHelper.cipherSha256(request.getPin()), product);
        } else {
            log.info(FINDING_FOR_CARD_CODE_AND_PRODUCT_ID, product.getId());
            optionalProductCode = this.repositoryContainer.getProductCodeRepository().findFirstByCardCodeAndProduct(request.getServiceProviderTxn().getRequest().getProduct().getSpNumber(), product);
        }

        if (optionalProductCode.isEmpty()) {
            log.info(PRODUCT_CODE_FOR_PRODUCT_WITH_NAME_NOT_FOUND, product.getName());
            return null;
        }

        ProductCode productCode = optionalProductCode.get();

        if (!productCode.getProduct().isStatus() || !productCode.getProduct().getCategory().isStatus()) {
            log.info(PRODUCT_WITH_ID_OR_CATEGORY_WITH_ID_NOT_HAS_ACTIVE_STATUS, productCode.getProduct().getId(), productCode.getProduct().getCategory().getId());
            return null;
        }

        return productCode;
    }

    private void changeProductCodeState(@NotNull AcquisitionDTO request, @NotNull ProductState state) {
        log.info(CHANGING_PRODUCT_CODE_WITH_ID_TO_STATE, request.getProductCode().getId(), state.name());
        request.getProductCode().setStatus(state);
        request.getProductCode().setUpdateDate(request.getTransaction().getDateTransaction());
        request.getProductCode().setSignature(this.containerSecurityHelper.getSignatureHelper()
                .sign(this.containerSecurityHelper.getSignatureHelper().assignSignatureVersion(request.getProductCode())));
        log.info(SAVING_PRODUCT_CODE_WITH_ID, request.getProductCode().getId());
        this.repositoryContainer.getProductCodeRepository().save(request.getProductCode());
    }

    private boolean isInvalidSignature(ProductCode productCode) {
        log.info(VALIDATING_SIGNATURE_FOR_PRODUCT_CODE_WITH_ID, productCode.getId());

        if (Boolean.TRUE.equals(productCode.getIsAltered())) {
            log.info(SIGNATURE_IS_PREVIOUSLY_ALTERED_FOR_PRODUCT_CODE_WITH_ID, productCode.getId());
            return true;
        }

        try {
            this.containerSecurityHelper.getSignatureHelper().validate(productCode);
            return false;
        } catch (DataException e) {
            log.error(SIGNATURE_ALTERED_FOR_PRODUCT_CODE_WITH_ID, productCode.getId());
            productCode.setIsAltered(true);
            this.repositoryContainer.getProductCodeRepository().save(productCode);
            return true;
        }
    }

    // Transaction

    private void validateProductTransactions(@NotNull AcquisitionDTO request) throws ComponentThrowable {
        log.info(VALIDATING_TRANSACTIONS_FOR_PRODUCT_CODE_WITH_ID, request.getProductCode().getId());

        List<Transaction> pendingTransactions = this.repositoryContainer.getTransactionRepository()
                .findByProductCodeAndStateAndIdNot(request.getProductCode(), TransactionState.PENDING, request.getTransaction().getId());

        if (!pendingTransactions.isEmpty()) {
            log.info(THE_PRODUCT_CODE_WITH_ID_HAS_A_TRANSACTION_PENDING_THIS_TRANSACTION_IS_REJECTED, request.getProductCode().getId());
            throw getComponentThrowableProcessing(SYSTEM_ERROR);
        }
    }

    private Transaction createIncommTransaction(@NotNull String correlative, @NotNull AcquisitionDTO
            request, @NotNull ProductCode productCode) {
        log.info(CREATING_TRANSACTION_WITH_TYPE, request.getTransactionType());
        Transaction transaction = Transaction.builder()
                .id(correlative)
                .transactionType(request.getTransactionType())
                .incommRefNum(request.getServiceProviderTxn().getRequest().getIncommRefNum())
                .merchName(request.getServiceProviderTxn().getRequest().getOrigin().getMerchName())
                .merchRefNum(request.getServiceProviderTxn().getRequest().getOrigin().getMerchRefNum())
                .dateTransaction(request.getTransactionDate())
                .state(TransactionState.PENDING)
                .respCode(DEFAULT_CODE)
                .incommTimeZone(request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeZoneValue())
                .storeId(request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getStoreID())
                .terminalId(request.getServiceProviderTxn().getRequest().getOrigin().getStoreInfo().getTermID())
                .cardNumber(request.getServiceProviderTxn().getRequest().getProduct().getSpNumber())
                .value(request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getAmount())
                .currencyCode(request.getServiceProviderTxn().getRequest().getProduct().getValue().getMoney().getCurrencyCode())
                .productCode(productCode)
                .isAltered(false)
                .build();
        try {
            transaction.setIncommDateTime(
                    this.dateTimeFormat.parse(request.getServiceProviderTxn().getRequest().getDateTimeInfo().getDateValue()
                            .concat(request.getServiceProviderTxn().getRequest().getDateTimeInfo().getTimeValue())));
        } catch (ParseException e) {
            log.error(ERROR_PARSING_INCOMM_DATE_TIME, e.getMessage());
        }
        transaction.setSignature(this.containerSecurityHelper.getSignatureHelper()
                .sign(this.containerSecurityHelper.getSignatureHelper().assignSignatureVersion(transaction)));
        log.info(SAVING_TRANSACTION_FOR_PRODUCT_CODE_WITH_ID, transaction.getId());
        return this.repositoryContainer.getTransactionRepository().save(transaction);
    }

    private Transaction createMoviiTransaction(@NotNull String correlative, @NotNull ProductCode
            productCode, @NotNull TransactionType transactionType) {
        log.info(CREATING_TRANSACTION_WITH_TYPE, transactionType.name());
        Transaction transaction = Transaction.builder()
                .id(correlative)
                .transactionType(transactionType)
                .dateTransaction(new Date())
                .state(TransactionState.PENDING)
                .productCode(productCode)
                .respCode(DEFAULT_CODE)
                .isAltered(false)
                .build();
        transaction.setSignature(this.containerSecurityHelper.getSignatureHelper()
                .sign(this.containerSecurityHelper.getSignatureHelper().assignSignatureVersion(transaction)));
        log.info(SAVING_TRANSACTION_FOR_PRODUCT_CODE_WITH_ID, productCode.getId());
        return this.repositoryContainer.getTransactionRepository().save(transaction);
    }

    private void changeTransactionState(@NotNull Transaction transaction, @NotNull String respCode) {
        TransactionState transactionState = (SUCCESS_0.equals(respCode) || SUCCESS_00.equals(respCode) || SUCCESS_CODE.equals(respCode)) ?
                TransactionState.SUCCESS : TransactionState.ERROR;
        log.info(CHANGING_TRANSACTION_STATE_TO, transactionState.name());
        transaction.setRespCode(respCode);
        transaction.setState(transactionState);
        transaction.setRespCode(respCode);
        transaction.setSignature(this.containerSecurityHelper.getSignatureHelper()
                .sign(this.containerSecurityHelper.getSignatureHelper().assignSignatureVersion(transaction)));
        log.info(SAVING_TRANSACTION_WITH_ID, transaction.getId());
        this.repositoryContainer.getTransactionRepository().save(transaction);
    }

    // AUTHENTICATION **************************************************************************************************

    private Mono<AcquisitionDTO> authentication(@NotNull String correlative, @NotNull String
            methodName, @NotNull String authorizationHeader, @NotNull AcquisitionDTO response, @NotNull boolean useLocalLogin) {
        return Mono.just(correlative)
                .flatMap(c -> {
                    log.info(DOING_LOGIN);
                    if (useLocalLogin) {
                        return doLocalLogin(methodName, authorizationHeader, response);
                    } else {
                        return doMahindraLogin(correlative, authorizationHeader, response);
                    }
                }).onErrorResume(error -> {
                    if (error instanceof DataException) {
                        log.info(USER_NOT_FOUND_OR_NOT_ALLOWED_FOR_METHOD);
                        response.setStatus(getErrorResponseByCode(INVALID_USER));
                    } else {
                        log.error(ERROR_DOING_LOGIN, error.getMessage());
                        response.setStatus(getErrorResponseByCode(SYSTEM_ERROR));
                    }
                    return Mono.just(response);
                });
    }

    private Mono<AcquisitionDTO> doLocalLogin(String methodName, @NotNull String
            authorizationHeader, @NotNull AcquisitionDTO response) {

        log.info(METHOD_USE_LOCAL_LOGIN);

        log.info(VALIDATING_AUTHORIZATION_PARTS);
        String[] authorizationParts = authorizationHeader.split(TWO_DOTS);
        if (authorizationParts.length != TWO_INT) {
            log.error(ERROR_AUTHORIZATION_NOT_IS_VALID);
            return Mono.error(DataException::new);
        }

        String hashUserAndPassword = UtilsHelper.cipherSha256(authorizationParts[ZERO_INT])
                .concat(TWO_DOTS).concat(UtilsHelper.cipherSha256(authorizationParts[ONE_INT]));

        // Find user
        for (ComponentUser componentUser : this.componentProperties.getBasicAuthenticationUsers()) {
            if (hashUserAndPassword.equals(componentUser.getUser().concat(TWO_DOTS).concat(componentUser.getEncryptedPass()))) {
                // Validate access to method
                if (componentUser.getMethodsAllowed().contains(methodName)) {
                    log.info(LOGIN_SUCCESSFUL_WITH_USER, componentUser.getName());
                    return Mono.just(response);
                } else {
                    log.info(USER_IS_NOT_ALLOWED_FOR_METHOD, methodName);
                    break;
                }
            }
        }
        return Mono.error(DataException::new);
    }

    private Mono<AcquisitionDTO> doMahindraLogin(@NotNull String correlative, @NotNull String
            authorizationHeader, @NotNull AcquisitionDTO response) {
        return Mono.just(authorizationHeader)
                // Validate authorization header
                .flatMap(authorization -> {
                    log.info(METHOD_USE_MAHINDRA_LOGIN);
                    try {
                        log.info(VALIDATING_AUTHORIZATION_PARTS);
                        return Mono.just(getAuthorizationParts(authorizationHeader));
                    } catch (ComponentThrowable error) {
                        log.error(ERROR_VALIDATING_AUTHORIZATION_PARTS, error.getMessage());
                        return Mono.error(error);
                    }
                })
                // Do mahindra login
                .flatMap(authorizationParts -> this.mahindraConnector.invokeLogin(correlative, authorizationParts[ZERO_INT], authorizationParts[ONE_INT]))
                // Validate login response
                .flatMap(loginResponse -> onMahindraResponse(loginResponse, correlative)
                        // validate user type
                        .flatMap(responseLogin -> {
                            log.info(VALIDATING_IF_USER_TYPE_IS_ALLOWED, responseLogin.getUserType());
                            if (this.componentProperties.getAllowedUserTypes().contains(responseLogin.getUserType())) {
                                log.info(USER_TYPE_IS_ALLOWED_FOR_THIS_METHOD, responseLogin.getUserType());
                                return Mono.just(responseLogin);
                            } else {
                                log.info(USER_TYPE_IS_NOT_ALLOWED, responseLogin.getUserType());
                                return Mono.error(new ComponentThrowable(ErrorType.PROCESSING, responseLogin.getMessage(), responseLogin.getTxnStatus(), getGlobalProperties().getName()));
                            }
                        })
                        .onErrorResume(error -> error instanceof ComponentThrowable ? Mono.error(DataException::new) : Mono.error(error)))
                .flatMap(loginResponse -> Mono.just(response));
    }
}
