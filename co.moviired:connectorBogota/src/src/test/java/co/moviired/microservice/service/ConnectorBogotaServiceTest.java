package co.moviired.microservice.service;

import co.moviired.microservice.ConnectorBogotaApplication;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = ConnectorBogotaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectorBogotaServiceTest {

    private static final String RESPONSE_STATUS_OK = "-1";

    @Autowired
    ConnectorBogotaService connectorBogotaService;

    @Test
    void serviceManualQuery() {

        log.info("************ INICIANDO - TEST - PROCESO DE serviceManualQuery CONNECTOR BOGOTA ************");

        Input parameters = new Input();
        Request request = new Request();

        parameters.setShortReferenceNumber("12346|MANUAL|6849|CHANNEL");
        parameters.setImei("1231213131|192.168.0.2|CHANNEL|5454554577222996|20200114152615.153|||174484|3482222222||174484|1234|11");
        parameters.setLastName("Tienda Miss Laura Moviired punto de venta @@@");
        request.setData(parameters);

        assertEquals(RESPONSE_STATUS_OK, connectorBogotaService.service(Mono.just(request), OperationType.QUERY).block().getOutcome().getError().getErrorCode());

        log.info("************ FINALIZADO - TEST - PROCESO DE serviceManualQuery CONNECTOR BOGOTA ************");
    }


}
