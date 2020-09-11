package co.moviired.supportp2pvalidatortransaction;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraProperties;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportProperties;
import co.moviired.supportp2pvalidatortransaction.common.service.IApplication;
import co.moviired.supportp2pvalidatortransaction.config.ComponentProperties;
import co.moviired.supportp2pvalidatortransaction.provider.supportsms.SupportSMSProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.validation.constraints.NotNull;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(value = {
        GlobalProperties.class,
        StatusCodeConfig.class,
        MahindraProperties.class,
        SchedulerSupportProperties.class,
        SupportSMSProperties.class,
        ComponentProperties.class
})
public class SupportP2pValidatorTransactionApplication extends IApplication implements ApplicationListener<ContextRefreshedEvent> {

    public SupportP2pValidatorTransactionApplication(@NotNull GlobalProperties config) {
        super(config);
    }

    public static void main(String[] args) {
        SpringApplication.run(SupportP2pValidatorTransactionApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@org.jetbrains.annotations.NotNull ContextRefreshedEvent event) {
        super.onApplicationEventBase(event);
    }
}
