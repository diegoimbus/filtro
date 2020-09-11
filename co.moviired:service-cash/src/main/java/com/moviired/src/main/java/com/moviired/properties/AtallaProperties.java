package com.moviired.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static com.moviired.helper.Constant.PREFIX_ATALLA;

@Data
@ConfigurationProperties(prefix = PREFIX_ATALLA)
public class AtallaProperties implements Serializable {

    private String host;
    private Integer port;
    private Integer timeOut;

    private String key;
    private String macLength1;
    private String macType6;
    private String dataTypeBinary;
    private String dataLengthPlaceHolder;
    private String dataPlaceHolder;
    private String macPlaceHolder;
    private String messageGenerateMac;
    private String messageValidateMac;

    private String regexGenerateMessageResponse;
    private String regexValidateMessageResponse;
    private String regexErrorMessageResponse;
}

