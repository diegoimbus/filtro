package co.moviired.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

@Data
@ConfigurationProperties(value = "spring.cloud.gateway")
public class PathsProperties implements Serializable {

    private List<String> whiteList;

}

