package co.moviired.supportp2pvalidatortransaction.config;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraConnector;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraProperties;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportProperties;
import co.moviired.supportp2pvalidatortransaction.provider.supportsms.SupportSMSConnector;
import co.moviired.supportp2pvalidatortransaction.provider.supportsms.SupportSMSProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.SUPPORT_SMS_API;

@Configuration
public class ClientsConnectors {

    @Bean(value = MAHINDRA_API)
    public MahindraConnector mahindraProviderConnector(@NotNull GlobalProperties globalProperties, @NotNull MahindraProperties mahindraProperties) {
        return new MahindraConnector(globalProperties, mahindraProperties);
    }

    @Bean(value = SCHEDULER_HELPER_API)
    public SchedulerSupportConnector schedulerSupportProviderConnector(@NotNull GlobalProperties globalProperties, @NotNull SchedulerSupportProperties schedulerSupportProperties) {
        return new SchedulerSupportConnector(globalProperties, schedulerSupportProperties);
    }

    @Bean(value = SUPPORT_SMS_API)
    public SupportSMSConnector supportSMSProviderConnector(@NotNull GlobalProperties globalProperties, @NotNull SupportSMSProperties supportSMSProperties) {
        return new SupportSMSConnector(globalProperties, supportSMSProperties);
    }
}

