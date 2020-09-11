package co.moviired.supportp2pvalidatortransaction.common.model;

import co.moviired.base.helper.CommandHelper;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.EMPTY_STRING;

@Slf4j
public abstract class IModel implements Serializable {

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson(String... protectFields) {
        try {
            return Utils.protectFields(toJsonString(), protectFields);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return EMPTY_STRING;
        }
    }

    public String toXml(String... protectFields) {
        try {
            return CommandHelper.printIgnore(toXmlString(), protectFields);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return EMPTY_STRING;
        }
    }

    private String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    private String toXmlString() throws JsonProcessingException {
        return new XmlMapper().writeValueAsString(this);
    }

    public abstract String protectedToString();
}

