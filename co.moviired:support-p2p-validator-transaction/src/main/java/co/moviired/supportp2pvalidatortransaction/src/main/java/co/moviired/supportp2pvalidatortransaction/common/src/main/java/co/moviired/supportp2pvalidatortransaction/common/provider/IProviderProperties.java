package co.moviired.supportp2pvalidatortransaction.common.provider;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class IProviderProperties implements Serializable {

    protected String name;
    protected String url;
    protected int connectionTimeout;
    protected int readTimeout;
}

