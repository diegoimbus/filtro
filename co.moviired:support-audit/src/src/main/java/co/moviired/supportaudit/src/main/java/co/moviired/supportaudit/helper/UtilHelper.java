package co.moviired.supportaudit.helper;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.audit.domain.dto.AuditDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class UtilHelper {

    private UtilHelper() {

    }

    public static AuditDto parseJsonToObject(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, AuditDto.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String parseObjectToJson(AuditDto object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}

