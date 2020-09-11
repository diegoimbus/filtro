package co.moviired.mahindrafacade.client.mahindra;

import co.moviired.base.helper.CommandHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.mahindrafacade.helper.ConstantsHelper;
import co.moviired.mahindrafacade.helper.UtilsHelper;
import co.moviired.mahindrafacade.properties.StatusCodeConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Serializable;
import java.io.StringReader;

@Slf4j
@Data
@Service
@AllArgsConstructor
public final class MahindraConnector implements Serializable {

    private static JAXBContext contextRes;
    private final StatusCodeConfig statusCodeConfig;
    private final ReactiveConnector mahindraClient;

    static {
        try {
            contextRes = JAXBContext.newInstance(Response.class);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    public Mono<Response> sendMahindraRequest(@NotNull Request request, String correlationId) {
        // Start medición de tiempo
        StopWatch watch = new StopWatch();
        watch.start();

        String xml = UtilsHelper.parseToXml(request);
        log.info("Request ws CORE mahindra :{}", CommandHelper.printIgnore(xml, ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));

        return mahindraClient.post(xml, String.class, MediaType.APPLICATION_XML, null)
                .flatMap(responseMahindra -> {
                    UtilsHelper.assignCorrelative(correlationId);
                    log.info("Response ws CORE mahindra :{}", CommandHelper.printIgnore(responseMahindra.toString(), ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));
                    return Mono.just(processResponse(responseMahindra.toString()));
                })
                .onErrorResume(Mono::error)
                .doOnTerminate(() -> {
                    watch.stop();
                    log.info("Tiempo de ejecución commandMH(Request): {} millis", watch.getTime());
                });
    }

    public Mono<String> sendMahindraRequest(@NotNull String xml, String correlationId) {
        // Start medición de tiempo
        StopWatch watch = new StopWatch();
        watch.start();

        log.info("Request ws CORE mahindra :{}", CommandHelper.printIgnore(xml, ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));

        return mahindraClient.post(xml, String.class, MediaType.APPLICATION_XML, null)
                .flatMap(responseMahindra -> {
                    UtilsHelper.assignCorrelative(correlationId);
                    log.info("Response ws CORE mahindra :{}", CommandHelper.printIgnore(responseMahindra.toString(), ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));
                    return Mono.just(responseMahindra.toString());
                })
                .onErrorResume(Mono::error)
                .doOnTerminate(() -> {
                    watch.stop();
                    log.info("Tiempo de ejecución commandMH(String): {} millis", watch.getTime());
                });
    }

    private Response processResponse(@NotNull String xmlString) {
        try {
            Unmarshaller m = contextRes.createUnmarshaller();
            return (Response) m.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            log.error(e.getMessage());
        }

        return null;
    }

}

