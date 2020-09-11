package com.moviired.support.test;

import co.moviired.support.SupportUsersApplication;
import co.moviired.support.domain.dto.Response;
import co.moviired.support.service.SupportUsersService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportUsersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SpringBootUserTests {

    @Autowired
    SupportUsersService supportUsersService;

    @LocalServerPort
    int randomServerPort;
    @Test
    public void testGetAllUsers() {
        log.info("--------------------------------------------------------");
        log.info("GET ALL USERS -->");
        Response response = supportUsersService.findAllUsers( "0000000012");

        Assert.assertEquals("00", response.getErrorCode());
        Assert.assertEquals(true, response.getUsers().size() > 0);
        log.info("--------------------------------------------------------");

    }

}


