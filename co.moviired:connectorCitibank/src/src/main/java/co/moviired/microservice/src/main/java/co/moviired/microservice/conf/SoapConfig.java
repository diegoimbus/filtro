package co.moviired.microservice.conf;

import co.moviired.microservice.connection.SoapInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
@AllArgsConstructor
public class SoapConfig {

    private final BankProperties bankProperties;

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        return connectionClient();
    }

    private WebServiceTemplate connectionClient() {
        WebServiceTemplate wsTemplate = new WebServiceTemplate();
        Jaxb2Marshaller marshaller = getJaxb2Marshaller();
        wsTemplate.setMessageSender(getMessageSender());
        wsTemplate.setMarshaller(marshaller);
        wsTemplate.setUnmarshaller(marshaller);
        wsTemplate.setInterceptors(getClientInterceptor());
        return wsTemplate;
    }

    public Jaxb2Marshaller getJaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("co.moviired.microservice.soap");
        return marshaller;
    }

    private HttpComponentsMessageSender getMessageSender() {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setConnectionTimeout(bankProperties.getConnectiontimeout());
        messageSender.setReadTimeout(bankProperties.getReadTimeout());
        return messageSender;
    }

    private ClientInterceptor[] getClientInterceptor() {
        return new ClientInterceptor[]{new SoapInterceptor()};
    }

}

