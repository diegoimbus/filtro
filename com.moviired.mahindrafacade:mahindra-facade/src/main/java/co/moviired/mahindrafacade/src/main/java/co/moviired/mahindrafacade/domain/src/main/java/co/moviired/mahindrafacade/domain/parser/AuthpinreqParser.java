package co.moviired.mahindrafacade.domain.parser;

import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.client.mahindra.Response;
import co.moviired.mahindrafacade.domain.provider.IParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Slf4j
@Service
public final class AuthpinreqParser implements IParser {

    private static JAXBContext contextReq;
    private static JAXBContext contextResp;

    static {
        try {
            contextReq = JAXBContext.newInstance(Request.class);
            contextResp = JAXBContext.newInstance(Response.class);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    @Override
    public Request getRequest(String requestMh) {
        return getXmlToRequest(requestMh);
    }

    @Override
    public Response getResponse(@NotNull String responseMh) {
        return getXmlToResponse(responseMh);
    }

    private Request getXmlToRequest(String xmlString) {
        try {

            Unmarshaller unmarshallerRequest = contextReq.createUnmarshaller();
            return (Request) unmarshallerRequest.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            log.error("getXmlToRequest:{}", e.getMessage());
        }
        return null;
    }

    private Response getXmlToResponse(String xmlString) {
        try {
            Unmarshaller unmarshallerResponse = contextResp.createUnmarshaller();
            return (Response) unmarshallerResponse.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            log.error("getXmlToResponse:{}", e.getMessage());
        }
        return null;
    }

}

