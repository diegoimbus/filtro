package co.moviired.transpiler.conf.soap;

import org.springframework.ws.wsdl.wsdl11.provider.SuffixBasedPortTypesProvider;

import javax.wsdl.Message;

public class MySuffixBasedPortTypesProvider extends SuffixBasedPortTypesProvider {

    private String requestSuffix = DEFAULT_REQUEST_SUFFIX;

    @Override
    public final String getRequestSuffix() {
        return requestSuffix;
    }

    @Override
    public final void setRequestSuffix(String prequestSuffix) {
        this.requestSuffix = prequestSuffix;
    }

    @Override
    protected final String getOperationName(Message message) {
        String messageName = getMessage(message);
        String result = null;
        if (messageName != null) {
            if (messageName.endsWith(getResponseSuffix())) {
                result = messageName.substring(0, messageName.length() - getResponseSuffix().length());
            } else if (messageName.endsWith(getFaultSuffix())) {
                result = messageName.substring(0, messageName.length() - getFaultSuffix().length());
            } else if (messageName.endsWith(getRequestSuffix())) {
                result = messageName.substring(0, messageName.length() - getRequestSuffix().length());
            }
        }
        return result;
    }

    @Override
    protected final boolean isInputMessage(Message message) {
        String messageName = getMessage(message);

        return messageName != null && !messageName.endsWith(getResponseSuffix());
    }

    private String getMessage(Message message) {
        return message.getQName().getLocalPart();
    }

}

