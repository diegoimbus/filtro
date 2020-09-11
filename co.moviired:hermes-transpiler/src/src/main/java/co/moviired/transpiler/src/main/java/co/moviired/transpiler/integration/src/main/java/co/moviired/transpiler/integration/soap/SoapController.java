package co.moviired.transpiler.integration.soap;

import co.moviired.transpiler.conf.soap.SoapConfig;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivation;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivationResponse;
import co.moviired.transpiler.integration.soap.service.SoapService;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

@Slf4j
@Endpoint
public class SoapController {

    private final SoapService soapService;

    public SoapController(SoapService psoapService) {
        this.soapService = psoapService;
    }

    @PayloadRoot(namespace = SoapConfig.NAMESPACE_URI, localPart = "prepaidProductsActivation")
    @ResponsePayload
    public final PrepaidProductsActivationResponse prepaidProductsActivation(@RequestPayload PrepaidProductsActivation tpRequest) {
        return soapService.proccess(OperationType.TOPUP, Mono.just(tpRequest)).block();
    }

}

