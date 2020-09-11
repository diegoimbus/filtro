package co.moviired.transpiler.conf.soap;

import org.springframework.util.Assert;
import org.springframework.ws.wsdl.wsdl11.provider.SuffixBasedMessagesProvider;
import org.w3c.dom.Element;

public class MySuffixBasedMessagesProvider extends SuffixBasedMessagesProvider {

    private String requestSuffix = DEFAULT_REQUEST_SUFFIX;

    @Override
    public final String getRequestSuffix() {
        return this.requestSuffix;
    }

    @Override
    public final void setRequestSuffix(String prequestSuffix) {
        this.requestSuffix = prequestSuffix;
    }

    @Override
    protected final boolean isMessageElement(Element element) {
        if (isMessageElement0(element)) {
            String elementName = getElementName(element);
            Assert.hasText(elementName, "Element has no name");
            return elementName.endsWith(getResponseSuffix())
                    || (getRequestSuffix().isEmpty() ? Boolean.TRUE : elementName.endsWith(getRequestSuffix()))
                    || elementName.endsWith(getFaultSuffix());
        }
        return false;
    }

    private boolean isMessageElement0(Element element) {
        return "element".equals(element.getLocalName())
                && "http://www.w3.org/2001/XMLSchema".equals(element.getNamespaceURI());
    }
}

