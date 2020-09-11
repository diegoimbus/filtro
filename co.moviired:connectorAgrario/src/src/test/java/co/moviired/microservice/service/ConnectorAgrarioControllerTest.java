package co.moviired.microservice.service;

import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectorAgrarioControllerTest {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private GlobalProperties globalProperties;

    @Value("${server.servlet.context-path}")
    private String servletPath;
    @Value("${spring.application.services.rest.ping}")
    private String pingPath;
    @Value("${spring.application.services.rest.query.manual}")
    private String manualPath;
    @Value("${spring.application.services.rest.query.automatic}")
    private String automaticPath;

    @Test
    public void testPing() {
        webClient.get().uri(servletPath + pingPath).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("I'm Alive!");
    }

    @Test
    public void testProcessManual() {
        Request request = getRequest(OperationType.MANUAL);
        webClient.post().uri(servletPath + manualPath).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.outcome.error.errorCode").isEqualTo("00");
    }

    @Test
    public void testProcessAutomatic() {
        Request request = getRequest(OperationType.AUTOMATIC);
        webClient.post().uri(servletPath + automaticPath).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.outcome.error.errorCode").isEqualTo("00");
    }

    private Request getRequest(OperationType opType) {
        Input data = new Input();
        Request request = new Request();
        if (opType.equals(OperationType.MANUAL)) {
            data.setShortReferenceNumber("123456|MANUAL|00000014|CHANNEL");
        } else {
            data.setShortReferenceNumber("41577073129800158020000008120004390000003991429620200102|AUTOMATIC|00000018|CHANNEL");
        }
        data.setImei("1231213131|192.168.0.2|CHANNEL|56781234589|20200504151806.153|||004021|3482222222||174484|1234");
        data.setLastName("Tienda Oscar Moviired punto de venta @@@");
        request.setData(data);
        return request;
    }

}
