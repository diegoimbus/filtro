package co.moviired.topups.service;


import co.moviired.topups.conf.OperatorStatusSearchProperties;
import co.moviired.topups.model.domain.Operator;
import co.moviired.topups.model.domain.dto.recharge.IOperatorResponse;
import co.moviired.topups.model.domain.dto.recharge.response.OperatorIntegrationResponse;
import co.moviired.topups.model.domain.dto.recharge.response.OperatorResponse;
import co.moviired.topups.model.domain.dto.recharge.response.ProductResponse;
import co.moviired.topups.model.domain.dto.recharge.response.SubTypeProductResponse;
import co.moviired.topups.model.domain.repository.IOperatorRepository;
import co.moviired.topups.model.enums.OperatorStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author carlossaul.ramirez
 * @version 1.0.1
 */
@Service
@Slf4j
public class OperatorService implements Serializable {
    private static final long serialVersionUID = 5411214124952767270L;

    private final IOperatorRepository operatorListRepository;
    private final OperatorStatusSearchProperties operatorStatusSearchProp;
    private final TopupsService rechargeService;

    public OperatorService(@NotNull IOperatorRepository operatorListRepository,
                           @NotNull OperatorStatusSearchProperties operatorStatusSearchProp,
                           @NotNull TopupsService rechargeService) {
        super();
        this.operatorStatusSearchProp = operatorStatusSearchProp;
        this.operatorListRepository = operatorListRepository;
        this.rechargeService = rechargeService;
    }

    public Mono<IOperatorResponse> getOperators(Mono<String> operatorId, String logIdent) {

        return operatorId
                .flatMap(optId -> obtainOperatorsByOperatorId(getOperatorIdAsInt(optId, logIdent), logIdent))
                .switchIfEmpty(
                        Mono.just(OperatorIntegrationResponse.builder().build())
                                .flatMap(emptyResponse -> {
                                    log.info("{} {\"message\":\"OperatorId is empty, it will reurn all products\"}", logIdent);
                                    return obtainOperatorsByOperatorId(null, logIdent);
                                })

                );
    }


    public Mono<IOperatorResponse> getOperatorsByType(Mono<String> type, String logIdent) {

        return type
                .flatMap(typeId -> obtainOperatorsByType(getOperatorIdAsInt(typeId, logIdent), logIdent));
    }

    // Obtain all operators or all operators filtering by Type, then get operatorsDistinct by operatorId, then group list operators with operatorsDistinct and return Products by operatorDistinct
    public Mono<IOperatorResponse> obtainOperatorsByType(Integer typeId, String logIdent) {
        List<Operator> operatorsFromDatabase;
        int pageSize = 0;
        // obtain operators from database
        operatorsFromDatabase = obtainAllOperatorsByType(typeId, logIdent, pageSize, operatorStatusSearchProp.getBlockSize(), operatorStatusSearchProp.getOperatorStatus());

        return Mono.just(new ArrayList<>()).flatMap(listServiceResponse -> {
            // distinct products by operator_id
            io.vavr.collection.List<Operator> operatorsByDistinc = getOpratorsByDistinct(logIdent, null,
                    operatorsFromDatabase);
            // distinct products by Subtype
            io.vavr.collection.List<Operator> operatorsByDistincSubType = getOpratorsByDistinctSubType(logIdent, null,
                    operatorsFromDatabase);

            OperatorIntegrationResponse result = OperatorIntegrationResponse.builder().errorType("0").errorCode("00")
                    .errorMessage("OK").operators(new ArrayList<>()).build();
            // set collection of products for each operator
            operatorsByDistinc.asJava().forEach(operatorDistinct -> {
                OperatorResponse operatorResponse = OperatorResponse.builder()
                        .id(String.valueOf(operatorDistinct.getOperatorId())).name(operatorDistinct.getOperatorName()).product_image(operatorDistinct.getProductImage())
                        .products(new ArrayList<>()).build();

                operatorsFromDatabase.forEach(operatorFromList -> {
                    if (operatorDistinct.getOperatorId() == operatorFromList.getOperatorId()) {

                        ProductResponse product = ProductResponse.builder().eanCode(operatorFromList.getEanCode())
                                .id(String.valueOf(operatorFromList.getId())).name(operatorFromList.getName())
                                .productCode(operatorFromList.getProductCode())
                                .maxValue(String.valueOf(operatorFromList.getMaxValue()))
                                .minValue(String.valueOf(operatorFromList.getMinValue()))
                                .multiple(null != operatorFromList.getMultiple()
                                        ? operatorFromList.getMultiple().toString()
                                        : "")
                                .regExp(operatorFromList.getRegExp())
                                .status(getStatusNameFromId(operatorFromList.getStatus()))
                                .subtype(operatorFromList.getSubType())
                                .detailsExpiration(operatorFromList.getDetailsExpiration())
                                .subTypeProducts(new ArrayList<>())
                                .build();
                        operatorResponse.getProducts().add(product);
                    }
                });

                result.getOperators().add(operatorResponse);
            });

            OperatorIntegrationResponse resultFinal = OperatorIntegrationResponse.builder().errorType("0").errorCode("00")
                    .errorMessage("OK").operators(new ArrayList<>()).build();

            // set collection of products for each operator
            result.getOperators().forEach(operator -> {
                OperatorResponse operatorResponseFinal = OperatorResponse.builder()
                        .id(String.valueOf(operator.getId())).name(operator.getName()).product_image(operator.getProduct_image())
                        .products(new ArrayList<>()).build();

                operatorsByDistincSubType.asJava().forEach(subType -> {
                    ProductResponse productResponseFinal = ProductResponse.builder()
                            .subTypeId(subType.getSubType().getId()).subTypeLabel(subType.getSubType().getSubtype()).subTypeProducts(new ArrayList<>()).build();

                    List<ProductResponse> listProductResponse = new ArrayList<>();
                    List<SubTypeProductResponse> listSubtypeProduct = new ArrayList<>();

                    for(ProductResponse product : operator.getProducts()){
                        if( subType.getSubType().getId() == product.getSubtype().getId()){
                             SubTypeProductResponse subtype = SubTypeProductResponse.builder().
                                     eanCode(product.getEanCode()).
                                     id(product.getId()).
                                     name(product.getName()).
                                     minValue(product.getMinValue())
                                     .maxValue(product.getMaxValue())
                                     .regExp(product.getRegExp())
                                     .status(product.getStatus())
                                     .productCode(product.getProductCode())
                                     .detailsExpiration(product.getDetailsExpiration()).build();
                             listSubtypeProduct.add(subtype);
                       }
                   }

                    if(!listSubtypeProduct.isEmpty()){
                        productResponseFinal.setSubTypeProducts(listSubtypeProduct);
                        operatorResponseFinal.getProducts().add(productResponseFinal);
                    }


                });
                resultFinal.getOperators().add(operatorResponseFinal);
            });

            log.info(resultFinal.getErrorCode());
            log.info("{} {\"message\":\"finished getOperators\"}", logIdent);
            return Mono.just((IOperatorResponse) resultFinal);

        }).onErrorResume(e -> {
            log.warn("{} {\"error\":\"{}\"}", logIdent, e.getMessage());
            return Mono.just(OperatorIntegrationResponse.builder().errorType("0").errorCode("403")
                    .errorMessage(e.getMessage()).operators(new ArrayList<>()).build());
        });
    }

    // Castear operator id sin cortar ciclo.
    private Integer getOperatorIdAsInt(String optId, String logIdent) {
        try {
            return Integer.valueOf(optId);
        } catch (Exception e) {
            log.warn("{} {\"error\":\"{}\"}", logIdent, e.getMessage(), e);
            return null;
        }
    }

    // Obtain all operators or all operators filtering by operatorId, then get operatorsDistinct by operatorId, then group list operators with operatorsDistinct and return Products by operatorDistinct
    public Mono<IOperatorResponse> obtainOperatorsByOperatorId(Integer operatorId, String logIdent) {
        List<Operator> operatorsFromDatabase;
        int pageSize = 0;
        // obtain operators from database
        operatorsFromDatabase = null == operatorId ? obtainAllOperators(logIdent, pageSize, operatorStatusSearchProp.getBlockSize(), operatorStatusSearchProp.getOperatorStatus())
                : obtainAllOperatorsByOperatorId(operatorId, logIdent, pageSize, operatorStatusSearchProp.getBlockSize(), operatorStatusSearchProp.getOperatorStatus());
        return Mono.just(new ArrayList<>()).flatMap(listServiceResponse -> {
            // distinct products by operator_id
            io.vavr.collection.List<Operator> operatorsByDistinc = getOpratorsByDistinct(logIdent, operatorId,
                    operatorsFromDatabase);
            OperatorIntegrationResponse result = OperatorIntegrationResponse.builder().errorType("0").errorCode("00")
                    .errorMessage("OK").operators(new ArrayList<>()).build();
            // set collection of products for each operator
            operatorsByDistinc.asJava().forEach(operatorDistinct -> {
                OperatorResponse operatorResponse = OperatorResponse.builder()
                        .id(String.valueOf(operatorDistinct.getOperatorId())).name(operatorDistinct.getName())
                        .products(new ArrayList<>()).status(getStatusNameFromId(operatorDistinct.getStatus())).build();
                operatorsFromDatabase.forEach(operatorFromList -> {
                    if (operatorDistinct.getOperatorId() == operatorFromList.getOperatorId()) {
                        ProductResponse product = ProductResponse.builder().eanCode(operatorFromList.getEanCode())
                                .id(String.valueOf(operatorFromList.getId())).name(operatorFromList.getName())
                                .maxValue(String.valueOf(operatorFromList.getMaxValue()))
                                .minValue(String.valueOf(operatorFromList.getMinValue()))
                                .multiple(null != operatorFromList.getMultiple()
                                        ? operatorFromList.getMultiple().toString()
                                        : "")
                                .regExp(operatorFromList.getRegExp())
                                .status(getStatusNameFromId(operatorFromList.getStatus())).build();
                        operatorResponse.getProducts().add(product);
                    }
                });
                result.getOperators().add(operatorResponse);
            });
            log.info("{} {\"message\":\"finished getOperators\"}", logIdent);
            return Mono.just((IOperatorResponse) result);
        }).onErrorResume(e -> {
            log.warn("{} {\"error\":\"{}\"}", logIdent, e.getMessage());
            return Mono.just(OperatorIntegrationResponse.builder().errorType("0").errorCode("403")
                    .errorMessage(e.getMessage()).operators(new ArrayList<>()).build());
        });
    }

    // Find all operators from database
    public List<Operator> obtainAllOperators(String logIdent, int page, int blockSize, List<Integer> status) {
        boolean returnOperators;

        // it will iterate while database returns data
        List<Operator> operators = new ArrayList<>();
        do {
            log.debug("{} iterating {}", logIdent, page);
            List<Operator> temporalOperators = operatorListRepository.findAllByStatusIn(status, PageRequest.of(page++, blockSize, Sort.by("operatorId").ascending()));
            returnOperators = !temporalOperators.isEmpty();
            operators.addAll(temporalOperators);
        } while (returnOperators);
        return operators;
    }

    // Find oprators filtering by OperatorId from database
    public List<Operator> obtainAllOperatorsByOperatorId(Integer operatorId,
                                                         String logIdent,
                                                         int page,
                                                         int blockSize,
                                                         List<Integer> status) {
        boolean returnOperators;

        // it will iterate while database returns data
        List<Operator> operators = new ArrayList<>();
        do {
            log.debug("{} iterating {}", logIdent, page);
            List<Operator> temporalOperators = operatorListRepository.findAllByOperatorIdAndStatusIn(operatorId, status, PageRequest.of(page++, blockSize, Sort.by("operatorId").ascending()));
            returnOperators = !temporalOperators.isEmpty();
            operators.addAll(temporalOperators);
        } while (returnOperators);
        return operators;
    }

    // Find oprators filtering by OperatorId from database
    public List<Operator> obtainAllOperatorsByType(Integer typeId,
                                                         String logIdent,
                                                         int page,
                                                         int blockSize,
                                                         List<Integer> status) {
        boolean returnOperators;

        // it will iterate while database returns data
        List<Operator> operators = new ArrayList<>();
        do {
            log.debug("{} iterating {}", logIdent, page);
            int statusView = 1;
            List<Operator> temporalOperators = operatorListRepository.findAllByTypeAndStatusViewAndStatusIn(typeId,statusView, status,PageRequest.of(page++, blockSize, Sort.by("operatorId").ascending().and(Sort.by("minValue"))));
            returnOperators = !temporalOperators.isEmpty();
            operators.addAll(temporalOperators);
        } while (returnOperators);
        return operators;
    }


    // Obtain a distinct array from Operators by operatorId
    public io.vavr.collection.List<Operator> getOpratorsByDistinct(String logIdent,
                                                                   Integer operatorId,
                                                                   List<Operator> operators) {
        if (null == operators || operators.isEmpty()) {
            return io.vavr.collection.List.empty();
        }

        log.debug("{}, {\"operatorId\":\"{}\",\"operatorsSize\":\"{}\"}", logIdent, operatorId, operators.size());
        return (null != operatorId)
                ? io.vavr.collection.List.of(operators.get(0))
                : io.vavr.collection.List.ofAll(operators).distinctBy(Operator::getOperatorId);
    }

    // Obtain a distinct array from Operators by subType
    public io.vavr.collection.List<Operator> getOpratorsByDistinctSubType(String logIdent,
                                                                   Integer subType,
                                                                   List<Operator> operators) {
        if (null == operators || operators.isEmpty()) {
            return io.vavr.collection.List.empty();
        }

        log.debug("{}, {\"subType\":\"{}\",\"operatorsSize\":\"{}\"}", logIdent, subType, operators.size());
        return (null != subType)
                ? io.vavr.collection.List.of(operators.get(0))
                : io.vavr.collection.List.ofAll(operators).distinctBy(Operator::getSubType);
    }

    private String getStatusNameFromId(Integer status) {
        String name;
        switch (status) {
            case 0:
                name = OperatorStatusType.INACTIVE.getValue();
                break;
            case 1:
                name = OperatorStatusType.ACTIVE.getValue();
                break;
            case 2:
                name = OperatorStatusType.SUSPENDED.getValue();
                break;
            default:
                name = "";
        }
        return name;
    }

}

