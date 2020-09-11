package co.moviired.support.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.domain.dto.Request;
import co.moviired.support.domain.dto.Response;
import co.moviired.support.domain.entity.Module;
import co.moviired.support.domain.entity.Operation;
import co.moviired.support.domain.entity.Profile;
import co.moviired.support.domain.enums.GeneralStatus;
import co.moviired.support.domain.enums.OperationType;
import co.moviired.support.domain.repository.IModuleRepository;
import co.moviired.support.domain.repository.IOperationRepository;
import co.moviired.support.domain.repository.IProfileRepository;
import co.moviired.support.helper.UtilHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class SupportProfilesService {

    private final IOperationRepository operationRepository;
    private final PushService pushAuditService;
    private final IModuleRepository moduleRepository;
    private final IProfileRepository profileRepository;
    private final GlobalProperties globalProperties;
    private static final String ERROR = "ERROR";
    private static final String OK = "OK";
    private static final String ID_NUMBER = "El id debe ser numerico";
    private static final String COD_ERROR = "99";
    private static final String SRVPRFNAME = "serviceProfileRName";
    private static final String COD_OK = "00";
    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String REQUEST = "REQUEST ";
    private static final String RESPONSE = "RESPONSE";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS SUPPORT PROFILES";

    public SupportProfilesService(IOperationRepository poperationRepository,
                                  PushService ppushAuditService,
                                  IModuleRepository pmoduleRepository,
                                  IProfileRepository pprofileRepository,
                                  GlobalProperties pglobalProperties) {
        super();
        this.operationRepository = poperationRepository;
        this.pushAuditService = ppushAuditService;
        this.moduleRepository = pmoduleRepository;
        this.profileRepository = pprofileRepository;
        this.globalProperties = pglobalProperties;
    }

    public final Mono<Response> serviceFindOperationByStatus(@NotNull Mono<Boolean> brequest, String correlationId) {
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "serviceFindOperationByStatus");

        return brequest.flatMap(request -> {
            try {

                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "status = " + new ObjectMapper().writeValueAsString(request));
                int status = 0;
                if (request.booleanValue()) {
                    status = 1;
                }

                GeneralStatus valor = GeneralStatus.ENABLED;
                if (status == GeneralStatus.DISABLED.getOrdinal()) {
                    valor = GeneralStatus.DISABLED;
                }
                List<Operation> topersations = operationRepository.findByStatus(valor);

                Response response = generateErrorResponse(COD_OK, OK, OK);
                response.setOperations(topersations);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, "OK");

                return Mono.just(response);
            } catch (NumberFormatException e) {
                return Mono.error(e);
            } catch (JsonProcessingException je) {
                return Mono.error(je);
            }

        }).onErrorResume(e -> {

            Response response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "serviceFindOperationByStatus"));
    }

    public final Mono<Response> getModuleAll(String correlationId) {
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "getModuleAll");

        return Mono.just(new Request()).flatMap(request -> {
            try {

                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "All");
                List<Module> modules = (List<Module>) moduleRepository.findAll();
                modules.forEach(module -> module.setOperations(operationRepository.findByModulesAndStatusIsNot(module, GeneralStatus.HIDDEN)));
                Response response = generateErrorResponse(COD_OK, OK, OK);
                response.setModules(modules);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, OK);

                return Mono.just(response);
            } catch (NumberFormatException e) {
                return Mono.error(e);
            }

        }).onErrorResume(e -> {

            Response response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "getModuleAll"));
    }

    public final Mono<Response> getProfileAll(String correlationId) {
        this.asignarCorrelativo(correlationId);

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "getProfileAll");

        return Mono.just(new Request()).flatMap(request -> {
            try {

                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "All");
                List<Profile> profiles = (List<Profile>) profileRepository.findAll();
                profiles.forEach(profile -> profile.setOperations(operationRepository.findByProfiles(profile)));
                Response response = generateErrorResponse(COD_OK, OK, OK);
                response.setProfiles(profiles);

                return Mono.just(response);
            } catch (NumberFormatException e) {
                return Mono.error(e);
            }

        }).onErrorResume(e -> {

            Response response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "getProfileAll"));
    }

    public final Mono<Response> serviceFindProfileByStatus(@NotNull Mono<Boolean> brequest, String correlationId) {

        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "serviceFindProfileByStatus");

        return brequest.flatMap(request -> {
            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "status = " + new ObjectMapper().writeValueAsString(request));
                int status = 0;
                if (request.booleanValue()) {
                    status = 1;
                }

                GeneralStatus valor = GeneralStatus.ENABLED;
                if (status == GeneralStatus.DISABLED.getOrdinal()) {
                    valor = GeneralStatus.DISABLED;
                }

                List<Profile> toprofile = profileRepository.findByStatus(valor);

                if (!toprofile.isEmpty()) {
                    for (Profile p : toprofile) {
                        List<Operation> operations = operationRepository.findByProfiles(p);
                        p.setOperations(operations);
                    }
                }

                Response response = generateErrorResponse(COD_OK, OK, OK);
                response.setProfiles(toprofile);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, OK);

                return Mono.just(response);
            } catch (NumberFormatException e) {
                return Mono.error(e);
            } catch (JsonProcessingException je) {
                return Mono.error(je);
            }
        }).onErrorResume(e -> {
            if (e instanceof NumberFormatException) {
                Response response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), "La variable statusProfile debe ser numerica");
                return Mono.just(response);
            }
            Response response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "serviceFindProfileByStatus"));
    }

    public final Mono<Response> serviceProfileRD(@NotNull Mono<String> idProfile,
                                                 String correlationId,
                                                 OperationType operation,
                                                 String authorization) {

        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "serviceProfileRD");

        return idProfile.flatMap(request -> {
            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "idProfile = " + request);
                Profile profile = null;
                int id = Integer.parseInt(request);
                Optional<Profile> toprofile = profileRepository.findById(id);

                if (!toprofile.isPresent()) {
                    Response response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "No existe un perfil con el idprofile recibido");
                    return Mono.just(response);
                }
                profile = toprofile.get();

                Mono<Response> response = ejecutarOperacionProfile(profile, null, operation, correlationId, authorization);

                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, "OK");
                return response;

            } catch (NumberFormatException e) {
                return Mono.error(e);
            }
        }).onErrorResume(e -> {
            Response response = null;

            if (e instanceof NumberFormatException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), ID_NUMBER);
            } else if (e instanceof DataException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), e.getMessage());
            } else {
                response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            }
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);
        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "serviceProfileRD"));
    }

    public final Mono<Response> serviceProfileRName(@NotNull Mono<String> name,
                                                    String correlationId) {
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, SRVPRFNAME);

        return name.flatMap(request -> {
            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "name = " + request);

                Profile toprofile = profileRepository.findByProfileName(request);

                if (toprofile == null) {
                    Response response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "No existe un perfil con el name recibido");
                    return Mono.just(response);
                }

                Response response = generateErrorResponse(COD_OK, OK, OK);
                response.setProfile(toprofile);
                List<Operation> operations = operationRepository.findByProfiles(toprofile);
                toprofile.setOperations(operations);
                response.setErrorMessage(OK);
                response.setProfile(toprofile);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, "OK");
                return Mono.just(response);

            } catch (NumberFormatException e) {
                return Mono.error(e);
            }
        }).onErrorResume(e -> {

            Response response = null;
            if (e instanceof NumberFormatException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), ID_NUMBER);
                return Mono.just(response);
            } else if (e instanceof DataException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), e.getMessage());
                return Mono.just(response);
            } else {
                response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            }
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);
        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, SRVPRFNAME));
    }


    public final Mono<Response> findAuthorities(@NotNull Mono<String> name,
                                                String correlationId) {
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, SRVPRFNAME);

        return name.flatMap(request -> {
            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "name = " + request);

                Profile toprofile = profileRepository.findByProfileName(request);

                if (toprofile == null) {
                    Response response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "No existe un perfil con el name recibido");
                    return Mono.just(response);
                }

                Response response = generateErrorResponse(COD_OK, OK, OK);
                List<Operation> operations = operationRepository.findByProfiles(toprofile);
                response.setAuthorities(new ArrayList<>());
                operations.forEach(operation -> response.getAuthorities().add(operation.getOperationName()));
                response.setErrorMessage(OK);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, "OK");
                return Mono.just(response);

            } catch (NumberFormatException e) {
                return Mono.error(e);
            }
        }).onErrorResume(e -> {

            Response response = null;
            if (e instanceof NumberFormatException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), ID_NUMBER);
                return Mono.just(response);
            } else if (e instanceof DataException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), e.getMessage());
                return Mono.just(response);
            } else {
                response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            }
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);
        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, SRVPRFNAME));
    }


    public final Mono<Response> serviceProfileCU(@NotNull @RequestBody Mono<Request> brequest, String correlationId,
                                                 OperationType operation,
                                                 String authorization) {
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "serviceProfileCU");

        return brequest.flatMap(request -> {

            try {
                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, request);
                validationInputProfile(request, operation);

                Mono<Response> response = ejecutarOperacionProfile(null, request, operation, correlationId, authorization);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response.block()));
                return response;
            } catch (ServiceException e) {
                return Mono.error(e);
            } catch (JsonProcessingException je) {
                return Mono.error(je);
            }


        }).onErrorResume(e -> {

            Response response = null;
            if (e instanceof DataException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), e.getMessage());
            } else {
                response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            }

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "serviceProfileCU"));
    }

    public final Mono<Response> serviceOperationR(@NotNull Mono<String> idOperation, String correlationId,
                                                  OperationType operationType) {
        this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "serviceOperationR");

        return idOperation.flatMap(request -> {
            try {

                log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST, "idOperation = " + request);
                Operation operation = null;
                int id = Integer.parseInt(request);
                Optional<Operation> toperation = operationRepository.findById(id);

                if (!toperation.isPresent()) {
                    Response response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "No existe una operaciòn con el idOperation recibido");
                    return Mono.just(response);
                }
                operation = toperation.get();

                Mono<Response> response = ejecutarOperacionOperation(operation, operationType);
                log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, new ObjectMapper().writeValueAsString(response.block()));
                return response;

            } catch (NumberFormatException e) {
                return Mono.error(e);
            } catch (JsonProcessingException je) {
                return Mono.error(je);
            }
        }).onErrorResume(e -> {

            Response response = null;
            if (e instanceof NumberFormatException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), ID_NUMBER);
                return Mono.just(response);
            } else if (e instanceof DataException) {
                response = generateErrorResponse(COD_ERROR, ErrorType.DATA.name(), e.getMessage());
                return Mono.just(response);
            } else {
                response = generateErrorResponse(COD_ERROR, ERROR, e.getMessage());
            }

            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE, response);
            return Mono.just(response);

        }).doAfterTerminate(() -> log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "serviceOperationR"));
    }


    private Request validationInputProfile(Request request, OperationType operation) throws DataException {

        if (request.getProfile() == null) {
            throw new DataException("-2", "profile es un parámetro obligatorio");
        }

        if (request.getOperationsId() == null || request.getOperationsId().isEmpty()) {
            throw new DataException("-2", "operationsId debe contener minimo un id de operación");
        }

        if (request.getProfile().getProfileName() == null) {
            throw new DataException("-2", "profileName es un parametro obligatorio");
        }

        if (request.getProfile().getProfileDescription() == null) {
            throw new DataException("-2", "profileDescription es un parametro obligatorio");
        }

        if (request.getProfile().getOrigin() == null) {
            throw new DataException("-2", "origin es un parametro obligatorio");
        }

        if (OperationType.INSERT.equals(operation)) {
            Profile profile = profileRepository.findByProfileName(request.getProfile().getProfileName());
            if (profile != null) {
                throw new DataException("-2", "Ya existe un perfil con el nombre: " + request.getProfile().getProfileName());
            }
        }

        if (OperationType.UPDATE.equals(operation) && request.getProfile().getId() == null) {
            throw new DataException("-2", "id es un parametro obligatorio");
        }

        return request;
    }


    private void notifyChangeProfiles() {
        ExecutorService taskReverse = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        taskReverse.submit(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                log.info("notifica cambios en el perfil --> " + globalProperties.getUrlNotifyChangesProfiles());
                ResponseEntity<String> response =
                        restTemplate.getForEntity(globalProperties.getUrlNotifyChangesProfiles(), String.class);

                if (response != null) {
                    log.info("Response notificar changes profiles --> " + response.getBody());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        });
    }


    private Mono<Response> ejecutarOperacionProfile(Profile profile, Request request, OperationType operation, String correlationId, String authorization) {
        Response response = new Response();
        try {

            switch (operation) {
                case SELECT:
                    List<Operation> operations = operationRepository.findByProfiles(profile);
                    profile.setOperations(operations);
                    response.setErrorMessage(OK);
                    response.setProfile(profile);
                    break;

                case DELETE:
                    if (GeneralStatus.DISABLED == profile.getEnableDelete()) {
                        response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "Este perfil no se puede eliminar");
                        return Mono.just(response);
                    }
                    profile.setStatus(GeneralStatus.DISABLED);
                    profileRepository.save(profile);
                    response.setErrorMessage("Operación exitosa: Profile eliminado");
                    notifyChangeProfiles();
                    break;

                case INSERT:
                    Profile toprofile = new Profile();
                    toprofile.setProfileName(request.getProfile().getProfileName());
                    toprofile.setProfileDescription(request.getProfile().getProfileDescription());
                    toprofile.setEnableDelete(request.getProfile().getEnableDelete());
                    toprofile.setStatus(request.getProfile().getStatus());
                    toprofile.setOrigin(request.getProfile().getOrigin());
                    toprofile.setCreatedUser(request.getProfile().getCreatedUser());

                    toprofile.setOperations(getOperations(request.getOperationsId()));
                    profileRepository.save(toprofile);
                    // AUDITORIA
                    this.pushAuditService.pushAudit(
                            this.pushAuditService.generarAudit(
                                    authorization.split(":")[0],
                                    "PROFILE_CREATE",
                                    correlationId,
                                    "Ha creado el profile " + toprofile.getProfileName(),
                                    this.generateDetailOperation(toprofile))
                    );
                    response.setErrorMessage(OK);
                    notifyChangeProfiles();
                    break;
                case UPDATE:
                    Profile toprofileU;
                    Map<String, String> details;

                    Optional<Profile> prof = profileRepository.findById(request.getProfile().getId());
                    if (!prof.isPresent()) {
                        response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), "No se encontro este perfil");
                        return Mono.just(response);
                    }
                    toprofileU = prof.get();
                    details = this.generateDetailOperationUpdate(toprofileU, request.getProfile(), request.getOperationsId());

                    toprofileU.setProfileName(request.getProfile().getProfileName());
                    toprofileU.setProfileDescription(request.getProfile().getProfileDescription());
                    toprofileU.setEnableDelete(request.getProfile().getEnableDelete());
                    toprofileU.setStatus(request.getProfile().getStatus());
                    toprofileU.setOrigin(request.getProfile().getOrigin());
                    toprofileU.setCreatedUser(request.getProfile().getCreatedUser());

                    toprofileU.setOperations(getOperations(request.getOperationsId()));
                    profileRepository.save(toprofileU);
                    this.pushAuditService.pushAudit(
                            this.pushAuditService.generarAudit(
                                    authorization.split(":")[0],
                                    "PROFILE_MODIFY",
                                    correlationId,
                                    "Ha modificado el profile " + toprofileU.getProfileName(),
                                    details)
                    );
                    response.setErrorMessage(OK);
                    notifyChangeProfiles();
                    break;
            }
            response.setErrorCode(COD_OK);
            response.setErrorType(OK);
            return Mono.just(response);

        } catch (Exception ex) {
            response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), ex.getMessage());
        }
        return Mono.just(response);
    }

    private List<Operation> getOperations(List<Integer> operationsIdU) {
        List<Operation> listOperationsU = new ArrayList<>();
        for (Integer idOperation : operationsIdU) {
            Optional<Operation> toperation = operationRepository.findById(idOperation);
            if (toperation.isPresent()) {
                listOperationsU.add(toperation.get());
            }
        }
        return listOperationsU;
    }

    private Mono<Response> ejecutarOperacionOperation(Operation operation, OperationType operationType) {
        Response response = new Response();
        try {
            if (operationType.equals(OperationType.SELECT)) {
                response.setErrorMessage(OK);
                response.setOperation(operation);
            }
            response.setErrorCode(COD_OK);
            response.setErrorType(OK);

        } catch (Exception ex) {
            response = generateErrorResponse(COD_ERROR, ErrorType.PROCESSING.name(), ex.getMessage());
        }
        return Mono.just(response);
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(String errorCode, String errorType, String errorMessage) {
        Response response = new Response();
        response.setErrorType(errorType);
        response.setErrorMessage(errorMessage);
        response.setErrorCode(errorCode);
        return response;
    }

    private void asignarCorrelativo(String corre) {
        String correlativo = corre;
        if ((correlativo == null) || (correlativo.isEmpty())) {
            correlativo = UtilHelper.generateCorrelationId();
        }
        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
    }


    private Map<String, String> generateDetailOperation(Profile profile) {
        Map<String, String> detailOperation = new LinkedHashMap<>();
        detailOperation.put("nombre", profile.getProfileName());
        detailOperation.put("descripcion", profile.getProfileDescription());
        detailOperation.put("es posible eliminarlo?", (profile.getEnableDelete() == GeneralStatus.ENABLED) ? "SI" : "NO");
        detailOperation.put("Fuente", profile.getOrigin().name());
        detailOperation.put("estado", (profile.getStatus() == GeneralStatus.ENABLED) ? "Activo" : "Inactivo");
        ArrayList<String> operations = new ArrayList<>();
        profile.getOperations().forEach(operation -> operations.add(operation.getOperationDescription()));
        detailOperation.put("Operaciones ", operations.toString());

        return detailOperation;
    }

    private Map<String, String> generateDetailOperationUpdate(Profile profileOld, Profile profileNew, List<Integer> ids) {
        Map<String, String> detailOperation = new LinkedHashMap<>();
        ArrayList<String> operationOld = new ArrayList<>();
        ArrayList<String> operationNew = new ArrayList<>();

        if ((profileOld.getProfileName() != null) && !profileOld.getProfileName().equalsIgnoreCase(profileNew.getProfileName())) {
            detailOperation.put("nombre anterior", profileOld.getProfileName());
            detailOperation.put("nombre nuevo", profileNew.getProfileName());
        }
        if ((profileOld.getProfileDescription() != null) && !profileOld.getProfileDescription().equalsIgnoreCase(profileNew.getProfileDescription())) {
            detailOperation.put("descripción anterior", profileOld.getProfileDescription());
            detailOperation.put("descripción nuevo", profileNew.getProfileDescription());
        }
        if ((profileOld.getOrigin() != null) && profileOld.getOrigin() != profileNew.getOrigin()) {
            detailOperation.put("fuente anterior", profileOld.getOrigin().name());
            detailOperation.put("fuente nuevo", profileNew.getOrigin().name());
        }
        if ((profileOld.getStatus() != null) && profileOld.getStatus() != profileNew.getStatus()) {
            detailOperation.put("estado anterior", profileOld.getStatus().name());
            detailOperation.put("estado nuevo", profileNew.getStatus().name());
        }
        if ((profileOld.getEnableDelete() != null) && profileOld.getEnableDelete() != profileNew.getEnableDelete()) {
            detailOperation.put("posible eliminar anterior", profileOld.getEnableDelete().name());
            detailOperation.put("posible eliminar nuevo", profileNew.getEnableDelete().name());
        }

        ArrayList<Operation> operationsOld = (ArrayList<Operation>) this.operationRepository.findByProfiles(profileOld);

        operationsOld.forEach(operation -> operationOld.add(operation.getOperationDescription()));
        ids.forEach(id -> operationNew.add(this.operationRepository.findById(id).get().getOperationDescription()));

        if ((profileOld.getOperations() != null) && this.diffOperations(operationOld, operationNew)) {
            detailOperation.put("operaciones anteriores", operationOld.toString());
            detailOperation.put("operaciones nuevas", operationNew.toString());
        }


        return detailOperation;
    }


    private boolean diffOperations(List<String> operationOld, List<String> operationNew) {
        ArrayList<String> nuevo = new ArrayList<>();

        if (operationOld.size() != operationNew.size()) {
            return true;
        }
        for (String o : operationNew) {
            nuevo.add(o);
        }
        nuevo.removeAll(operationOld);
        return !nuevo.isEmpty();
    }
}

