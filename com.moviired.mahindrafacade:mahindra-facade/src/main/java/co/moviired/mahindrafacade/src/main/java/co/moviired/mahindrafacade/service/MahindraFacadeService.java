package co.moviired.mahindrafacade.service;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.helper.CommandHelper;
import co.moviired.mahindrafacade.client.mahindra.MahindraConnector;
import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.client.mahindra.Response;
import co.moviired.mahindrafacade.domain.entity.mahindrafacade.User;
import co.moviired.mahindrafacade.domain.enums.OperationType;
import co.moviired.mahindrafacade.domain.provider.IParser;
import co.moviired.mahindrafacade.domain.provider.IProccesor;
import co.moviired.mahindrafacade.domain.provider.ParserFactory;
import co.moviired.mahindrafacade.domain.provider.ProccesorFactory;
import co.moviired.mahindrafacade.domain.repository.mahindrafacade.IUserRepository;
import co.moviired.mahindrafacade.helper.ConstantsHelper;
import co.moviired.mahindrafacade.helper.UtilsHelper;
import co.moviired.mahindrafacade.properties.CommandProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static co.moviired.mahindrafacade.helper.ConstantsHelper.TRANSACTION_401;

@Slf4j
@Service
@AllArgsConstructor
public final class MahindraFacadeService {

    private final CommandProperties commandProperties;
    private final ParserFactory parserFactory;
    private final ProccesorFactory proccesorFactory;
    private final MahindraConnector mahindraConnectorClient;
    private final IUserRepository userRepository;


    public Mono<String> processMHFacade(Mono<String> pRequest) {
        // Start medición de tiempo
        StopWatch watch = new StopWatch();
        watch.start();

        // Procesar la petición
        AtomicReference<String> correlationId = new AtomicReference<>();
        return pRequest.flatMap(requestMh -> {

            correlationId.set(UtilsHelper.assignCorrelative(""));

            log.info("************** Iniciando el servicio processMHFacade **************");
            log.info("Request :{}", CommandHelper.printIgnore(requestMh, ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));

            try {
                // Obtener valor de type request MH
                String optType = commandProperties.getCommands().stream().filter(requestMh::contains).findFirst().orElse("");

                // Hacer un "passthroug" a Mahindra
                if (optType.isEmpty()) {
                    return mahindraConnectorClient.sendMahindraRequest(requestMh, correlationId.get());
                }

                // Transformar la petición recibida
                OperationType operation = OperationType.valueOf(optType);
                IParser parser = parserFactory.getParser(operation);
                OperationType typeResponse = OperationType.valueOf(commandProperties.getResponse().get(operation.toString()));

                Request request = parser.getRequest(requestMh);

                // Procesar la petición
                IProccesor provider = proccesorFactory.getProccesor(operation);
                return provider.proccess(request, typeResponse, correlationId.get());

            } catch (ParsingException | JsonProcessingException | ProcessingException e) {
                return Mono.error(e);
            }

        }).onErrorResume(e -> Mono.just(responseError(e))
        ).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio processMHFacade **************");
        });
    }

    public Mono<String> validateUserService(Mono<String> pRequest) {

        // Start medición de tiempo
        StopWatch watch = new StopWatch();
        watch.start();

        AtomicReference<String> correlationId = new AtomicReference<>();

        return pRequest.flatMap(requestMh -> {

            correlationId.set(UtilsHelper.assignCorrelative(""));

            log.info("************** Iniciando el servicio validateUserMHFacade **************");
            log.info("Request :{}", CommandHelper.printIgnore(requestMh, ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));

            try {

                IParser parser = parserFactory.getParser(OperationType.AUTHPINRESP);
                AtomicReference<Response> responseMh = new AtomicReference<>(parser.getResponse(requestMh));

                return sendMahindraInfo(responseMh.get(), correlationId.get())
                        .flatMap(responseMhInfo -> {

                            //Guardar campos adicionales de command "USRQRYINFO"
                            responseMh.get().setCity(responseMhInfo.getCity());
                            responseMh.get().setStatus(responseMhInfo.getStatus());
                            responseMh.get().setBarredtype(responseMhInfo.getBarredtype());
                            responseMh.get().setPreflanguage(responseMhInfo.getPreflanguage());

                            return Mono.just(responseMh);

                        })
                        .flatMap(responseInfo -> userRepository.findByMsisdn(responseMh.get().getMsisdn())
                                .flatMap(userMongo -> {

                                    correlationId.set(UtilsHelper.assignCorrelative(correlationId.get()));

                                    User user = User.parse(responseMh.get());
                                    user.setId(userMongo.getId());
                                    userRepository.save(user).subscribe();

                                    log.info("SI existe registro, usuario actualizado con id: {}", userMongo.getId());

                                    return Mono.just(UtilsHelper.parseToXml(responseMh.get()));

                                }).switchIfEmpty( Mono.defer(() -> userRepository.save(User.parse(responseMh.get())).flatMap(userSave -> {

                                    correlationId.set(UtilsHelper.assignCorrelative(correlationId.get()));
                                    log.info("NO existe registro, usuario guardado con id: {}", userSave.getId());

                                    Response response = Response.parse(userSave);

                                    return Mono.just(UtilsHelper.parseToXml(response));

                                })))
                        );

            } catch (ParsingException e) {
               log.error("Error:{}",e.getMessage());
               return Mono.error(e);
            }

        }).onErrorResume(e -> Mono.just(responseError(e))
        ).doOnTerminate(() -> {
            watch.stop();
            log.info("Tiempo de ejecución: {} millis", watch.getTime());
            log.info("************** Finalizando el servicio validateUserMHFacade **************");
        });

    }


    //*********************************** Metodos de ayuda ***********************************//

    private String responseError(Throwable e) {
        log.info("Error: {}", e.getMessage());
        Response response = new Response();
        response.setTxnstatusmf(TRANSACTION_401);
        response.setMessagemf(e.getMessage());

        return UtilsHelper.parseToXml(response);
    }

    private Mono<Response> sendMahindraInfo(Response responseMh, String correlationId){

        Request requestInfo = new Request();
        requestInfo.setType(OperationType.USRQRYINFO.name());
        requestInfo.setMsisdn(responseMh.getMsisdn());
        requestInfo.setProvider(responseMh.getProvider());
        requestInfo.setUsertype(responseMh.getUsertype());

        return mahindraConnectorClient.sendMahindraRequest(requestInfo, correlationId);
    }

}

