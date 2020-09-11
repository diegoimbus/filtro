package co.moviired.acquisition.common.model;

import co.moviired.base.helper.CommandHelper;
import co.moviired.acquisition.common.util.UtilsHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static co.moviired.acquisition.common.util.ConstantsHelper.EMPTY_STRING;

@Slf4j
public abstract class IModel implements Serializable {

    /**
     * This method return string with data of object
     *
     * @return to String of object in json format
     */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * This method return string to show in logs with sensible fields protected
     *
     * @return protected to String of object in json format
     */
    public String toJson(String... protectFields) {
        try {
            return UtilsHelper.protectFields(toJsonString(), protectFields);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return EMPTY_STRING;
        }
    }

    /**
     * This method return string to show in logs with sensible fields protected
     *
     * @return protected to String of object in xml format
     */
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

    /**
     * This method return string to show in logs with sensible fields protected
     *
     * @return protected to String of object in xml/json format
     */
    public abstract String protectedToString();
}

