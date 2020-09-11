package co.moviired.acquisition.common.provider;

import lombok.Data;

@Data
public abstract class IProviderFactory<T> {

    private T providerProperties;

    public IProviderFactory(T providerPropertiesI) {
        this.providerProperties = providerPropertiesI;
    }
}

