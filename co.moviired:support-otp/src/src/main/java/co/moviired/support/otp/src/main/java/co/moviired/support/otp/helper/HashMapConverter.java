package co.moviired.support.otp.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {

        String customerInfoJson = null;
        try {
            if (customerInfo != null) {
                customerInfoJson = new ObjectMapper().writeValueAsString(customerInfo);
            }
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {

        Map<String, Object> customerInfo = null;
        try {
            if (customerInfoJSON != null) {
                customerInfo = new ObjectMapper().readValue(customerInfoJSON, Map.class);
            }
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return customerInfo;
    }

}

