package com.moviired.support.test;

import co.moviired.support.SupportProfilesApplication;
import co.moviired.support.domain.dto.Response;
import co.moviired.support.service.SupportProfilesService;
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
@SpringBootTest(classes = SupportProfilesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SpringBootProfileTests {

    @Autowired
    SupportProfilesService supportProfilesService;

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
    public void testGetAuthoritiesListSuccess(){
        log.info("--------------------------------------------------------");
        log.info("GET AUTHORITIES -->");
        Response response = supportProfilesService.findAuthorities(Mono.just("ADMIN"), "0000000012").block();

        Assert.assertEquals("00", response.getErrorCode());
        Assert.assertEquals(true, response.getAuthorities().size() > 0);
        log.info("--------------------------------------------------------");

    }

    @Test
    public void testGetProfileStatusListSuccess(){
        log.info("--------------------------------------------------------");
        log.info("GET PROFILE BY STATUS -->");
        Response response = supportProfilesService.serviceFindProfileByStatus(Mono.just(true), "000000013").block();

        Assert.assertEquals("00", response.getErrorCode());
        Assert.assertEquals(true, response.getProfiles().size() > 0);
        log.info("--------------------------------------------------------");

    }
}


