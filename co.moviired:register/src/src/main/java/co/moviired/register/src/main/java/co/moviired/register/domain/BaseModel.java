package co.moviired.register.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.utils.Log;

import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseModel implements Serializable {

    @Override
    public String toString() {
        try {
            return toJson();
        } catch (JsonProcessingException e) {
            Log.error(e.getMessage());
            return "";
        }
    }

    private String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

}

