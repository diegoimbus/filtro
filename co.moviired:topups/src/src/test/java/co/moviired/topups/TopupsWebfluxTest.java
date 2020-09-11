package co.moviired.topups;


import co.moviired.topups.exception.ParseException;
import co.moviired.topups.mahindra.parser.impl.RechargeMahindraParser;
import co.moviired.topups.model.domain.Operator;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationRequest;
import co.moviired.topups.model.enums.OperatorType;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopupsWebfluxTest {


    private final String productId = "1";
    private final String operatorErrorMessage = "Operador inválido";
    @Autowired
    RechargeMahindraParser parse;
    @Autowired
    private WebTestClient webClient;
    @Value("${server.servlet.context-path}")
    private String servletPath;
    @Value("${spring.application.services.ping}")
    private String pingUri;
    @Value("${spring.application.services.recharge}")
    private String rechargeUri;
    @Value("${spring.application.services.operators}")
    private String operatorsUri;

    @Test
    public void testProcessRechargeIntegrationSuccessful() {
        RechargeIntegrationRequest request = getRequest(String.valueOf(randomInteger()), null);
        request.setIssuerName("Recharge Successful");
        webClient.post().uri(servletPath + rechargeUri + "/3168353333").accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json")
                .header("Authorization", "3602505689:0124")
                .header("merchantId", "0000001603")
                .header("posId", "12345678")
                .body(Mono.just(request), RechargeIntegrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errorCode").isEqualTo("00")
                .jsonPath("$.correlationId").isEqualTo(request.getCorrelationId())
        ;

    }

    @Test
    public void testPing() {
        webClient.get().uri(servletPath + pingUri).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("I'm Alive!");
    }


    @Test
    public void testProcessRechargeIntegrationInvalidOperator() {
        RechargeIntegrationRequest request = getRequest(String.valueOf(randomInteger()), null);
        request.setEanCode("7707175322811");
        webClient.post().uri(servletPath + rechargeUri + "/3168353333").accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json")
                .header("Authorization", "3124394759:1234")
                .header("merchantId", "0000001603")
                .header("posId", "1nv4l1d0p3r4t0r")
                .body(Mono.just(request), RechargeIntegrationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errorCode").isEqualTo("500")
                .jsonPath("$.errorMessage").isEqualTo("Operador inválido")
        ;

    }


    @Test
    public void getOperatorByEanCode() throws ParseException {
        String logIdent = this.getClass().getName() + ".getOperatorByEanCode()";
        String eanCode = "7707175322809";
        String productId = this.productId;
        Operator operator = parse.getOperatorValidatingSearchParams(logIdent, eanCode, productId, OperatorType.PACKAGE);
        assertNotNull("could not get object from database", operator);
    }

    //	@Test
    public void getOperatorByProdCodeRecharge() throws ParseException {
        String logIdent = this.getClass().getName() + ".getOperatorByProdCodeRecharge()";
        String eanCode = null;
        String productId = this.productId;
        Operator operator = parse.getOperatorValidatingSearchParams(logIdent, eanCode, productId, OperatorType.RECHARGE);
        assertNotNull("could not get object from database", operator);
    }

    @Test
    public void getOperatorByProdCodePackage() throws ParseException {
        String logIdent = this.getClass().getName() + ".getOperatorByProdCodePackage()";
        String eanCode = null;
        String productId = "50029";
        Operator operator = parse.getOperatorValidatingSearchParams(logIdent, eanCode, productId, OperatorType.PACKAGE);
        assertNotNull("could not get object from database", operator);
    }

    @Test
    public void getOperatorFailure() {
        String logIdent = this.getClass().getName() + ".getOperatorFailure()";
        String eanCode = null;
        String productId = null;
        try {
            parse.getOperatorValidatingSearchParams(logIdent, eanCode, productId, OperatorType.PACKAGE);
        } catch (ParseException e) {
            assertEquals("sin productId ni eanCode debe catchear error", operatorErrorMessage, e.getMessage());
        }

    }

    @Test
    public void getOperatorByExpectingRechargeWithPackageParams() {
        String logIdent = this.getClass().getName() + ".getOperatorByExpectingRechargeWithPackageParams()";
        String eanCode = null;
        String productId = this.productId;
        try {
            parse.getOperatorValidatingSearchParams(logIdent, eanCode, productId, OperatorType.PACKAGE);
        } catch (ParseException e) {
            assertEquals("sin productId ni eanCode debe catchear error", operatorErrorMessage, e.getMessage());
        }

    }

    @Test
    public void testGetOperatorById() {
        webClient.get().uri(servletPath + operatorsUri + "/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errorCode").isEqualTo("00")
                .jsonPath("$.errorMessage").isEqualTo("OK");
    }


    @Test
    public void testGetOperatorAllOpt() {
        webClient.get().uri(servletPath + operatorsUri).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errorCode").isEqualTo("00")
                .jsonPath("$.errorMessage").isEqualTo("OK");
    }

    private int randomInteger() {
        int min = 5000;
        int max = 8500;
        Random r = new Random();
        boolean withouHundredMultiple = true;
        int amount = 0;
        do {
            amount = r.nextInt((max - min) + 1) + min;
            withouHundredMultiple = !((amount % 100) == 0);
        } while (withouHundredMultiple);
        return amount;
    }

    private RechargeIntegrationRequest getRequest(String amount, String echoData) {
        RechargeIntegrationRequest request = new RechargeIntegrationRequest();
        request.setAmount(amount);
        Integer pkgAmount = Integer.parseInt(request.getAmount()) - 450;
        request.setPackageAmount(pkgAmount.toString());
        String id = UUID.randomUUID().toString();
        request.setCorrelationId(id.replaceAll("-", "").substring((id.length() / 2)));
        request.setEanCode("7707175322809");
        request.setEchoData(echoData);
        request.setIp("192.168.31.1");
        request.setIssuerDate("2019-02-15 16:15:36.262");
        request.setIssuerId("801885087");
        request.setIssuerName("Carlos Ramirez");
        request.setProductId("1");
        request.setSource("API");
        request.setPackageId(null);
        request.setImei("99999999999999");
        return request;
    }

}

