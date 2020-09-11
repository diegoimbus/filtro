package co.moviired.microservice.conf.soap;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class SoapHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        SOAPMessage msg = context.getMessage();
        boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty) {
            this.getMessageXML(msg, "Request:");
        } else {
            this.getMessageXML(msg, "Response:");
        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        log.error("Error handleFault ws...");

        SOAPMessage msg = context.getMessage();
        this.getMessageXML(msg, "ERROR Response:");

        return true;
    }

    @Override
    public void close(MessageContext context) {
        log.debug("close context");
    }

    private void getMessageXML(SOAPMessage msg, String msj) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            msg.writeTo(outputStream);
            log.info(msj + "[" + outputStream.toString() + "]");

        } catch (Exception ex) {
            log.error("Error al processar String XML: " + ex.getMessage());
        }
    }
}

