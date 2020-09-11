package co.moviired.acquisition.service;

import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.service.IPrimaryController;
import co.moviired.acquisition.model.dto.AcquisitionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.util.ConstantsHelper.*;

@Slf4j
@Controller
@RequestMapping(PROJECT_PATH)
public class AcquisitionController extends IPrimaryController<AcquisitionService> {

    public AcquisitionController(@NotNull AcquisitionService acquisitionService,
                                 @NotNull GlobalProperties globalProperties,
                                 @NotNull StatusCodeConfig statusCodeConfig,
                                 @NotNull @Qualifier(SCHEDULER_HELPER_API) SchedulerSupportConnector schedulerSupportConnector) {
        super(acquisitionService, globalProperties, statusCodeConfig, schedulerSupportConnector);
    }

    @PostMapping(value = PATH_INCOMM_REQUEST)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> incommRequest(@RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                         @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                                         @RequestBody AcquisitionDTO request) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startTransaction(correlativeId, request, getBaseBaseService().incommRequest(correlativeId, authorizationHeader, request)));
    }

    @PostMapping(value = PATH_PRODUCT_CODE_VALIDATION)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> productValidation(@PathVariable String productIdentifier,
                                                                             @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                             @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                                             @RequestBody AcquisitionDTO request) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startTransaction(correlativeId, request, getBaseBaseService().productValidation(correlativeId, authorizationHeader, productIdentifier, request)));
    }

    @PostMapping(value = PATH_PRODUCT_CODE_REDEEM)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> productRedeem(@PathVariable String productIdentifier,
                                                                         @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                         @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                                         @RequestBody AcquisitionDTO request) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startTransaction(correlativeId, request, getBaseBaseService().productRedeem(correlativeId, authorizationHeader, productIdentifier, request)));
    }

    @PostMapping(value = PATH_PRODUCT_CODES_CREATION)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> productCodesCreation(@PathVariable String productIdentifier,
                                                                                @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                                @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                                                @RequestBody AcquisitionDTO request) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startTransaction(correlativeId, request, getBaseBaseService().productCodesCreation(correlativeId, authorizationHeader, productIdentifier, request)));
    }

    @GetMapping(value = PATH_GET_LOTS_IDENTIFIERS)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> getLotsIdentifiers(@RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                              @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startTransaction(correlativeId, getBaseBaseService().getLotsIdentifiers(correlativeId, authorizationHeader)));
    }

    @PostMapping(value = PATH_GET_PRODUCT_CODES)
    public final Mono<ResponseEntity<Object>> getProductCodes(@RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                              @RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                              @RequestBody AcquisitionDTO request) {
        return assignCorrelative(correlative).flatMap(correlativeId ->
                startMonoTransaction(correlativeId, request, getBaseBaseService().getProductCodes(correlativeId, authorizationHeader, request)))
                .flatMap(response -> {
                    try {
                        boolean cantCreateFile = false;

                        AcquisitionDTO acquisitionDTO = new AcquisitionDTO();
                        if (response instanceof AcquisitionDTO) {
                            acquisitionDTO = (AcquisitionDTO) response;
                        } else {
                            cantCreateFile = true;
                        }

                        if (!cantCreateFile && (request.getFileName() == null && acquisitionDTO.getProductCodesCSV() == null)) {
                            cantCreateFile = true;
                        }

                        if (cantCreateFile) {
                            return Mono.just(new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR));
                        }

                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILE_NAME + request.getFileName());
                        return Mono.just(new ResponseEntity<>(acquisitionDTO.getProductCodesCSV(), headers, HttpStatus.OK));
                    } catch (Exception e) {
                        return Mono.just(new ResponseEntity<>(new byte[]{}, HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }
}
