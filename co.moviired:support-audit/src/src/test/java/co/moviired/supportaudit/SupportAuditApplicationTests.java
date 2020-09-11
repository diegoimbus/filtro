package co.moviired.supportaudit;

import co.moviired.supportaudit.domain.response.Response;
import co.moviired.supportaudit.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportAuditApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class SupportAuditApplicationTests {


	@Autowired
	AuditService auditService;

	@LocalServerPort
	int randomServerPort;

	/* INVOCAR DESDE EEL CONTROLLER
    @Test
    public void testGetAuthoritiesListSuccess() throws URISyntaxException {
        log.info("--------------------------------------------------------");
        log.info("GET AUTHORITIES -->");
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/moviired-api/support-profiles/v1/profiles/authorities/ADMIN";
        log.info("URL= " + baseUrl);
        URI uri = new URI(baseUrl);
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        //Verify request succeed
        Assert.assertEquals(200, result.getStatusCodeValue());
        Assert.assertEquals(true, result.getBody().contains("authorities"));
        log.info("--------------------------------------------------------");
    }*/
	@Test
	public void testGetAllAuditListSuccess(){
		log.info("--------------------------------------------------------");
		log.info("GET AUTHORITIES -->");
		Response response = auditService.getAllAudit().block();

		Assert.assertEquals("00", response.getCode());
		Assert.assertEquals(true, response.getAudits().size() > 0);
		log.info("--------------------------------------------------------");

	}
}

