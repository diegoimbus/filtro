package co.moviired.acquisition.config;

import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportProperties;
import co.moviired.acquisition.common.provider.mahindra.MahindraProperties;
import co.moviired.acquisition.common.provider.mahindra.MahindraConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;

/**
 * This class defined the bean of connectors for connect with providers
 */
@Configuration
public class ClientsConnectors {

    /**
     * Api for connect with mahindra
     *
     * @param globalProperties   component global properties
     * @param mahindraProperties properties for provider
     * @return connector for use in invocation
     */
    @Bean(value = MAHINDRA_API)
    public MahindraConnector mahindraProviderConnector(@NotNull GlobalProperties globalProperties, @NotNull MahindraProperties mahindraProperties) {
        return new MahindraConnector(globalProperties, mahindraProperties);
    }

    /**
     * Api for connect with scheduler support
     *
     * @param globalProperties           component global properties
     * @param schedulerSupportProperties properties for provider
     * @return connector for use in invocation
     */
    @Bean(value = SCHEDULER_HELPER_API)
    public SchedulerSupportConnector schedulerSupportProviderConnector(@NotNull GlobalProperties globalProperties, @NotNull SchedulerSupportProperties schedulerSupportProperties) {
        return new SchedulerSupportConnector(globalProperties, schedulerSupportProperties);
    }
}

