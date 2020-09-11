package co.moviired.supportp2pvalidatortransaction.common.provider;

public abstract class IProviderFactory<T> {

    protected T providerProperties;

    public IProviderFactory(T providerProperties) {
        this.providerProperties = providerProperties;
    }
}

