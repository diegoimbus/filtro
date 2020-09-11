package co.moviired.support.controller;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.support.domain.dto.GradeDTO;
import co.moviired.support.domain.request.RequestAuth;
import co.moviired.support.domain.request.impl.Request;
import co.moviired.support.domain.response.impl.ResponseBarExemptionDays;
import co.moviired.support.domain.response.impl.ResponseConsultBalance;
import co.moviired.support.domain.response.impl.ResponseGrade;
import co.moviired.support.domain.response.impl.ResponseStatementAccounts;
import co.moviired.support.service.StatementAccountsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "${server.servlet.context-path}")
public final class StatementAccountsController {
    private static final String CORRELATIONID = "correlationId";
    private static final String AUTHORIZATION = "Authorization";
    private static final String PATH_GRADE = "${spring.application.services.rest.grade}";
    private static final String LOG_PATTERN = "{}";
    private final StatementAccountsService statementAccountsService;


    @Autowired
    private final CryptoHelper cryptoHelperAuthorization;


    @PostMapping(value = "${spring.application.services.rest.userConsultBalance}")
    public ResponseConsultBalance consultBalance(@RequestHeader(value = CORRELATIONID, required = false) String correlationId,
                                                 @RequestHeader(value = "grade", required = false) String grade,
                                                 @RequestBody RequestAuth request) {
        return statementAccountsService.consultBalance(request.getAuthorization(), correlationId, grade);
    }


    @GetMapping(value = "${spring.application.services.rest.userConsultBalance}")
    public ResponseConsultBalance consultBalance(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = "grade", required = false) String grade,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId) throws ParsingException {


        String auth = authorization.split(":")[0] + ":" + cryptoHelperAuthorization.encoder(authorization.split(":")[1]);
        String decodeBase64Authorization = new String(Base64.encodeBase64(auth.getBytes()));
        RequestAuth request = new RequestAuth();
        request.setAuthorization(decodeBase64Authorization);
        return statementAccountsService.consultBalance(request.getAuthorization(), correlationId, grade);
    }


    // CRUD GRADOS

    @GetMapping(value = PATH_GRADE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseGrade getGrade(
            @RequestHeader(value = AUTHORIZATION) String authorization, @RequestHeader(value = CORRELATIONID, required = false) String correlationId) {
        return statementAccountsService.getGrade(correlationId);
    }

    @PostMapping(value = PATH_GRADE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseGrade save(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody GradeDTO request) {
        return statementAccountsService.saveGrade(correlationId, request.toEntity(), authorization);
    }

    @PutMapping(value = PATH_GRADE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseGrade update(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody GradeDTO request) {
        return statementAccountsService.updateGrade(correlationId, request.toEntity(), authorization);
    }

    @DeleteMapping(value = PATH_GRADE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseGrade delete(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody GradeDTO request) {
        return statementAccountsService.deleteGrade(correlationId, request.toEntity(), authorization);
    }

    // CRUD DIAS EXCENTOS

    @GetMapping(value = "${spring.application.services.rest.exemptionDays}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarExemptionDays getExemptionDays(
            @RequestHeader(value = AUTHORIZATION) String authorization, @RequestHeader(value = CORRELATIONID, required = false) String correlationId) {
        return statementAccountsService.getExemptionDays(correlationId);
    }

    @PostMapping(value = "${spring.application.services.rest.exemptionDays}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarExemptionDays saveExemptionDays(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody Request request) {
        return statementAccountsService.saveExemptionDays(correlationId, request, authorization);
    }

    @PutMapping(value = "${spring.application.services.rest.exemptionDays}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarExemptionDays updateExemptionDays(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody Request request) {
        return statementAccountsService.updateExemptionDays(correlationId, request, authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.exemptionDaysDelete}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarExemptionDays deleteExemptionDays(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody Request request) {
        return statementAccountsService.deleteExemptionDays(correlationId, request, authorization);
    }

    // OBTENER ESTADO DE CUENTA

    @GetMapping(value = "${spring.application.services.rest.statementAccount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseStatementAccounts getStatementAccount(
            @RequestHeader(value = AUTHORIZATION) String authorization, @RequestHeader(value = CORRELATIONID, required = false) String correlationId) {
        return statementAccountsService.getStatementAccounts(correlationId, authorization.split(":")[0], true);
    }

}

