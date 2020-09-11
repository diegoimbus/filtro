package co.moviired.support.controller;

import co.moviired.support.domain.dto.BarTemplateDTO;
import co.moviired.support.domain.dto.UnbarBarHistoryDTO;
import co.moviired.support.domain.entity.account.BarUnbarConfig;
import co.moviired.support.domain.repository.account.IBarUnbarConfigRepository;
import co.moviired.support.domain.response.impl.ResponseBarTemplate;
import co.moviired.support.domain.response.impl.ResponseBarUnbarHistory;
import co.moviired.support.service.BarUnbarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category ConsignmentController
 */

@Slf4j
@RestController
@RequestMapping(value = "${server.servlet.context-path}")
public final class BarUnbarController {


    private static final int NUMBER_5 = 15;
    private static final int NUMBER_20 = 20;
    private static final String ERROR_FATAL = "Falta realizar la configuración de bar/unbar en BD.";
    private static final String CORRELATIONID = "correlationId";
    private static final String AUTHORIZATION = "Authorization";
    private final BarUnbarService barUnbarService;
    private final IBarUnbarConfigRepository barUnbarConfigRepository;
    private final Random random;

    public BarUnbarController(@NotNull BarUnbarService pbarUnbarService,
                              IBarUnbarConfigRepository pbarUnbarConfigRepository) {
        super();
        this.barUnbarService = pbarUnbarService;
        this.barUnbarConfigRepository = pbarUnbarConfigRepository;
        random = new Random();
        Optional<BarUnbarConfig> oConfig = this.barUnbarConfigRepository.findById(1);
        BarUnbarConfig config;
        if (oConfig.isPresent()) {
            config = oConfig.get();
            config.setRunningUnbar(false);
            config.setRunningBar(false);
            this.barUnbarConfigRepository.save(config);
        } else {
            log.info(ERROR_FATAL);
        }
    }

    @GetMapping(value = "${spring.application.services.rest.pingUnbar}")
    public String ping() {
        log.info("It's Alive");
        return "I'm alive";
    }

    @PostMapping(value = "${spring.application.services.rest.historyBarUnbar}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarUnbarHistory history(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody UnbarBarHistoryDTO request) {
        return barUnbarService.barUnbarHistory(correlationId, request);
    }

    @GetMapping(value = "${spring.application.services.rest.templateBar}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarTemplate getTemplateBar(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId) {
        return barUnbarService.getTemplateBar(correlationId);
    }

    @PostMapping(value = "${spring.application.services.rest.templateBar}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarTemplate saveTemplateBar(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody BarTemplateDTO barTemplate) {
        return barUnbarService.saveTemplateBar(correlationId, barTemplate.toEntity(), authorization);
    }

    @PutMapping(value = "${spring.application.services.rest.templateBar}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBarTemplate updateTemplateBar(
            @RequestHeader(value = AUTHORIZATION) String authorization,
            @RequestHeader(value = CORRELATIONID, required = false) String correlationId,
            @RequestBody BarTemplateDTO barTemplate) {
        return barUnbarService.updateTemplateBar(correlationId, barTemplate.toEntity(), authorization);
    }


    @Scheduled(cron = "${spring.application.jobs.barAccount.cron}")
    public synchronized void barAccount() throws InterruptedException {

        sleepThread();

        Optional<BarUnbarConfig> oConfig = this.barUnbarConfigRepository.findById(1);
        BarUnbarConfig config;
        if (oConfig.isPresent()) {
            config = oConfig.get();
        } else {
            log.info(ERROR_FATAL);
            return;
        }

        if (config.getEnabledBar().booleanValue() && !config.getRunningBar().booleanValue()) {
            config.setRunningBar(true);
            this.barUnbarConfigRepository.save(config);
            // Ejecutar el JOB
            this.barUnbarService.barAccount(config);
        }
    }

    @Scheduled(cron = "${spring.application.jobs.unbarAccount.cron}")
    public synchronized void unBarAccount() throws InterruptedException {

        sleepThread();

        Optional<BarUnbarConfig> oConfig = this.barUnbarConfigRepository.findById(1);
        BarUnbarConfig config;
        if (oConfig.isPresent()) {
            config = oConfig.get();
        } else {
            log.info(ERROR_FATAL);
            return;
        }

        if (config.getEnabledUnbar().booleanValue() && !config.getRunningUnbar().booleanValue()) {
            config.setRunningUnbar(true);
            this.barUnbarConfigRepository.save(config);
            // Ejecutar el JOB
            this.barUnbarService.unBarAccount(config);
        }
    }

    private void sleepThread() throws InterruptedException {
        // TIEMPO DELAY RANDOM PENSADO PARA VARIAS INSTANCIAS.
        TimeUnit.SECONDS.sleep((this.random.nextInt((NUMBER_20 - NUMBER_5) + 1) + NUMBER_5));
    }

}

