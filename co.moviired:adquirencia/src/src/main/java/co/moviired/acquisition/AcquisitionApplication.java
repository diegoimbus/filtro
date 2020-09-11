package co.moviired.acquisition;

import co.moviired.acquisition.common.service.IApplication;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.provider.mahindra.MahindraProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportProperties;
import co.moviired.acquisition.config.ComponentProperties;
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
        ComponentProperties.class
})
// mvn sonar:sonar -Dsonar.projectKey=acquisition -Dsonar.host.url=http://localhost:9000 -Dsonar.login=4d04b43726f6b71ff37484f7bb049ef3c3ad7ae6
public class AcquisitionApplication extends IApplication implements ApplicationListener<ContextRefreshedEvent> {

    public AcquisitionApplication(@NotNull GlobalProperties config) {
        super(config);
    }

    public static void main(String[] args) {
        SpringApplication.run(AcquisitionApplication.class);
    }

    @Override
    public final void onApplicationEvent(@org.jetbrains.annotations.NotNull ContextRefreshedEvent event) {
        super.onApplicationEventBase(event);
    }
}

