package co.moviired.topups;

import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Calendar;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopupsWebServiceTest {

    @Autowired
    public WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String servletPath;
    @Value(value = "${spring.application.services.operatorsType}")
    private String urlOperators;
    @Value(value = "${spring.application.services.recharge}")
    private String urlRecharge;

    @ParameterizedTest
    @CsvFileSource(resources = "/TestOperators.csv", numLinesToSkip = 1, delimiter = ';')
    public void testOperatorsByType(String operator, String type, String merchant, String posId, String correlationId) {
        WebTestClient.ResponseSpec res = webTestClient.get()
                .uri(servletPath.concat(urlOperators).concat("/").concat(operator))
                .accept(MediaType.APPLICATION_JSON)
                .header("type", type)
                .header("merchantId", merchant)
                .header("posId", posId)
                .header("correlationId", correlationId)
                .exchange();
        res.expectStatus().isOk();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/TestRecharge.csv", numLinesToSkip = 1, delimiter = ';')
    public void testRecharge(String cellPhoneNumber, String authorization, String contentType, String merchant, String posId,
                             String issuerName, String issuerId, String amount, String source, String ip, String productId, String packageId, String eanCode) {
        RechargeIntegrationRequest request = createRechargeIntegrationRequest(issuerName, issuerId, amount, source, ip, productId, packageId, eanCode);
        WebTestClient.ResponseSpec res = webTestClient.post()
                .uri(servletPath.concat(urlRecharge).concat("/").concat(cellPhoneNumber))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .header("Content-Type", contentType)
                .header("merchantId", merchant)
                .header("posId", posId)
                .body(Mono.just(request), RechargeIntegrationRequest.class)
                .exchange();
        res.expectStatus().isOk();
    }

    private RechargeIntegrationRequest createRechargeIntegrationRequest(
            String issuerName, String issuerId, String amount, String source,
            String ip, String productId, String packageId, String eanCode
    ) {
        RechargeIntegrationRequest request = new RechargeIntegrationRequest();
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(c.get(Calendar.HOUR));
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        String second = String.valueOf(c.get(Calendar.SECOND));
        String miliSecond = String.valueOf(c.get(Calendar.MILLISECOND));
        String value = year.concat(month).concat(day).concat(hour).concat(minute).concat(second).concat(miliSecond);
        Integer pkgAmount = Integer.parseInt(amount) - 450;
        request.setPackageAmount(pkgAmount.toString());
        request.setCorrelationId(value);
        request.setIssuerDate(value);
        request.setIssuerName(issuerName);
        request.setIssuerId(issuerId);
        request.setAmount(amount);
        request.setSource(source);
        request.setIp(ip);
        request.setProductId(productId);
        request.setPackageId(packageId);
        request.setEanCode(eanCode);
        request.setEchoData(null);
        request.setImei(null);
        return request;
    }

}

