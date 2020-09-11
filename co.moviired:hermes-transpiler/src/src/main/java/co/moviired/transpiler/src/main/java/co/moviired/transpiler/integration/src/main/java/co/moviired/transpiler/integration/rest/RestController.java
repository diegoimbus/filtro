package co.moviired.transpiler.integration.rest;

import co.moviired.transpiler.conf.DigitalContentProperties;
import co.moviired.transpiler.integration.rest.dto.IRestResponse;
import co.moviired.transpiler.integration.rest.dto.billpay.request.RequestBillPayDTO;
import co.moviired.transpiler.integration.rest.dto.digitalcontent.request.RequestDigitalContentDTO;
import co.moviired.transpiler.integration.rest.dto.topup.request.RequestTopUpDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.request.RequestValidateBillByEanDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.request.RequestValidateBillByReferenceDTO;
import co.moviired.transpiler.integration.rest.service.RestService;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("${spring.application.services.rest.uri}")
public class RestController {

    private final IUserRepository userRepository;
    private final RestService restService;
    private final DigitalContentProperties gtp;

    public RestController(IUserRepository puserRepository, RestService prestService, DigitalContentProperties gtp) {
        this.userRepository = puserRepository;
        this.restService = prestService;
        this.gtp = gtp;
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        // Verificar que la conexión a BD esté activa
        this.userRepository.findAll();

        // Si no hay error con la BD, return OK!
        return Mono.just("OK");
    }

    @PostMapping(value = "${spring.application.services.rest.topUp.processTopUpTransaction}")
    public final IRestResponse processTopUpTransaction(@RequestBody RequestTopUpDTO tpRequest) {
        return restService.proccess(OperationType.TOPUP, Mono.just(tpRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.billPayment.validateBillPaymentByReference}")
    public final IRestResponse validateBillPaymentByReference(@RequestBody RequestValidateBillByReferenceDTO billRequest) {
        return restService.proccess(OperationType.VALIDATE_BILL_REFERENCE, Mono.just(billRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.billPayment.validateBillPaymentByEANCode}")
    public final IRestResponse validateBillPaymentByEANCode(@RequestBody RequestValidateBillByEanDTO billRequest) {
        return restService.proccess(OperationType.VALIDATE_BILL_EAN, Mono.just(billRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.billPayment.billPay}")
    public final IRestResponse billPay(@RequestBody RequestBillPayDTO billRequest) {
        return restService.proccess(OperationType.BILL_PAY, Mono.just(billRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.digitalContent.activate}")
    public final IRestResponse processDigitalContentActivate(@RequestBody RequestDigitalContentDTO tpRequest) {
        tpRequest.getData().setOperation(gtp.getCodeActivate());
        return restService.proccess(OperationType.DIGITAL_CONTENT_CARD, Mono.just(tpRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.digitalContent.inactivate}")
    public final IRestResponse processDigitalContentInactivate(@RequestBody RequestDigitalContentDTO tpRequest) {
        tpRequest.getData().setOperation(gtp.getCodeInactivate());
        return restService.proccess(OperationType.DIGITAL_CONTENT_CARD, Mono.just(tpRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.digitalContent.pinesSale}")
    public final IRestResponse processDigitalContentPinesSale(@RequestBody RequestDigitalContentDTO tpRequest) {
        tpRequest.getData().setOperation(gtp.getCodePinesSale());
        return restService.proccess(OperationType.DIGITAL_CONTENT_PINES, Mono.just(tpRequest)).block();
    }

    @PostMapping(value = "${spring.application.services.rest.digitalContent.pinesInactivate}")
    public final IRestResponse processDigitalContentPinesInactivate(@RequestBody RequestDigitalContentDTO tpRequest) {
        tpRequest.getData().setOperation(gtp.getCodePinesInactivate());
        return restService.proccess(OperationType.DIGITAL_CONTENT_PINES, Mono.just(tpRequest)).block();
    }

}

