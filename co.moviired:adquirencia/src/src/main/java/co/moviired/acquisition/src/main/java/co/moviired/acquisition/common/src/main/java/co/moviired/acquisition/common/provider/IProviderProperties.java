package co.moviired.acquisition.common.provider;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class IProviderProperties implements Serializable {

    private String name;
    private String url;
    private int connectionTimeout;
    private int readTimeout;
}

