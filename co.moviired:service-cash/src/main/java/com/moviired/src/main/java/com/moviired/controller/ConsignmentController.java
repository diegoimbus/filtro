package com.moviired.controller;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import com.moviired.model.request.RequestAuth;
import com.moviired.model.response.impl.ResponseConsultBalance;
import com.moviired.service.ConsignmentServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("${spring.application.services.rest.uri}")
public class ConsignmentController {

    private static final String LOG_PATTERN = "{}";
    private final ConsignmentServiceImpl consignmentService;

    @Autowired
    private final CryptoHelper cryptoHelperAuthorization;

    /**
     * service consultBalance (consulta el balance de un subscriber).
     *
     * @param authorization,grade,correlationId
     * @return ResponseEntity<ResponseConsultBalance>
     */
    @GetMapping(value = "${spring.application.methods.userConsultBalance}")
    public ResponseEntity<ResponseConsultBalance> consultBalance(
            @RequestHeader(value = "Authorization") String authorization,
            @RequestHeader(value = "grade", required = false) String grade,
            @RequestHeader(value = "correlationId", required = false) String correlationId) throws ParsingException {


        String auth = authorization.split(":")[0] + ":" + cryptoHelperAuthorization.encoder(authorization.split(":")[1]);
        String decodeBase64Authorization = new String(Base64.encodeBase64(auth.getBytes()));
        RequestAuth request = new RequestAuth();
        request.setAuthorization(decodeBase64Authorization);
        return consignmentService.consultBalance(request.getAuthorization(), correlationId);
    }


    /**
     * service ping
     *
     * @param
     * @return String
     */
    @GetMapping(value = "${spring.application.methods.ping}")
    public String ping() {
        log.info("It's Alive");
        return "I'm alive";
    }

}

