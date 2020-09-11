package co.moviired.mahindrafacade.helper;


import co.moviired.base.helper.CommandHelper;
import co.moviired.base.util.Generator;
import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.client.mahindra.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;

@Slf4j
@Component
public final class UtilsHelper {

    private static JAXBContext contextReq;
    private static JAXBContext contextRes;

    static {
        try {
            contextReq = JAXBContext.newInstance(Request.class);
            contextRes = JAXBContext.newInstance(Response.class);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    private UtilsHelper() {
        super();
    }

    public static String assignCorrelative(String pcorrelation) {
        String correlation = pcorrelation;
        if (correlation == null || correlation.isEmpty()) {
            correlation = String.valueOf(Generator.correlationId());
        }
        MDC.putCloseable("correlation-id", correlation);
        return correlation;
    }

    public static String parseToXml(@NotNull Request request) {
        String xmlRequest = "";
        try {
            StringWriter sw = new StringWriter();
            Marshaller requestMarshaller = contextReq.createMarshaller();
            requestMarshaller.marshal(request, sw);
            xmlRequest = sw.toString();
            sw.close();

        } catch (JAXBException | IOException e) {
            log.error(e.getMessage());
        }

        return xmlRequest;
    }

    public static String parseToXml(@NotNull Response response) {
        String xmlResponse;
        try {
            StringWriter sw = new StringWriter();
            Marshaller responseMarshaller = contextRes.createMarshaller();
            responseMarshaller.marshal(response, sw);
            xmlResponse = sw.toString();
            sw.close();

        } catch (JAXBException | IOException e) {
            log.error(e.getMessage());
            xmlResponse = "";
        }
        log.info("Response :{}", CommandHelper.printIgnore(xmlResponse, ConstantsHelper.MPIN, ConstantsHelper.PIN, ConstantsHelper.PASSCODE));
        return xmlResponse;
    }
}

