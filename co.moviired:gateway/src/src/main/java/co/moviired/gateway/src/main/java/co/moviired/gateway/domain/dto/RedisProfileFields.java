package co.moviired.gateway.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisProfileFields implements Serializable {

    private String profileName;
    private StringBuilder profilePaths;

}
