package co.moviired.support.service;


import co.moviired.support.domain.request.impl.ApproveConsignment;
import co.moviired.support.domain.request.impl.ConsignmentRegistry;
import co.moviired.support.domain.request.impl.ConsignmentReject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectorConsignmentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Value("${server.servlet.context-path}")
    private String servletPath;
    @Value("${spring.application.services.rest.ping}")
    private String pingPath;
    @Value("${spring.application.services.rest.userConsignmentsRegistry}")
    private String userConsignmentsRegistry;
    @Value("${spring.application.services.rest.userConsignmentsApprove}")
    private String approveConsignment;
    @Value("${spring.application.services.rest.userConsignmentsReject}")
    private String rejectConsignment;

    @Test
    public void testPing() {
        webClient.get().uri(servletPath + pingPath).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("I'm alive by Gelver");
    }

    @Test
    public void testConsignmentsRegistry() {
        ConsignmentRegistry request = getRequestRegistry();
        webClient.post().uri(servletPath + userConsignmentsRegistry).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ConsignmentRegistry.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testApproveConsignment() {
        ApproveConsignment request = getRequestApprove();
        webClient.post().uri(servletPath + approveConsignment).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ConsignmentRegistry.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testRejectConsignment() {
        ConsignmentReject request = getRequestReject();
        webClient.post().uri(servletPath + rejectConsignment).accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ConsignmentRegistry.class)
                .exchange()
                .expectStatus().isBadRequest();
    }


    private ConsignmentRegistry getRequestRegistry() {
        ConsignmentRegistry request = new ConsignmentRegistry();

        String empty = "";
        Date date = new Date();

        request.setId(null);
        request.setCorrelationId(empty);
        request.setBankId("BK181008.0930.010331");
        request.setPaymentDate("2020-03-19");
        request.setAmount("70000");
        request.setRegistryDate(date);
        request.setAgreementNumber("756765431255");
        request.setPaymentReference("7656765165");
        request.setState("Bogotá DC");
        request.setCity("Bogotá");
        request.setBranchOffice("Bogotá");
        request.setAuthorization("MzEwMDAwMDAwMDoxayt4TFRSdy93NEdIREhBR0tTYkpRPT0=");
        request.setVoucher("");
        request.setNameClient("Andrea Varela");
        request.setNameAlliance("Yuly");
        request.setUsernamePortalRegistry("andrea");

        return request;
    }

    private ApproveConsignment getRequestApprove() {
        ApproveConsignment request = new ApproveConsignment();

        request.setCorrelationId("20051418395508989");
        request.setUsernamePortalAuthorizer("ronel");

        return request;
    }

    private ConsignmentReject getRequestReject() {
        ConsignmentReject request = new ConsignmentReject();

        request.setCorrelationId("20051412174575973");
        request.setReason("Razón 2");
        request.setUsernamePortalAuthorizer("rrivas");

        return request;
    }

}
