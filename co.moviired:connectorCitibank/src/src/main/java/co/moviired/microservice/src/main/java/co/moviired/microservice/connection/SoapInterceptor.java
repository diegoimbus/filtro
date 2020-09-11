package co.moviired.microservice.connection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Slf4j
public class SoapInterceptor implements ClientInterceptor {

    @Override
    @SneakyThrows
    public boolean handleRequest(MessageContext messageContext) {
        OutputStream out = new ByteArrayOutputStream();
        messageContext.getRequest().writeTo(out);
        log.info("PETICION PARA PROVEEDOR: " + out.toString());
        out.flush();
        out.close();
        return true;
    }

    @Override
    @SneakyThrows
    public boolean handleResponse(MessageContext messageContext) {
        OutputStream out = new ByteArrayOutputStream();
        messageContext.getResponse().writeTo(out);
        log.info("RESPUESTA DEL PROVEEDOR: " + out.toString());
        out.flush();
        out.close();
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        return false;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) {
        // Do nothing because isn't necessary
    }

}
