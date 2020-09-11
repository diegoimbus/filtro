package co.moviired.support.controller;

import co.moviired.base.helper.CryptoHelper;
import co.moviired.support.domain.request.impl.*;
import co.moviired.support.domain.response.*;
import co.moviired.support.service.ConsignmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category ConsignmentController
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "${server.servlet.context-path}")
public class ConsignmentController {

    private static final String LOG_PATTERN = "{}";
    private final ConsignmentService consignmentService;

    @Autowired
    private final CryptoHelper cryptoHelperAuthorization;

    @PostMapping(value = "${spring.application.services.rest.userConsignmentsApprove}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseApproveConsignment> approveConsignment(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody ApproveConsignment iRequest) {
        log.info(LOG_PATTERN, iRequest);
        return consignmentService.approveConsignment(authorization, correlationId, iRequest);
    }

    @PostMapping(value = "${spring.application.services.rest.userConsignmentsRegistry}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentRegistry> consignmentRegistry(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody ConsignmentRegistry request) {

        log.info(LOG_PATTERN, request);
        return consignmentService.consignmentRegistry(authorization, request);
    }

    @PostMapping(value = "${spring.application.services.rest.userConsignmentsUpdater}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentRegistry> userConsignmentsUpdater(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody ConsignmentRegistry request) {

        log.info(LOG_PATTERN, request);
        return consignmentService.consignmentUpdater(authorization, request);
    }

    @GetMapping(value = "${spring.application.services.rest.bankingList}"  , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseBankSearch> listBanks(@RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId) {

        return consignmentService.listBanks();
    }

    @PostMapping(value = "${spring.application.services.rest.consignments}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentSearch> listConsignments(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody ConsignmentSearch request) {
        log.info(LOG_PATTERN, request);
        return consignmentService.listConsignments(request);
    }

    @PostMapping(value = "${spring.application.services.rest.userConsignments}"  , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentSearch> listUserConsignments(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody UserConsignmentSearch request) {
        log.info(LOG_PATTERN, request);
        return consignmentService.listUserConsignments(request);
    }

    @PostMapping(value = "${spring.application.services.rest.userConsignmentsReject}"  , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentReject> rejectConsignment(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody ConsignmentReject iRequest) {

        log.info(LOG_PATTERN, iRequest);
        return consignmentService.rejectConsignment(authorization, correlationId, iRequest);
    }

    @PostMapping(value = "${spring.application.services.rest.consignmentDetail}"  , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponseConsignmentSearch> viewConsignmentDetail(
            @RequestHeader(value = "Authorization") String authorization, @RequestHeader(value = "correlationId", required = false) String correlationId, @RequestBody UserConsignmentSearch request) {
        log.info(LOG_PATTERN, request);
        return consignmentService.viewConsignmentDetail(request);
    }

    @GetMapping(value = "${spring.application.services.rest.userConsultVoucher}/{id}")
    public ResponseEntity<IResponseConsignmentSearch> viewConsignmentVoucher(

            @PathVariable(value = "id") String id,
            @RequestHeader(value = "Authorization") String authorization,
                @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return consignmentService.viewConsignmentVoucher(id);
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public String ping() {
        log.info("It's Alive");
        return "I'm alive by Gelver";
    }

}

