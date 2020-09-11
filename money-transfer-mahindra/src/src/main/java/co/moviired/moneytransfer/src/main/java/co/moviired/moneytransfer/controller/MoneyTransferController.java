package co.moviired.moneytransfer.controller;

import co.moviired.base.domain.StatusCode;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.domain.model.response.MoneyTransferResponse;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.helper.DocumentType;
import co.moviired.moneytransfer.properties.GlobalProperties;
import co.moviired.moneytransfer.service.MoneyTransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Slf4j
@RestController()
@AllArgsConstructor
@RequestMapping("${spring.application.services.rest.uri}")
public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;
    private final GlobalProperties globalProperties;


    @GetMapping(value = "${spring.application.services.rest.methods.ping}")
    public final Mono<MoneyTransferResponse> ping() {
        return moneyTransferService.getPing();
    }

    @PostMapping(value = "${spring.application.services.rest.methods.startGiro}")
    public final Mono<MoneyTransferResponse> start(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                   @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                   @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                   @Valid @RequestBody Mono<MoneyTransferRequest> request) {
        return moneyTransferService.serviceStart(request, authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.placeGiro}")
    public final Mono<MoneyTransferResponse> place(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                   @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                   @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                   @Valid @RequestBody Mono<MoneyTransferRequest> request) {

        return moneyTransferService.servicePlace(request, authorization, merchantId, posId);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.payGiro}")
    public final Mono<MoneyTransferResponse> pay(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                 @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                 @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                 @Valid @RequestBody Mono<MoneyTransferRequest> request) {

        return moneyTransferService.servicePay(request, authorization, posId);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.cancelGiro}")
    public final Mono<MoneyTransferResponse> cancel(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                    @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                    @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                    @Valid @RequestBody Mono<MoneyTransferRequest> request) {

        return moneyTransferService.serviceCancel(request, authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.cancelGiroPortal}")
    public final Mono<MoneyTransferResponse> cancelPortal(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                          @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                          @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                          @Valid @RequestBody Mono<MoneyTransferRequest> request) {

        return moneyTransferService.serviceCancelPortal(request, authorization, merchantId, posId);
    }


    @PostMapping(value = "${spring.application.services.rest.methods.reverseGiro}")
    public final Mono<MoneyTransferResponse> reverse(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                     @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                     @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                     @Valid @RequestBody Mono<MoneyTransferRequest> request) {
        return moneyTransferService.serviceReverse(request, authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.listPendingGiro}")
    public final Mono<MoneyTransferResponse> listPending(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                         @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                         @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                         @Valid @RequestBody Mono<MoneyTransferRequest> request) {
        return moneyTransferService.servicelistPending(request, authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.methods.resendOtpGiro}")
    public final Mono<MoneyTransferResponse> resendOtp(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                       @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                       @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId,
                                                       @Valid @RequestBody Mono<MoneyTransferRequest> request) {
        return moneyTransferService.serviceResendOtp(request, authorization);
    }


    @GetMapping(value = "${spring.application.services.rest.methods.allFreights}")
    public final Mono<MoneyTransferResponse> allFreights(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                         @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                         @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId) {
        return moneyTransferService.allFreights();
    }


    @GetMapping(value = "${spring.application.services.rest.methods.typesDocuments}")
    public final Mono<MoneyTransferResponse> typesDocuments(@RequestHeader(value = ConstanHelper.AUTHORIZATION_HEADER) @NotNull String authorization,
                                                            @RequestHeader(value = ConstanHelper.MERCHANT_ID) @NotNull String merchantId,
                                                            @RequestHeader(value = ConstanHelper.POS_ID) @NotNull String posId) {

        ArrayList<DocumentType> listDocumentType = new ArrayList<>();


        DocumentType documentTypeCC = new DocumentType();
        DocumentType documentTypeCE = new DocumentType();
        DocumentType documentTypePEP = new DocumentType();
        DocumentType documentTypePAS = new DocumentType();

        documentTypeCC.setAlias(globalProperties.getAliasCC());
        documentTypeCC.setDescripcion(globalProperties.getDescripcionCC());
        documentTypeCE.setAlias(globalProperties.getAliasCE());
        documentTypeCE.setDescripcion(globalProperties.getDescripcionCE());
        documentTypePEP.setAlias(globalProperties.getAliasPEP());
        documentTypePEP.setDescripcion(globalProperties.getDescripcionPEP());
        documentTypePAS.setAlias(globalProperties.getAliasPAS());
        documentTypePAS.setDescripcion(globalProperties.getDescripcionPAS());

        listDocumentType.add(documentTypeCC);
        listDocumentType.add(documentTypeCE);
        listDocumentType.add(documentTypePEP);
        listDocumentType.add(documentTypePAS);

        return Mono.just(new MoneyTransferResponse(listDocumentType, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "00"));
    }


}

