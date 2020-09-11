package co.moviired.register.controllers;

import co.moviired.register.domain.dto.RegisterDTO;
import co.moviired.register.domain.enums.ado.AdoProcess;
import co.moviired.register.domain.enums.register.DocumentType;
import co.moviired.register.helper.UtilsHelper;
import co.moviired.register.helper.schedulerhelper.SchedulerHelperService;
import co.moviired.register.properties.SchedulersConfigurationProperties;
import co.moviired.register.properties.ServiceActivationProperties;
import co.moviired.register.properties.SubsidyProperties;
import co.moviired.register.service.MoviiService;
import co.moviired.register.service.MoviiredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static co.moviired.register.helper.ConstantsHelper.*;

@Slf4j
@RestController
@RequestMapping(PROJECT_PATH)
public final class MoviiController {

    private static final String TOKEN_UPLOAD_SUBSIDIARIES = "3KipPujcVUfHiwon7UvcyPwc2bOdJ4Ban4xqW9Dm";
    private static final String TOKEN_RISK_CHANGE_HASH_SUBSIDY = "xeLlESawdDxNg5SgKkaqgNVrPz8KrlJ0guUZjfg9";
    private final MoviiService moviiService;
    private final MoviiredService moviiredService;
    private final int ipAddress;
    private final SchedulersConfigurationProperties schedulersConfigurationProperties;
    private final ServiceActivationProperties serviceActivationProperties;
    private final SchedulerHelperService schedulerHelperService;
    private final SubsidyProperties subsidyProperties;

    public MoviiController(@NotNull MoviiService pMoviiService,
                           @NotNull MoviiredService pMoviiredService,
                           @NotNull SchedulersConfigurationProperties pSchedulersConfigurationProperties,
                           @NotNull ServiceActivationProperties pServiceActivationProperties,
                           @NotNull SchedulerHelperService pSchedulerHelperService,
                           @NotNull SubsidyProperties pSubsidyProperties) throws UnknownHostException {
        super();
        this.moviiService = pMoviiService;
        this.moviiredService = pMoviiredService;
        this.schedulersConfigurationProperties = pSchedulersConfigurationProperties;
        this.serviceActivationProperties = pServiceActivationProperties;
        this.schedulerHelperService = pSchedulerHelperService;
        this.subsidyProperties = pSubsidyProperties;

        // Get HASH of ip address of current host
        this.ipAddress = InetAddress.getLocalHost().hashCode();
    }

    @GetMapping(value = PING_YML_ROUTE)
    public Mono<ResponseEntity<Mono<String>>> ping() {
        return Mono.just(new ResponseEntity<>(moviiService.ping(), HttpStatus.OK));
    }

    @GetMapping(value = GET_STATUS_USER_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> validate(@PathVariable(PHONE_NUMBER) String phoneNumber, @PathVariable(PHONE_SERIAL_NUMBER) String phoneSerial) {
        return Mono.just(new ResponseEntity<>(moviiService.validateUserStatus(phoneNumber, phoneSerial, AdoProcess.REGISTRATION), HttpStatus.OK));
    }

    @GetMapping(value = GET_STATUS_USER_PROCESS_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> validateForProcess(@PathVariable(PHONE_NUMBER) String phoneNumber, @PathVariable(PHONE_SERIAL_NUMBER) String phoneSerial, @PathVariable(PROCESS_NUMBER) Integer processNumber) {
        return Mono.just(new ResponseEntity<>(moviiService.validateUserStatus(phoneNumber, phoneSerial, AdoProcess.getById(processNumber)), HttpStatus.OK));
    }

    @PostMapping(value = ADD_PENDING_USER_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> setPendingUser(@RequestBody RegisterDTO requestRegisterDTO) {
        return Mono.just(new ResponseEntity<>(moviiService.createPendingUser(requestRegisterDTO), HttpStatus.OK));
    }

    @PostMapping(value = CHANGE_FORM_COMPLETED_STATUS_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> changeStatusFormCompleted(@PathVariable(PHONE_NUMBER) String phoneNumber, @PathVariable(PHONE_SERIAL_NUMBER) String phoneSerial) {
        return Mono.just(new ResponseEntity<>(moviiService.changeStatusFormCompleted(phoneNumber, phoneSerial), HttpStatus.OK));
    }

    @PostMapping(value = INACTIVE_CASE_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> inactiveAdoCase(@PathVariable(PHONE_NUMBER) String phoneNumber, @PathVariable(PHONE_SERIAL_NUMBER) String phoneSerial) {
        return Mono.just(new ResponseEntity<>(moviiService.inactiveCase(phoneNumber, phoneSerial), HttpStatus.OK));
    }

    // Subsidy *********************************************************************************************************

    @GetMapping(value = VALIDATE_SUBSIDIZED_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> validateSubsidized(@PathVariable(DOCUMENT_NUMBER) String documentNumber,
                                                                      @PathVariable DocumentType documentType,
                                                                      @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader) {
        return Mono.just(new ResponseEntity<>(moviiService.validateSubsidized(authorizationHeader, documentType, documentNumber, null), HttpStatus.OK));
    }

    //@PostMapping(value = GET_SUBSIDIZED_DATA_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> getSubsidizedData(@PathVariable(DOCUMENT_NUMBER) String documentNumber,
                                                                     @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader,
                                                                     @RequestBody RegisterDTO registerDTO) {
        return Mono.just(new ResponseEntity<>(moviiService.getSubsidizedData(authorizationHeader, documentNumber,
                registerDTO.getSubsidizedCode(), moviiredService), HttpStatus.OK));
    }

    //@PostMapping(value = INACTIVATE_SUBSIDIZED_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> inactivateSubsidizedCase(@PathVariable(DOCUMENT_NUMBER) String documentNumber,
                                                                            @PathVariable DocumentType documentType) {
        return Mono.just(new ResponseEntity<>(moviiService.inactivateSubsidizedCase(documentType, documentNumber), HttpStatus.OK));
    }

    @PostMapping(value = CHANGE_SUBSIDIZED_HASH_YML_ROUTE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> changeSubsidizedHash(@RequestHeader String devToken, @RequestHeader String infraToken, @RequestBody RegisterDTO registerDTO) {
        if (Boolean.TRUE.equals(serviceActivationProperties.getChangeHashSubsidy()) && devToken.equals(MoviiController.TOKEN_RISK_CHANGE_HASH_SUBSIDY) && infraToken.equals(subsidyProperties.getInfraTokenChangeHashSubsidy())) {
            return Mono.just(new ResponseEntity<>(moviiService.changeSubsidizedHash(registerDTO), HttpStatus.OK));
        } else {
            return Mono.just(new ResponseEntity<>(Mono.just(new RegisterDTO()), HttpStatus.OK));
        }
    }

    @PostMapping(value = UPLOAD_SUBSIDIZED_DOCUMENTS_YML_ROUTE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Mono<RegisterDTO>>> uploadSubsidizedCases(@RequestPart(FILE) FilePart filePart, @RequestPart String token, @PathVariable DocumentType documentType) {
        if (Boolean.TRUE.equals(serviceActivationProperties.getUploadSubsidizedDocuments()) && token.equals(MoviiController.TOKEN_UPLOAD_SUBSIDIARIES)) {
            return Mono.just(new ResponseEntity<>(moviiService.uploadSubsidedCases(documentType, filePart), HttpStatus.OK));
        } else {
            return Mono.just(new ResponseEntity<>(Mono.just(new RegisterDTO()), HttpStatus.OK));
        }
    }

    //@Scheduled(fixedRateString = JOB_SCHEDULED_TIME_RATE)
    public void validateStatusADO() {
        // Verify turn of execution
        if (!Boolean.TRUE.equals(schedulersConfigurationProperties.getValidateStatusAdoEnabled()) || UtilsHelper.validateShift(ipAddress)) {
            return;
        }

        // Execute el JOB
        this.moviiService.validateStatusADO();
    }

    //@Scheduled(fixedRateString = JOB_SCHEDULED_SUBSIDY_TIME_RATE, initialDelay = 1000)
    public void updateSubsidizedData() {
        // Execute el JOB
        if (Boolean.TRUE.equals(schedulersConfigurationProperties.getUpdateInfoPersonSubsidizedEnabled()) && !moviiService.isJobUpdateSubsidized()) {
            String correlative = UtilsHelper.asignarCorrelativo(null);
            schedulerHelperService.schedulerController(correlative, PROCESS_UPDATE_DATA_SUBSIDIZED, false)
                    .flatMap(execute -> {
                        if (Boolean.TRUE.equals(execute)) {
                            this.moviiService.updateSubsidizedInformation(moviiredService, correlative);
                        }
                        return Mono.just(true);
                    }).subscribe();
        }
    }
}

