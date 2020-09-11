package co.moviired.digitalcontent.business.service;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.domain.entity.SubtypeOperator;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import co.moviired.digitalcontent.business.domain.enums.SellerChannel;
import co.moviired.digitalcontent.business.domain.repository.ISubtypeOperatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("${spring.application.root}")
public final class DigitalContentController {

    private static final String MERCHANT_ID = "merchantId";
    private static final String POS_ID = "posId";
    private static final String AUTHORIZATION = "Authorization";

    private final DigitalContentService digitalContentService;
    private final CryptoHelper cryptoHelper;

    // Datos para  mejorar el rendimiento
    private SubtypeOperator pinesType;
    private SubtypeOperator cardsType;

    public DigitalContentController(
            @NotNull DigitalContentService pdigitalContentService,
            @NotNull CryptoHelper pcryptoHelper,
            @NotNull ISubtypeOperatorRepository subtypeOperatorRepository) {
        super();
        this.digitalContentService = pdigitalContentService;
        this.cryptoHelper = pcryptoHelper;

        // Datos para  mejorar el rendimiento
        Optional<SubtypeOperator> subType = subtypeOperatorRepository.findBySubtype("Pines");
        subType.ifPresent(type -> this.pinesType = type);

        subType = subtypeOperatorRepository.findBySubtype("Tarjeta");
        subType.ifPresent(type -> this.cardsType = type);
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public Mono<String> echo() {
        return digitalContentService.getEcho();
    }

    // PINES
    @PostMapping(value = "${spring.application.services.rest.pines.pines}")
    public Mono<DigitalContentResponse> pinesActivation(@NotNull ServerHttpRequest httpRequest,
                                                        @RequestHeader(name = MERCHANT_ID) @NotNull String merchantId,
                                                        @RequestHeader(name = POS_ID) @NotNull String posId,
                                                        @RequestHeader(name = AUTHORIZATION) @NotNull String userpass,
                                                        @NotNull @RequestBody Mono<DigitalContentRequest> request) {
        return digitalContentService.service(httpRequest, OperationType.DIGITAL_CONTENT_PINES_SALE, request, userpass, merchantId, posId);
    }

    @DeleteMapping(value = "${spring.application.services.rest.pines.pines}")
    public Mono<DigitalContentResponse> pinesInactivation(@NotNull ServerHttpRequest httpRequest,
                                                          @RequestHeader(name = MERCHANT_ID) @NotNull String merchantId,
                                                          @RequestHeader(name = POS_ID) @NotNull String posId,
                                                          @RequestHeader(name = AUTHORIZATION) @NotNull String userpass,
                                                          @NotNull @RequestBody Mono<DigitalContentRequest> request) {
        return digitalContentService.service(httpRequest, OperationType.DIGITAL_CONTENT_PINES_INACTIVATE, request, userpass, merchantId, posId);
    }

    // CARDS
    @PostMapping(value = "${spring.application.services.rest.card.card}")
    public Mono<DigitalContentResponse> cardActivation(@NotNull ServerHttpRequest httpRequest,
                                                       @RequestHeader(name = MERCHANT_ID) @NotNull String merchantId,
                                                       @RequestHeader(name = POS_ID) @NotNull String posId,
                                                       @RequestHeader(name = AUTHORIZATION) @NotNull String userpass,
                                                       @NotNull @RequestBody Mono<DigitalContentRequest> request) {
        return digitalContentService.service(httpRequest, OperationType.DIGITAL_CONTENT_CARD_ACTIVATE, request, userpass, merchantId, posId);
    }

    @DeleteMapping(value = "${spring.application.services.rest.card.card}")
    public Mono<DigitalContentResponse> cardInactivation(@NotNull ServerHttpRequest httpRequest,
                                                         @RequestHeader(name = MERCHANT_ID) @NotNull String merchantId,
                                                         @RequestHeader(name = POS_ID) @NotNull String posId,
                                                         @RequestHeader(name = AUTHORIZATION) @NotNull String userpass,
                                                         @NotNull @RequestBody Mono<DigitalContentRequest> request) {
        return digitalContentService.service(httpRequest, OperationType.DIGITAL_CONTENT_CARD_INACTIVATE, request, userpass, merchantId, posId);
    }

    // CATEGORIES
    @GetMapping(value = "${spring.application.services.rest.pines.categories}")
    public Mono<DigitalContentResponse> getPinesCategories(@RequestHeader @NotNull SellerChannel source) {
        return digitalContentService.getCategories(source, pinesType);
    }

    @GetMapping(value = "${spring.application.services.rest.pines.getCategory}")
    public DigitalContentResponse getPinesCategory(@PathVariable @NotNull Integer id, @RequestHeader @NotNull SellerChannel source) {
        return digitalContentService.getCategory(source, id, pinesType);
    }

    @GetMapping(value = "${spring.application.services.rest.card.categories}")
    public Mono<DigitalContentResponse> getCardsCategories(@RequestHeader @NotNull SellerChannel source) {
        return digitalContentService.getCategories(source, cardsType);
    }

    @GetMapping(value = "${spring.application.services.rest.card.getCategory}")
    public DigitalContentResponse getCardsCategory(@PathVariable @NotNull Integer id, @RequestHeader @NotNull SellerChannel source) {
        return digitalContentService.getCategory(source, id, cardsType);
    }

    @PostMapping(value = "${spring.application.services.rest.pines.resend}")
    public Mono<DigitalContentResponse> resendPin(@NotNull ServerHttpRequest httpRequest,
                                                  @RequestHeader(name = MERCHANT_ID) @NotNull String merchantId,
                                                  @RequestHeader(name = POS_ID) @NotNull String posId,
                                                  @RequestHeader(name = AUTHORIZATION) @NotNull String userpass,
                                                  @NotNull @RequestBody Mono<DigitalContentRequest> request) {
        return digitalContentService.resendPin(httpRequest, request, userpass, merchantId, posId);
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////ENCRYPT/DECRYPT/////////////////////////////
    ////////////////(OJO! Sólo para desarrollo)///////////////////////
    //////////////////////////////////////////////////////////////////
    @GetMapping(value = "/encrypt/{data}")
    public String encrypt(@PathVariable(name = "data") String data) throws ParsingException {
        return cryptoHelper.encoder(data);
    }

    @GetMapping(value = "/decrypt/{data}")
    public String decrypt(@PathVariable(name = "data") String data) throws ParsingException {
        return cryptoHelper.decoder(data);
    }
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////


}

