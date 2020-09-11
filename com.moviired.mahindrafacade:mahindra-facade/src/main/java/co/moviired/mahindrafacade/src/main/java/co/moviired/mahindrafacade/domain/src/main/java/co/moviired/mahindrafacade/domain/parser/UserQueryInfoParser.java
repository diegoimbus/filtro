package co.moviired.mahindrafacade.domain.parser;

import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.domain.provider.IParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Slf4j
@Service
public final class UserQueryInfoParser implements IParser {

    private final transient Unmarshaller unmarshallerRequest;

    public UserQueryInfoParser() throws JAXBException {
        super();
        JAXBContext contextReq = JAXBContext.newInstance(Request.class);
        this.unmarshallerRequest = contextReq.createUnmarshaller();
    }

    @Override
    public Request getRequest(String requestMh) {
        return getXmlToRequest(requestMh);
    }

    private Request getXmlToRequest(String xmlString) {
        try {
            return (Request) this.unmarshallerRequest.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            log.error("getXmlToRequest:{}", e.getMessage());
        }

        return null;
    }

}

