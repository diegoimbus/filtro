package co.moviired.digitalcontent.incomm.jpos;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.LogEvent;
import org.jpos.util.LogListener;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SimpleLogListener implements LogListener {

    private final String[] protectFields;

    public SimpleLogListener() {
        super();
        protectFields = new String[0];
    }

    public SimpleLogListener(String[] protectFieldsI) {
        protectFields = Arrays.copyOf(protectFieldsI, protectFieldsI.length);
    }

    public final synchronized LogEvent log(LogEvent ev) {
        final List<Object> payLoad = ev.getPayLoad();
        for (int i = 0; i < payLoad.size(); i++) {
            Object obj = payLoad.get(i);
            if (obj instanceof ISOMsg) {
                payLoad.set(i, protectFields((ISOMsg) ((ISOMsg) obj).clone()));
            }
        }
        log.debug(ev.toString());
        return ev;
    }

    private ISOMsg protectFields(ISOMsg message) {
        for (String protectField : protectFields) {
            try {
                Object field = message.getValue(protectField);
                if (field != null) {
                    if (field instanceof String) {
                        message.set(protectField, StringUtils.repeat("*", ((String) field).length()));
                    } else {
                        message.set(protectField, "[PROTECTED FIELD]");
                    }
                }
            } catch (ISOException ignored) {
                // nothing to do
            }
        }
        return message;
    }
}


