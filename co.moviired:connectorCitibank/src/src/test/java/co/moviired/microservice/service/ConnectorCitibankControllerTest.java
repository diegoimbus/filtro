package co.moviired.microservice.service;

import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient(timeout = "${client.citiBank.timeout.test}")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectorCitibankControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Value("${server.servlet.context-path}")
    private String servletPath;
    @Value("${spring.application.services.rest.ping}")
    private String pingPath;
    @Value("${spring.application.services.rest.query.manual}")
    private String manualPath;
    @Value("${spring.application.services.rest.payment}")
    private String payment;

    private static String jsonPath = "$.outcome.error.errorType";

    @Test
    public void ping() {
        webClient.get().uri(servletPath + pingPath).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("I'm Alive!");
    }

    @Test
    public void processManualActive() {
        Request request = getRequest(OperationType.MANUAL, "A");
        webClient.post().uri(servletPath + manualPath).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(jsonPath).isEqualTo("0");
    }

    @Test
    public void processManualPassive() {
        Request request = getRequest(OperationType.MANUAL, "P");
        webClient.post().uri(servletPath + manualPath).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(jsonPath).isEqualTo("0");
    }


    public void processPaymentActive() {
        Request request = getRequest(OperationType.PAYMENT, "A");
        webClient.post().uri(servletPath + payment).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(jsonPath).isEqualTo("00");
    }


    public void processPaymentPassive() {
        Request request = getRequest(OperationType.PAYMENT, "P");
        webClient.post().uri(servletPath + payment).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), Request.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(jsonPath).isEqualTo("0");
    }

    private Request getRequest(OperationType opType, String transactionType) {
        Request request = new Request();
        Input data = new Input();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHMMss");

        String dateFormated = sdf.format(new Date());
        String referenceNumber = (transactionType.equals("A")) ? "46202005290006" : "46202005290";
        String echoData = "3038";

        data.setShortReferenceNumber(referenceNumber.concat("|MANUAL|19464164|SUBSCRIBER"));
        data.setImei("1231213131|192.168.0.2|CHANNEL|" + dateFormated + "1|20200515172615.153|||004021|3482222222||174484|1234");
        if (opType.equals(OperationType.MANUAL)) {
            data.setEchoData(echoData);
        } else {
            data.setValueToPay("10");
            data.setEchoData(echoData.concat(transactionType.equals("A") ? "Y|20191130" : "N|").concat("|Y"));
        }
        request.setData(data);
        return request;
    }

}
