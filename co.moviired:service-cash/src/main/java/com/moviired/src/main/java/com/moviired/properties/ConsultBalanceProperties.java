package com.moviired.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Data
@Component
@ConfigurationProperties(prefix = "endpoints.consult-balance")
public class ConsultBalanceProperties implements Serializable {

    private String url;
    private int connectionTimeout;
    private int readTimeout;


}

